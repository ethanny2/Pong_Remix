package com.example.pong_game;

import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

public class Player_Rect {

	ShapeDrawable player;
	int x;
	int y = Pong_Activity.screenHeight / 2 - (PADDLE_HEIGHT / 2);
	int width = PADDLE_WIDTH;
	int height = PADDLE_HEIGHT;
	// Constants for making the right size paddle
	static final int PADDLE_WIDTH = 40;
	static final int PADDLE_HEIGHT = 210;
	boolean touched;
	final static String TAG = Player_Rect.class.getSimpleName();
	// Only used for AI, set to 0 initially, set by the difficulty option in the
	// menu
	static int REGULAR_VELOCITY = 0;
	static int AI_RESPONSE_VELOCITY = 0;
	int vy = REGULAR_VELOCITY;

	// Context and pong
	Context myContext;
	PongApplication pongApplication;

	// Add another boolean to indicate be passed in
	// indicates if instance created is an AI or a player
	boolean comp;
	// A boolean that indicates direction the the AI was moving as it calculated
	// the
	// response to the player, it should keep moving in that direction
	boolean UP;

	// This is dumb, another boolean to determine if the velocity changed from a
	// wall bounce,
	// thus you do NOT determine the direction the paddle moves based on where
	// the Ai calcualtion
	// left it
	boolean bounce;

	// LET'S SEE WHAT HAPPENS, dont think this does anything
	boolean DOWN_LOCK;
	boolean UP_LOCK;

	// For storing the temporary index that is the pointer position
	int tempPointerID;

	// The only thing the constructor really needs is the X position and the
	// comp boolean
	public Player_Rect(int x, boolean comp, Context context) {
		this.x = x;
		this.comp = comp;
		player = new ShapeDrawable(new RectShape());
		player.getPaint().setColor(Color.WHITE);
		player.setBounds(x, y, width + x, height + y);

		// Set the context and get the application class
		myContext = context;
		pongApplication = (PongApplication) context.getApplicationContext();

		// Set the options here, such as the difficulties
		// Check if the key even exists
		// if (pongApplication.getContentValueDiff().containsKey(
		// PongApplication.KEY_DIFFICULTY)) {
		// Even more Balancing tweaks for Landscape mode
		switch (pongApplication.getDifficulty()) {
		case MenuActivity.diff_easy:
			// Blah
			Log.d(TAG, "EASY MODE");
			if (myContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				REGULAR_VELOCITY = 8;
				AI_RESPONSE_VELOCITY = 8;
			} else {
				REGULAR_VELOCITY = 10;
				AI_RESPONSE_VELOCITY = 14;
			}
			break;

		case MenuActivity.diff_med:
			// blah
			Log.d(TAG, "MEDIUM MODE");
			REGULAR_VELOCITY = 10;
			AI_RESPONSE_VELOCITY = 17;
			break;

		case MenuActivity.diff_hard:
			// blah
			REGULAR_VELOCITY = 13;
			AI_RESPONSE_VELOCITY = 24;
			Log.d(TAG, "HARD MODE");
			break;

		default:
			// PUT easy things here.
			Log.d(TAG, "EASY MODE/DEFAULT");
			if (myContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				REGULAR_VELOCITY = 8;
				AI_RESPONSE_VELOCITY = 8;
			} else {
				REGULAR_VELOCITY = 10;
				AI_RESPONSE_VELOCITY = 14;
			}
			break;

		}
	}

