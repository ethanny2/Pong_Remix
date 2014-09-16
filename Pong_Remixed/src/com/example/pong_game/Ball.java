package com.example.pong_game;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer;
import android.util.Log;

public class Ball {
	final static String TAG = Ball.class.getSimpleName();
	ShapeDrawable ball;

	// Give the constructor a context so it can acess the application class and
	// the changes occur immidiately
	// get Application access
	PongApplication pongApplication;

	// Create a MediaPlayer that holds a reference to the Application classes
	// sound player.
	public MediaPlayer mySound;

	// Hold the context
	Context myContext;

	// A clone reference that is generated every time the a HUMAN hits the ball
	// A boolean to check if the boolean is a clone, or that actual game ball.
	// One more boolean that is checked when the the instance of the gameball is
	// passed to the instance of the enemy to calculate where it should move
	Ball cloneBall;
	boolean moving;
	boolean ai_calc;

	int x = Pong_Activity.screenWidth / 2;
	int y = Pong_Activity.screenHeight / 2;
	int width = 40;
	int height = 40;
	double vx = REGULAR_X_VELCOCITY;
	double vy = REGULAR_Y_VELOCITY;
	// Normally 12
	final static double REGULAR_X_VELCOCITY = 12;
	final static double REGULAR_Y_VELOCITY = 12;
	//1.05
	final static double LANDSCAPE_X_VELOCITY_RESPONSE=1.02;
	final static double POTRAIT_X_VELOCITY_RESPONSE=1.03;
	//Speed needs to differ for the Ball's velocity (its too slow)
	//when the Orientation is portrait, and the clone ball creation also needs to
	//be altered (cloneBall is too fast, makes the ball fly under the paddle)
	
	
	
	public Ball(Context context) {
		ball = new ShapeDrawable(new RectShape());
		ball.getPaint().setColor(Color.WHITE);
		ball.setBounds(x, y, x + width, y + height);
		myContext = context;
		pongApplication = (PongApplication) context.getApplicationContext();
		if (pongApplication.isSoundOn()) {
			mySound = pongApplication.getSoundPlayer();
			mySound = MediaPlayer.create(context, R.raw.pong_sound);
		}

	}

	public Ball(int x, int y) {
		this.x = x;
		this.y = y;
		ball = new ShapeDrawable(new RectShape());
		ball.getPaint().setColor(Color.WHITE);
		ball.setBounds(x, y, x + width, y + height);
	}

	public void draw(Canvas canvas) {
		ball.setBounds(x, y, x + width, y + height);
		ball.draw(canvas);
	}

