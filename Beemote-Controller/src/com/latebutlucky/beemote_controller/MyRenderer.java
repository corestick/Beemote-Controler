package com.latebutlucky.beemote_controller;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.View;
import android.view.View.OnClickListener;


public class MyRenderer implements Renderer  {
	private float red = 0.5f;
	private float green = 0.0f;
	private float blue = 0.5f;
	public Hexagon[] hexagon;
	public Vector<CirclePoint> pointSet = new Vector<CirclePoint>();

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);

		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -10);
//		gl.glClearColor(red, green, blue, 1.0f);
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < hexagon.length; i++)
			hexagon[i].draw(gl);
	}
	

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
				100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		pointSet.add(new CirclePoint(-1.7f, -1.1f));			// 0		   		
		pointSet.add(new CirclePoint(-1.7f,  0.0f));			// 1
		pointSet.add(new CirclePoint(-1.7f,  1.1f));		// 2
		
		pointSet.add(new CirclePoint(-0.85f, -1.6f));		// 3
		pointSet.add(new CirclePoint(-0.85f, -0.55f));		// 4
		pointSet.add(new CirclePoint(-0.85f,  0.55f));		// 5
		pointSet.add(new CirclePoint(-0.85f,  1.6f));		// 6
		
		pointSet.add(new CirclePoint(0.0f, -2.2f));			// 7
		pointSet.add(new CirclePoint(0.0f, -1.1f));			// 8
		pointSet.add(new CirclePoint(0.0f,  0.0f));			// 9
		pointSet.add(new CirclePoint(0.0f,  1.1f));			// 10
		pointSet.add(new CirclePoint(0.0f,  2.2f));			// 11
		
		pointSet.add(new CirclePoint(0.85f, -1.6f));		// 12
		pointSet.add(new CirclePoint(0.85f, -0.55f));		// 13
		pointSet.add(new CirclePoint(0.85f,  0.55f));		// 14
		pointSet.add(new CirclePoint(0.85f,  1.6f));		// 15
		
		pointSet.add(new CirclePoint(1.7f, -1.1f));			// 16
		pointSet.add(new CirclePoint(1.7f,  0.0f));			// 17
		pointSet.add(new CirclePoint(1.7f,  1.1f));			// 18


		
		

		hexagon = new Hexagon[19];

		for(int i = 0; i<hexagon.length; i++){
			hexagon[i] = new Hexagon(pointSet.get(i).x,pointSet.get(i).y);
			hexagon[i].setColor(1,1,1,0);
		}

		gl.glClearColor(red, green, blue, 1.0f);// 1.0, 0.0, 0.0, 1.0
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}
	
	public void setColor(float r, float g, float b) {
		red = r;
		green = g;
		blue = b;
	}

}
