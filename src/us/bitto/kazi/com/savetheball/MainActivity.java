package us.bitto.kazi.com.savetheball;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.Visibility;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity implements Runnable {
	GameThread gamethread;
	PowerManager.WakeLock mWakeLock;
	SurfaceHolder surfaceHolder;
	Canvas canvas;
	SurfaceView sf;
	Thread thread;
	
	int VIEW_WIDTH;
	int VIEW_HEIGHT;
	boolean init_h_w=true;
	boolean run=true;
	Bitmap bg;
	ImageButton b1;
	ImageButton b2;
	boolean game_view=false;
	Context context;
	MainActivity ma;
	boolean highscore=false;
	
	SharedPreferences sharedPref;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gamethread=new GameThread(this.getApplicationContext(), this);
        context=this.getApplicationContext();
        ma=this;
        sharedPref = context.getSharedPreferences("savetheball", Context.MODE_PRIVATE);
        //this.setContentView(gamethread);
        this.setContentView(R.layout.front_page);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        init();
        this.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		if(game_view) {
			try {
				gamethread.btc.run=false;
				gamethread.bec.run=false;
				gamethread.destroy();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			try{
				Thread.sleep(200);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			this.setContentView(R.layout.front_page);
			init();
			game_view=false;
		}
		else if(highscore) {
			highscore=false;
			b1.setVisibility(View.VISIBLE);
			b2.setVisibility(View.VISIBLE);
		}
		else {
			System.exit(0);
		}
	}


	@Override
	protected void onPause() {
		gamethread.mSensorManager.unregisterListener(gamethread);
		super.onPause();
	}
	
    public void draw() {
    	if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if(init_h_w) init_h_w(canvas.getWidth(), canvas.getHeight());
            
            canvas.drawBitmap(bg, 0, 0, null);
            if(this.highscore) this.draw_highscore();
            surfaceHolder.unlockCanvasAndPost(canvas);
		}
    }
    
    private void init_h_w(int w, int h) {
		VIEW_WIDTH = w;
        VIEW_HEIGHT = h;
        init_h_w=false;  
        
        
        InputStream is;
		Bitmap bmpImage = null;
		try {
			is = this.getAssets().open("images/bg1.png");
			bmpImage = BitmapFactory.decodeStream(is);
			bmpImage = Bitmap.createScaledBitmap(bmpImage, this.VIEW_WIDTH, this.VIEW_HEIGHT, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bg=bmpImage;
    }


	@Override
	public void run() {
		while(run) {
			if(!game_view) draw();
		}
	}
	public void start() {
		thread=new Thread(this, "frontpage");
		thread.start();
	}
	public void init() {
		sf=(SurfaceView) this.findViewById(R.id.surfaceView1);
        surfaceHolder = sf.getHolder();
        b1=(ImageButton) this.findViewById(R.id.imageButton1);
        b2=(ImageButton) this.findViewById(R.id.imageButton2);
        b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				game_view=true;
				gamethread=new GameThread(context, ma);
				setContentView(gamethread);
			}
		});
        b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				highscore=true;
				b1.setVisibility(View.GONE);
				b2.setVisibility(View.GONE);
			}
		});
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
	public void draw_highscore() {
		Paint textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextAlign(Align.CENTER);
        //textPaint.setTypeface(font);
        textPaint.setTextSize(100);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
        canvas.drawText("HighScore", xPos, yPos-150, textPaint);
        textPaint.setTextSize(200);
        canvas.drawText(this.read_mem(), xPos, yPos-150+250, textPaint);
	}
}
