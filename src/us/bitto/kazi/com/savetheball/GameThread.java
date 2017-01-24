package us.bitto.kazi.com.savetheball;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameThread extends SurfaceView implements Runnable, SensorEventListener {

	public Thread thread;
	public boolean run=true;
	public boolean left=false;
	Context context;
	Activity activity;
	int VIEW_WIDTH;
	int VIEW_HEIGHT;
	boolean init_h_w;
	private Canvas canvas;
	private SurfaceHolder surfaceHolder;
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    long tiles_frequency;
    long speed_up;
    float tiles_dy;
    int tiles_time;
    Paint paint;
    Bitmap bg[];
    float bg_x[];
    float bg_y[];
    long bg_change_time;
    public static boolean game_over=false;
    public static int Score=0;
    public static boolean pause=false;
    public static boolean start=false;
    public static boolean highscore=false;
    
    
    ArrayList<Tiles> tiles;
    ArrayList<Enemy> enemy;
    Ball ball;
    public BallTilesCollision btc;
    public BallEnemyCollision bec;
    
    SharedPreferences sharedPref;
	
	public GameThread(Context context, Activity activity) {
		super(context);
		this.context=context;
		this.activity=activity;
		init_h_w=true;
		bg=new Bitmap[2];
		bg_x=new float[2];
		bg_y=new float[2];
		
		game_over=false;
	    Score=0;
	    pause=false;
	    start=false;
	    highscore=false;
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sharedPref = context.getSharedPreferences("savetheball", Context.MODE_PRIVATE);
		surfaceHolder = getHolder();
		paint = new Paint();
		this.start();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
        	float gx = event.values[0];
        	if(gx>1 && (ball.getX()-ball.getRadious())>0 && !game_over) {
        		ball.setX((ball.getX()-(gx)));
        	}
        	else if(gx<1 && (ball.getX()+ball.getRadious())<VIEW_WIDTH && !game_over) {
        		ball.setX((ball.getX()-(gx)));
        	}
        }
	}

	@Override
	public void run() {
		while(run) {
			if(!run) Log.d("lola", "lola");
			draw();
		}
	}
	
	public void draw() {
		if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if(init_h_w) init_h_w(canvas.getWidth(), canvas.getHeight());
            //draw
            canvas.drawColor(Color.CYAN);
            
            for(int i=0; i<2; i++) {
    			canvas.drawBitmap(bg[i], bg_x[i], bg_y[i], null);
    		}
            
            if(!game_over) create_tiles();
            for(int i=0; i<this.tiles.size(); i++) {
            	this.tiles.get(i).draw(canvas, paint);
            }
            ball.draw(canvas, paint);
            for(int i=0; i<this.enemy.size(); i++) {
            	paint.setStrokeWidth(3);
            	paint.setColor(Color.YELLOW);
            	this.enemy.get(i).draw(canvas, paint);
            }
            paint.setStrokeWidth(5);
            paint.setColor(Color.GREEN);
            
            
            draw_text();
            /*
            final float testTextSize = 48f;
            paint.setTextSize(testTextSize);
            Rect bounds = new Rect();
            paint.getTextBounds("Score: "+Score, 0, ("Score: "+Score).length(), bounds);
            float desiredTextSize = testTextSize * 200f / bounds.width();
            paint.setTextSize(desiredTextSize);
            canvas.drawText("Score: "+Score, 5, 120, paint);*/
            
            
            
            //draw end
            //update
            update();
            //update end
            surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}
	public void update() {
		if(!game_over) {
			for(int i=0; i<this.tiles.size(); i++) {
				this.tiles.get(i).update();
	        }
			for(int i=0; i<this.tiles.size(); i++) {
				if(!this.tiles.get(i).isEnable()) {
					this.tiles.remove(i);
					i--;
				}
	        }
			update_bg();
		}
	}
	public void draw_text() {
		if(!game_over) {
			Paint textPaint = new Paint();
	        textPaint.setColor(Color.GREEN);
	        textPaint.setTextAlign(Align.CENTER);
	        //textPaint.setTypeface(font);
	        textPaint.setTextSize(50);
	        int xPos = (canvas.getWidth() / 2);
	        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
	        canvas.drawText("Score: "+Score, xPos, 120, textPaint);
		}
		else {
			Paint textPaint = new Paint();
			textPaint.setColor(Color.GREEN);
	        textPaint.setTextAlign(Align.CENTER);
	        //textPaint.setTypeface(font);
	        textPaint.setTextSize(100);
	        int xPos = (canvas.getWidth() / 2);
	        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
	        canvas.drawText("Game Over", xPos, yPos-150, textPaint);
	        canvas.drawText("Score: "+Score, xPos, yPos-150+120, textPaint);
	        if(this.highscore) {
	        	canvas.drawText("HighScore!!!", xPos, yPos-150+120+120, textPaint);
	        }
		}
	}
	private void init_h_w(int w, int h) {
		VIEW_WIDTH = w;
        VIEW_HEIGHT = h;
        init_h_w=false;
        tiles_time=1000;
        
        
        
        InputStream is;
		Bitmap bmpImage = null;
		try {
			is = context.getAssets().open("images/bg.png");
			bmpImage = BitmapFactory.decodeStream(is);
			bmpImage = Bitmap.createScaledBitmap(bmpImage, this.VIEW_WIDTH, this.VIEW_HEIGHT, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
        bg[0]=bmpImage;
        bg[1]=bmpImage;
        bg_x[0]=0;
        bg_y[0]=0;
        bg_x[1]=0;
        bg_y[1]=this.VIEW_HEIGHT;
        
        
        this.tiles=new ArrayList<Tiles>();
        this.tiles_frequency=System.currentTimeMillis();
        this.speed_up=System.currentTimeMillis();
        this.bg_change_time=System.currentTimeMillis();
        
        this.tiles_dy=(float)-3.0;
        
        Tiles t=new Tiles();
		t.setX(0);
		t.setY(this.VIEW_HEIGHT-150);
		t.setHeight(40);
		t.setWidth(randInt(90, VIEW_WIDTH/2));
		t.setDy(this.tiles_dy);
		this.tiles.add(t);
		left=false;
		
		ball=new Ball();
		ball.setRadious(20);
		ball.setX(ball.getRadious()+30);
		ball.setY(this.VIEW_HEIGHT-ball.getRadious()-150);
		ball.setDy((float)0.8);
		ball.setDx(0);
		
		this.enemy=new ArrayList<Enemy>();
		int wid=this.VIEW_WIDTH/8;
		int xx=0;
		for(int i=0; i<8; i++) {
			Enemy e=new Enemy();
			e.setX(xx);
			e.setY(0);
			e.setHeight(70);
			e.setWidth(wid);
			e.setInverted(true);
			xx=xx+wid;
			this.enemy.add(e);
		}
		xx=0;
		for(int i=0; i<8; i++) {
			Enemy e=new Enemy();
			e.setX(xx);
			e.setHeight(70);
			e.setWidth(wid);
			e.setY(this.VIEW_HEIGHT);
			xx=xx+wid;
			this.enemy.add(e);
		}
		
		
		btc=new BallTilesCollision(this.ball, this.tiles, this.Score);
		btc.start();
		bec=new BallEnemyCollision(this.ball, this.enemy, this.context);
		bec.start();
		
		mSensorManager.registerListener((SensorEventListener) this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	public void create_tiles() {
		if(System.currentTimeMillis()>=this.tiles_frequency+tiles_time) {
			if(left) {
				Tiles t=new Tiles();
				t.setX(0);
				t.setY(this.VIEW_HEIGHT);
				t.setHeight(40);
				t.setWidth(randInt(90, VIEW_WIDTH/2));
				t.setDy(this.tiles_dy);
				this.tiles.add(t);
				left=false;
			}
			else {
				Tiles t=new Tiles();
				int width=randInt(90, VIEW_WIDTH/2);
				t.setX(this.VIEW_WIDTH-width);
				t.setY(this.VIEW_HEIGHT);
				t.setHeight(40);
				t.setWidth(width);
				t.setDy(this.tiles_dy);
				this.tiles.add(t);
				left=true;
			}
			tiles_frequency=System.currentTimeMillis();
		}
		if(System.currentTimeMillis()>=this.speed_up+10000) {
			this.tiles_dy=this.tiles_dy-(float)0.5;
			for(int i=0; i<tiles.size(); i++) {
				tiles.get(i).setDy(this.tiles_dy);
			}
			if(this.tiles_time>400)this.tiles_time=tiles_time-50;
			this.speed_up=System.currentTimeMillis();
		}
	}
	public void update_bg() {
		for(int i=0; i<2; i++) {
			if(bg_y[i]+VIEW_HEIGHT==0) bg_y[i]=this.VIEW_HEIGHT-(float)3;
			else bg_y[i]=bg_y[i]-(float)1;
		}
	}
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	public void start() {
		thread=new Thread(this, "gamethread");
		thread.start();
	}
	public void destroy() throws InterruptedException {
		run=false;
	}
	public String read_mem() {
		String s = sharedPref.getString("savetheball", "0");
		return s;
	}
	public void write_mem(String s) {
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("savetheball", s);
		editor.commit();
	}
}