	protected void draw(Canvas canvas) {
		player.setBounds(x, y, width + x, height + y);
		player.draw(canvas);
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
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

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	public ShapeDrawable getPlayer() {
		return player;
	}

	public void setPlayer(ShapeDrawable player) {
		this.player = player;
	}

	public boolean isComp() {
		return comp;
	}

	public void setComp(boolean comp) {
		this.comp = comp;
	}

	// Check if the users finger is with in the boundary of the rectangle
	// Origin of rectangle is the upper left hand point

	// && posX >= x Taken out of if statement to allow player to move the player
	// by touching any space on the rect or behind its height.

	public void handleTouch(int posX, int posY, int currentPointerId) {

		//Left Side
		if (this.x == MainGamePanel.PADDLE_X) {
			if (posX <= (x + this.getWidth())) {
				if (posY <= (y + this.getHeight()) && posY >= y) {
					setTouched(true);
					tempPointerID=currentPointerId;
				}else{
					setTouched(false);
				}
			}else{
				setTouched(false);
			}

			//Right side
		} else if (this.x == Pong_Activity.screenWidth - MainGamePanel.PADDLE_X
				- Player_Rect.PADDLE_WIDTH) {
			if (posX >= x) {
				if (posY <= (y + this.getHeight()) && posY >= y) {
					setTouched(true);
					tempPointerID=currentPointerId;
				}else{
					setTouched(false);
				}
			}else {
				setTouched(false);
			}

		}

		// if (posX <= (x + this.getWidth())) {
		// if (posY <= (y + this.getHeight()) && posY >= y) {
		// // Use the height and width to check if x,y coordinates of
		// // finger is in the boundary of the rectangle.
		// // Log.d(TAG, "Touched is true with the Player_Rect");
		// setTouched(true);
		// // If the rectangle was touched, it will store the index of the
		// // pointer that
		// // touched it
		// tempPointerID = currentPointerId;
		//
		// } else {
		// setTouched(false);
		// }
		//
		// } else {
		// setTouched(false);
		// }

	}

	// The initial AI movement is a simple up and down movement
	// for some reason the bounds don't work? Oh, don't keep intializing
	// vy=10, the velocity will never reverse.
	// Possibly take speed as a parameter for different
	// difficulty levels.
	// REFACTORED TO ONE METHOD TO KEEP LOGIC OUT OF PANEL CLASS AND INCREASE
	// ENCAPSULATION

	public void moveComputer() {
		this.y += vy;
		// Log.d(TAG,"Y coord: " + this.y );
		if (this.getY() <= 0) {
			vy *= -1;
		}
		if (this.getY() + this.height >= Pong_Activity.screenHeight) {
			vy *= -1;
		}

	}

	// The cloned ball reached a wall and has stopped
	// either passing in coordinates or passing the ball object.
	// Finish by destroying the reference and setting a boolean
	// IMPORTANT!!!- ACCESS THE CLONE PASS THROUGH THE PASSED IN GAMEBALL
	public void compterResponse(Ball gameBall) {
		if (gameBall.ai_calc) {
			Log.d(TAG, "Now starting Ai calulations");

			// Bounds placed up here, because the paddle should stop waiting
			// even if
			// it hits the ball ON THE WAY to reach the clone ball resting
			// place.
			if (gameBall.getX() + gameBall.getWidth() >= Pong_Activity.screenWidth
					|| gameBall.getX() <= 0) {

				Log.d(TAG, "CloneBall was nulled ");
				gameBall.cloneBall = null;
				gameBall.ai_calc = false;
				// Log.d(TAG,
				// "CloneBall was nulled and ai_calc is"
				// + gameBall.isAi_calc());
				// UP = false;
				bounce = false;
			}
			// Or if ball collides with the rectangle
			if (gameBall.x <= this.x + this.width) {
				if (gameBall.x + gameBall.width >= this.x) {
					if (gameBall.y <= this.y + this.height) {
						if (gameBall.y + gameBall.height > this.y) {
							Log.d(TAG, "CloneBall was nulled ");
							gameBall.cloneBall = null;
							gameBall.ai_calc = false;
							Log.d(TAG, "CloneBall was nulled and ai_calc is"
									+ gameBall.isAi_calc());
							// UP = false;
							bounce = false;

						}
					}
				}

			}

			// Ball is below paddle.
			// &&(this.y + height / 2)
			if (gameBall.cloneBall != null
					&& this.y + height / 2 < gameBall.cloneBall.getY()) {

				DOWN_LOCK = true;
				vy = AI_RESPONSE_VELOCITY;
				y += vy;
				UP = false;
				Log.d(TAG, "BALL IS BELOW PADDLE");

				// Stop at screen edge
				if (this.getY() <= 0) {
					this.y = 0;
				}
				if (this.getY() + this.height >= Pong_Activity.screenHeight) {
					this.y = Pong_Activity.screenHeight - height;
				}

				// When middle of the paddle reaches the cloned ball
				// position
				// change this to random
				// Set the bottom and top of the paddle as the bounds, but
				// it
				// can stop anywhere
				// inbetween
				// Random number generator for calculating where the AI wil
				// hit
				// the ball.
				Random rand = new Random();
				int randomStop = rand.nextInt(5) + 1;
				// Log.d(TAG, "Radnom stop is " + randomStop);
				// 1.75 was random stop,
				// height/ randomstop
				if (this.y + height / randomStop >= gameBall.cloneBall.getY()
						&& DOWN_LOCK) {
					// Stop the paddle
					Log.d(TAG, "Paddle stopped, BALL BELOW");
					vy = 0;
					DOWN_LOCK = false;
				}
			} // **End of ball above paddle

			// Ball is above paddle
			// && this.y + height / 2
			// ...cloneBall.getY()+cb.getHeight
			if (gameBall.cloneBall != null
					&& this.y + height / 2 > gameBall.cloneBall.getY()) {
				UP_LOCK = true;
				vy = AI_RESPONSE_VELOCITY;
				y -= vy;
				UP = true;
				Log.d(TAG, "BALL IS ABOVE PADDLE");
				// Stop at screen edge
				if (this.getY() <= 0) {
					this.y = 0;
				}
				if (this.getY() + this.height >= Pong_Activity.screenHeight) {
					this.y = Pong_Activity.screenHeight - height;
					Log.d(TAG, "Lower boundary X:" + this.x + "Y: " + this.y);
				}

				// Ai reaches ball
				// When middle of the paddle reaches the cloned ball
				// position
				// Random number generator for calculating where the AI wil
				// hit
				// the ball.

				Random rand2 = new Random();
				int randomStop2 = rand2.nextInt(5) + 1;
				if (this.y + height / 1.75 <= gameBall.cloneBall.getY()
						&& UP_LOCK) {
					Log.d(TAG, "Paddle stopped, BALL ");
					vy = 0;
					UP_LOCK = false;

				}
			}
		} else {
			// If the game ball has not passed in a true, ai_calc (meaning the
			// trajectory ball was generated on stopped) then the paddle just
			// moves up and down.
			// ANOTHER PROBLEM. If the ball is moving down, responds to a hit
			// from the player, it and goes all the way up, it should finish the
			// upward movement, not continue going down.

			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// If the AI is on the LOWER HALF OF SCREEN
			// downward velocity, else upward
			// Collision works, but velocity gets reset by if statement
			// therefore it does not bounce, when it reaches one side of the
			// wall.

			// Log.d(TAG, "Normal compution, there is no cloned ball");

			if (!bounce) {
				if (UP) {
					vy = -REGULAR_VELOCITY;
					// Log.d(TAG, "UP IS TRUE VELOCITY SHOULD GO UP");
				} else {
					vy = REGULAR_VELOCITY;
					// Log.d(TAG, "UP IS FALSE VELOCITY SHOULD GO DOWN");
				}
			}
			this.y += vy;

			if (this.getY() <= 0) {
				bounce = true;
				this.y = 0;
				vy *= -1;
				// Log.d(TAG, "VY: " + this.vy);
			}
			if (this.getY() + this.height >= Pong_Activity.screenHeight) {
				bounce = true;
				this.y = Pong_Activity.screenHeight - height;
				vy *= -1;
				// Log.d(TAG, "VY: " + this.vy);
			}

		}
	}

	public void topBottomBound() {
		// Stop at screen edge
		if (this.getY() <= 0) {
			// this.y = height;
			this.y = 0;
		}
		if (this.getY() + this.height >= Pong_Activity.screenHeight) {
			this.y = Pong_Activity.screenHeight - height;
		}

	}

	public int getTempPointerID() {
		return tempPointerID;
	}

	public void setTempPointerID(int tempPointerID) {
		this.tempPointerID = tempPointerID;
	}

}