	// Might have to pass in a Player_Rect, the other AI, need to figure out
	// when to
	// generate clone ball on after a point score.
	public void borderCollision(Player_Rect p1, Player_Rect p2) {
		// If the ball exists give it velocity,
		// and add the boundary collisions
		if (this.cloneBall != null) {
			if (this.isMoving()) {
				cloneBall.x += cloneBall.getVx();
				cloneBall.y += cloneBall.getVy();
				Log.d(TAG, "CloneBall is at X: " + this.cloneBall.x + "Y: "
						+ this.cloneBall.y);
				if (cloneBall.x + cloneBall.width >= Pong_Activity.screenWidth) {
					cloneBall.x = Pong_Activity.screenWidth - 40;
					cloneBall.vx = 0;
					cloneBall.vy = 0;
					Log.d(TAG, "CLONE_BALL STOPPED X: " + cloneBall.x + "Y: "
							+ cloneBall.y);
					this.setMoving(false);
					this.ai_calc = true;

				}

				else if (cloneBall.x <= 0) {
					cloneBall.x = 0;
					cloneBall.vx = 0;
					cloneBall.vy = 0;
					Log.d(TAG, "CLONE_BALL STOPPED X: " + cloneBall.x + "Y: "
							+ cloneBall.y);
					this.setMoving(false);
					this.ai_calc = true;
				}

				else if (cloneBall.y <= 0) {

					cloneBall.y = 40;
					cloneBall.vy *= -1;
				}

				else if (cloneBall.y + cloneBall.height >= Pong_Activity.screenHeight) {
					cloneBall.y = Pong_Activity.screenHeight - 40;
					cloneBall.vy *= -1;
				}
			}
		}

		// Set the velocity of the x and y coordinates
		// call before drawing the rectangle in the MainGamePanel.
		x += vx;
		y += vy;

		Log.d(TAG, "REG_BALL is at X: " + this.x + "Y: " + this.y);

		// Right side, negative velocity.
		// Boundary collision, bounce back.
		// + width Removed to make ball go off screen fully.
		if (x >= Pong_Activity.screenWidth) {
			// x = Pong_Activity.screenWidth - 40;
			// vx *= -1;
			// Gotta null the ball and stop the ai response.
			this.cloneBall = null;
			this.ai_calc = false;

			x = Pong_Activity.screenWidth / 2;
			y = Pong_Activity.screenHeight / 2;
			vx = -REGULAR_X_VELCOCITY+5;
			vy=REGULAR_Y_VELOCITY-2;
			// Check if the passed in player is an ai, and which side it
			// is on, to determine if it the computer's turn to serve

			// This means left side scored yo
			pongApplication.addLeftScore();

			// Check if the score was by the player, then add one to consec_hits
			// in this case check if player is left, score was on right side
			if (pongApplication.isLeft() && !pongApplication.isMultiplayer()) {
				pongApplication.incrementHits();
			}

			// WHEN TO GENERATE CLONE BALL?
			if (!pongApplication.isLeft()) {
			//	makeCloneBall(x, y);
				makeFastCloneBall(x, y);
			}

		}

		// Left side, positive velocity
		// reset ball coordinates in the middle with
		// + width added to make ball go off screen fully.
		if (x + width <= 0) {
			// x = 40;
			// vx *= -1;
			// Gotta null the ball and stop the ai response.
			this.cloneBall = null;
			this.ai_calc = false;

			x = Pong_Activity.screenWidth / 2;
			y = Pong_Activity.screenHeight / 2;
			vx = REGULAR_X_VELCOCITY-7;
			vy=REGULAR_Y_VELOCITY-2;
			// Add score to the right
			pongApplication.addRightScore();

			// Check if the score was by the player, then add one to consec_hits
			// in this case check if player is right, score was on left side
			if (!pongApplication.isLeft() && !pongApplication.isMultiplayer()) {
				pongApplication.incrementHits();
			}

			// WHEN TO GENERATE CLONE BALL?
			if (pongApplication.isLeft()) {
				//makeCloneBall(x, y);
				makeFastCloneBall(x, y);
			}

		}

		if (y <= 0) {
			y = 40;
			vy *= -1;
		}

		if (y + height >= Pong_Activity.screenHeight) {
			y = Pong_Activity.screenHeight - 40;
			vy *= -1;
		}
		// Log.d(TAG, "The ball is currenly at  X: " + this.x + "Y: " + this.y);
	}

