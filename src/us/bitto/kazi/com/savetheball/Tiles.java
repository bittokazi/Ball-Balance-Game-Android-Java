package us.bitto.kazi.com.savetheball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Tiles {
	float x;
	float y;
	int height;
	int width;
	float dy;
	boolean enable=true;
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public float getDy() {
		return dy;
	}
	public void setDy(float dy) {
		this.dy = dy;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public void draw(Canvas canvas, Paint paint) {
		paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
		canvas.drawRect(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), paint);
	}
	public void update() {
		if(this.getY()+this.getHeight()>0) this.setY(this.getY()+this.getDy());
		else this.setEnable(false);
	}
}
