package us.bitto.kazi.com.savetheball;

import java.util.ArrayList;

import android.util.Log;

public class BallTilesCollision implements Runnable {
	Thread thread;
	public boolean run;
	ArrayList<Tiles> tiles;
	Ball ball;
	int score;
	boolean get_score=false;
	
	public BallTilesCollision (Ball ball, ArrayList<Tiles> tiles, int score) {
		this.tiles=tiles;
		this.ball=ball;
		run=true;
		this.score=score;
	}
	
	@Override
	public void run() {
		while(run) {
			boolean ground=false;
			if(GameThread.game_over) break;
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0; i<this.tiles.size(); i++) {
				if(tiles.get(i).getX()<ball.getX() && tiles.get(i).getX()+tiles.get(i).getWidth()>ball.getX()) {
					if(tiles.get(i).getY()<=ball.getY()+ball.getRadious() && tiles.get(i).getY()+tiles.get(i).getHeight()>=ball.getY()+ball.getRadious()) {
						
						ball.update(ball.getDx(),0);
						ball.setY(this.tiles.get(i).getY()-ball.getRadious());
						ground=true;
						if(this.get_score) {
							Log.d("thisis","score: "+score);
							this.score++;
							GameThread.Score++;
							this.get_score=false;
						}
					}
					else {
						//ball.update(ball.getDx(), ball.getDy());
					}
				}
				else {
					//ball.update(ball.getDx(), ball.getDy());
				}
				//this.tiles.get(i).update();
			}
			if(!ground) {
				ball.update(ball.getDx(), ball.getDy());
				this.get_score=true;
			}
		}
	}
	public void start() {
		thread=new Thread(this, "balltiles");
		thread.start();
	}
}
