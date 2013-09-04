package com.latebutlucky.beemote_view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Hexagon  {
	
	private float vertices[] = {
			
			 0.5f,   0.0f, 0.0f,
			 0.25f,  0.5f, 0.0f,
			-0.25f,  0.5f, 0.0f,
			-0.5f,   0.0f, 0.0f,
			-0.25f, -0.5f, 0.0f,
			 0.25f, -0.5f, 0.0f 
			
//			 0.3f,  0.0f, 0.0f,
//			 0.15f,  0.3f, 0.0f,
//			-0.15f,  0.3f, 0.0f,
//			-0.30f,  0.0f, 0.0f,
//			-0.15f, -0.3f, 0.0f,
//			 0.15f, -0.3f, 0.0f 
		};
	
	private short[] indices = { 0, 1, 5, 
								1, 2, 5, 
								2, 4, 5,
								2, 3, 4};
	
	private float color[] ={1.0f, 1.0f, 0.0f, 0.0f};
	
	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	
	public Hexagon() {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}
	
	public Hexagon(float mVertices[], float mColor[]) {
		setVertices(mVertices);
		setColor(mColor);
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}
	
	public Hexagon(float rate) {
		int i = 0;
		while(i<this.vertices.length){
			this.vertices[i]=this.vertices[i]*rate;
			this.vertices[i+1]=this.vertices[i+1]*rate;
			i+=3;			
		}
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}
	
	public Hexagon(float x, float y) {
		for(int i = 0; i<this.vertices.length; i+=3){
			this.vertices[i]  += x;
			this.vertices[i+1]+= y;
			
			
		}		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColor4f(color[0],color[1],color[2],color[3]);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);		
	}
	
	public void setVertices(float mVertices[]){
		for(int i =0 ; i< mVertices.length  ; i++){
			this.vertices[i]=mVertices[i];
		}
	}
	
	public void setColor(float mColor[]){
		for(int i =0 ; i< mColor.length  ; i++){
			this.color[i]=mColor[i];
		}
	}	
	
	public void setColor(float R, float G, float B, float A){
		this.color[0]=R;
		this.color[1]=G;
		this.color[2]=B;
		this.color[3]=A;		
	}	
	
//	public void setHexagonSize(float rate){
////		int i = 0;
////		while(i<this.vertices.length){
////			this.vertices[i]=this.vertices[i]*rate;
////			this.vertices[i++]=this.vertices[i++]*rate;
////			i++;			
////		}
//		
//		this.vertices[0] =3.0f;
//		this.vertices[1] =0.0f;
//		this.vertices[3] =1.0f;
//		this.vertices[4] =2.0f;
//		this.vertices[6] =-2.0f;
//		this.vertices[7] =0.0f;
//		this.vertices[9] =-1.0f;
//		this.vertices[10] =-2.0f;
//		this.vertices[12] =1.0f;
//		this.vertices[13] =-2.0f;
//
//		this.color[0]  = 1.0f;
//		this.color[1]  = 1.0f;
//		this.color[2]  = 1.0f;
////		
////				 3.0f,  0.0f, 0.0f,
////				 1.0f,  2.0f, 0.0f,
////				-1.0f,  2.0f, 0.0f,
////				-2.0f,  0.0f, 0.0f,
////				-1.0f, -2.0f, 0.0f,
////				 1.0f, -2.0f, 0.0f, 	
////				 };
//		
//	}
	
}
