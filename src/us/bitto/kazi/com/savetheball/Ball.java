package us.bitto.kazi.com.savetheball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
	float x;
	float y;
	float radious;
	float dx;
	float dy;
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
	public float getRadious() {
		return radious;
	}
	public void setRadious(int radious) {
		this.radious = radious;
	}
	public float getDx() {
		return dx;
	}
	public void setDx(float dx) {
		this.dx = dx;
	}
	public float getDy() {
		return dy;
	}
	public void setDy(float dy) {
		this.dy = dy;
	}
	public void draw(Canvas canvas, Paint paint) {
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLUE);
		canvas.drawCircle(this.getX(), this.getY(), this.getRadious(), paint);
	}
	public void update(float dx, float dy) {
		this.setX(this.getX()+dx);
		this.setY(this.getY()+dy);
	}
}