	// NEED TO ADJUST THE COLLSION SO THE BALL DOES NOT GET STUCK.
	public void rectangleCollison(Player_Rect player) {
		if (this.x <= player.getX() + player.getWidth()) {
			if (this.x + width >= player.getX()) {
				if (this.y <= player.y + player.getHeight()) {
					if (this.y + height >= player.getY()) {
						// Touches the Paddle make a beeping sound
						if (pongApplication.isSoundOn()) {
							mySound.start();
						}
						
						
						
						//Resetting the x coordinate for the left paddle
						if(player.getX()==MainGamePanel.PADDLE_X ){
							if(this.x<=player.getX()+player.getWidth()){
								Log.d(TAG, "LEFT BOUND CALLED");
								this.x=player.getX()+player.getWidth();
							}
							//INCLUDED
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}
						}
						
						
						//Resetting the x coordinate for the right paddle, 
						 if(player.getX()>=Pong_Activity.screenWidth - MainGamePanel.PADDLE_X - Player_Rect.PADDLE_WIDTH){
							Log.d(TAG, "RIGHT SIDE BOUND FIX");
							if(this.getX()+this.width>=player.getX()){
								this.x=player.getX()-width;
							}
							//INCLUED
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}
						}
					
						// AREA 1 OF 5 UPPER REGION, MOST UPWARD VELOCITY
						if (this.y + height <= (player.getY() + player
								.getHeight() / 5)
								&& this.y + height >= player.getY()) {

//							vx *= -1;
//							vy = -REGULAR_Y_VELOCITY - 4;
							//Faster ball when the screen is in landscape, or else the game is too slow
							if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
								vx*=-LANDSCAPE_X_VELOCITY_RESPONSE;
								vy = -REGULAR_Y_VELOCITY - 4;
								
							}else{
								//vx *= -1;
								vx*=-POTRAIT_X_VELOCITY_RESPONSE;
								vy = -REGULAR_Y_VELOCITY - 4;
								
							}
							

							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}
						}

						// AREA 2 OF 5 UPPER REGION, LESS UPWARD VELOCITY
						if (this.y + height <= player.getY()
								+ (2 * player.getHeight() / 5)
								&& this.y + height >= player.getY()
										+ player.getHeight() / 5) {
//							vx *= -1;
//							vy = -REGULAR_Y_VELOCITY;
							//Faster ball when the screen is in landscape, or else the game is too slow
							if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
								vx*=-LANDSCAPE_X_VELOCITY_RESPONSE;
								vy = -REGULAR_Y_VELOCITY;
							}else{
								//vx *= -1;
								vx*=-POTRAIT_X_VELOCITY_RESPONSE;
								vy = -REGULAR_Y_VELOCITY+6;
							}
							
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}

						}

						// AREA 3 OF 5 MIDDLE REGION, FORWARD/REGULAR VELOCITY
						if (this.y + height <= player.getY()
								+ (3 * player.getHeight() / 5)
								&& this.y + height >= player.getY()
										+ (2 * player.getHeight() / 5)) {
							
							//vx *= -1;
							//vy = 0.6;
							//Faster ball when the screen is in landscape, or else the game is too slow
							if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
								vx*=-LANDSCAPE_X_VELOCITY_RESPONSE-0.10;
								vy = 0.6;
								
							}else{
								//vx *= -1;
								vx*=-POTRAIT_X_VELOCITY_RESPONSE;
								vy = 0.6;
								
							}
							
							
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}
						}

						// AREA 4 OF 5 UPPER REGION, LESS DOWNWARD VELOCITY
						if (this.y + height <= player.getY()
								+ (4 * player.getHeight() / 5)
								&& this.y + height >= player.getY()
										+ (3 * player.getHeight() / 5)) {

//							vx *= -1;
//							vy = REGULAR_Y_VELOCITY - 4;
							//Faster ball when the screen is in landscape, or else the game is too slow
							if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
								vx*=-LANDSCAPE_X_VELOCITY_RESPONSE;
								vy = REGULAR_Y_VELOCITY - 4;
								
							}else{
								//vx *= -1;
								vx*=-POTRAIT_X_VELOCITY_RESPONSE;
								vy = REGULAR_Y_VELOCITY - 6;
							}
							
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}
						}

						
						//EXTRA COLLISION BETWEEN THE 4 AND 5TH AREA, IT GETS STUCK
						if(this.y>=player.y+ 3*player.getHeight()/5 && this.y<= player.getY()+ 4*player.getHeight()/5
								&& this.y+height>=player.getY()+4*player.getHeight()/5 && this.y+height<=player.getY()+player.getHeight()){
								
//							vx *= -1;
//							vy = REGULAR_Y_VELOCITY - 2;
							//Faster ball when the screen is in landscape, or else the game is too slow
							if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
								vx*=-LANDSCAPE_X_VELOCITY_RESPONSE;
								vy = REGULAR_Y_VELOCITY - 2;
								
							}else{
								//vx *= -1;
								vx*=-POTRAIT_X_VELOCITY_RESPONSE;
								vy = REGULAR_Y_VELOCITY - 2;
							}
							
							
							
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}

							
							
						}
						
						
						
						// AREA 5 OF 5 UPPER REGION, MOST DOWNWARD VELOCITY
						if (this.y >= player.getY()
								+ (4 * player.getHeight() / 5)
								&& this.y <= (player.getY() + player
										.getHeight())) {
							
//							
//							vx *= -1;
//							vy = REGULAR_Y_VELOCITY;
							//Faster ball when the screen is in landscape, or else the game is too slow
							if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
								vx*=-LANDSCAPE_X_VELOCITY_RESPONSE;
								vy = REGULAR_Y_VELOCITY;
								
							}else{
								//vx *= -1;
								vx*=-POTRAIT_X_VELOCITY_RESPONSE;
								vy = REGULAR_Y_VELOCITY;
							}
							if (!player.isComp()) {
								makeCloneBall(this.getX(), this.getY());
							}		
							
						}

						// // WORKING CODE!
						// // Upper collision bounces up, vy=negative
						// if (this.y + height <= player.getY()
						// + player.getHeight() / 5) {
						// // BOUND
						// y = player.getHeight() / 5 + player.getY()
						// - this.height;
						// vx *= -1;
						// vy = -REGULAR_Y_VELOCITY;
						// // Ball clone is created if the passed in instance
						// // is a not
						// // a computer, velocity is doubled and the boolean
						// // to
						// // check if it is a clone is set to true
						// // Ohhh, need to set X,Y or else the new instance
						// // of cloneBall will just keep using the default
						// // value
						// // of X and Y.
						// if (!player.isComp()) {
						// makeCloneBall(this.getX(), this.getY());
						// }
						// }
						// Bottom Collision, vy goes downwards=positive
						// else if (this.y >= player.getY() + 4
						// * player.getHeight() / 5
						// && this.y + height <= (player.getY() + player
						// .getHeight())) { // Bottom collison
						// vx *= -1;
						// vy = REGULAR_Y_VELOCITY;
						// if (!player.isComp()) {
						// makeCloneBall(this.getX(), this.getY());
						// }
						//
						// } else {
						// // middle collision ball, ball goes relatively
						// // straight, vy approaches 0.
						// vx *= -1;
						// vy *= 0.6;
						//
						// if (!player.isComp()) {
						// makeCloneBall(this.getX(), this.getY());
						// }
						//
						// }

					}
				}

			}

		}
		// Adding a callback function to detect if the sound is stopped
		if (pongApplication.isSoundOn()) {
			mySound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.d(TAG, "Sound has finished playing");
					// After completion set the sound track back to 0. Restart
					// it.
					mp.seekTo(0);
				}
			});
		}

	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getVx() {
		return vx;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isAi_calc() {
		return ai_calc;
	}

	public void setAi_calc(boolean ai_calc) {
		this.ai_calc = ai_calc;

	}

	public void makeCloneBall(int x, int y) {
		//Make the clone ball go slower so the paddle has a greater chance to hit while moving
		if(myContext.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
			cloneBall = new Ball(x, y);
			cloneBall.vx = this.getVx() *1.6;
			cloneBall.vy = this.getVy() *1.6;
			//1.2
			this.setMoving(true);
			Log.d(TAG, "LANDSCAPE CLONEBALL CREATED ");		
		}else{
		cloneBall = new Ball(x, y);
		cloneBall.vx = this.getVx() *2.3;
		cloneBall.vy = this.getVy()*2.3 ;
		//2.4
		this.setMoving(true);
		Log.d(TAG, "Clone ball created ");
		}
	}
	
	public void makeFastCloneBall(int x, int y){
		cloneBall = new Ball(x, y);
		cloneBall.vx = this.getVx() *2.3;
		cloneBall.vy = this.getVy() *2.3;
		//2.4
		this.setMoving(true);
		Log.d(TAG, "Clone ball created ");
		
	}
	
	

}
