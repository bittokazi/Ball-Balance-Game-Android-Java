package us.bitto.kazi.com.savetheball;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class BallEnemyCollision implements Runnable {
	Thread thread;
	public boolean run;
	ArrayList<Enemy> enemy;
	Ball ball;
	Context context;
	SharedPreferences sharedPref;
	
	public BallEnemyCollision(Ball ball, ArrayList<Enemy> enemy, Context context) {
		this.ball=ball;
		this.enemy=enemy;
		run=true;
		this.context=context;
		sharedPref = context.getSharedPreferences("savetheball", Context.MODE_PRIVATE);
	}
	
	@Override
	public void run() {
		while(run) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0; i<enemy.size(); i++) {
				if(enemy.get(i).getX()<ball.getX() && enemy.get(i).getX()+enemy.get(i).getWidth()>ball.getX()) {
					if(enemy.get(i).isInverted() && enemy.get(i).getY()<=ball.getY()+ball.getRadious()+ball.getDy() && enemy.get(i).getY()+enemy.get(i).getHeight()>=ball.getY()+ball.getRadious()+ball.getDy()) {
						Log.d("game_over","game_over");
						GameThread.game_over=true;
						run=false;
						if(Integer.parseInt(this.read_mem())<GameThread.Score) {
							GameThread.highscore=true;
							this.write_mem(Integer.toString(GameThread.Score));
						}
					}
					else if(!enemy.get(i).isInverted() && enemy.get(i).getY()-enemy.get(i).getHeight()+15<=ball.getY()+ball.getRadious()&& enemy.get(i).getY()>=ball.getY()+ball.getRadious()) {
						Log.d("game_over","game_over");
						GameThread.game_over=true;
						if(Integer.parseInt(this.read_mem())<GameThread.Score) {
							GameThread.highscore=true;
							this.write_mem(Integer.toString(GameThread.Score));
						}
						run=false;
					}
				}
			}
		}
	}
	public void start() {
		thread=new Thread(this, "ballenemy");
		thread.start();
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
