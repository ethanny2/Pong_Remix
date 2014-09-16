package com.example.pong_game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	// Coordinates +-50 from borders is a good x value to start,
	// paddle placed in middle.
	// Left side
	public static final int PADDLE_X = 50;

	 int leftX = PADDLE_X;
	 int rightX = Pong_Activity.screenWidth - PADDLE_X
			- Player_Rect.PADDLE_WIDTH;
	private gameThread thread;
	private static final String TAG = MainGamePanel.class.getSimpleName();
	private Player_Rect rectPlayer;
	private Ball gameBall;
	private Player_Rect otherPlayer;
	LayoutInflater inflater;
	PongApplication pongApplication;
	// Reference
	Context myContext;
	// Constants for the position of the score strings
	// float leftScoreX = Pong_Activity.screenWidth / 2 - 150;
	float leftScoreX;
	static final float leftScoreY = 175;
	// float rightScoreX = Pong_Activity.screenWidth / 2 + 75;
	float rightScoreX;
	static final float rightScoreY = 175;
	// The color for NOW is white
	Paint displayText = new Paint();
	// Create a boolean to hold the isComp() input that will be passed into the
	// computer
	boolean computerPlayer;

	// This should not really be here
	Bitmap original;
	Bitmap background;

	// Reference to the activity that holds the surface view
	Pong_Activity hostActivity;

	public MainGamePanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		// Use context to get settings and apply them
		pongApplication = (PongApplication) context.getApplicationContext();
		// Create the thread for game loop

		thread = new gameThread(getHolder(), this);
		Log.d(TAG, "New thread created");

		// Hold context
		myContext = context;

		// Also set the X position's of the Score again, or it will be
		// misaligned on rotation
		leftScoreX = Pong_Activity.screenWidth / 2 - 150;
		rightScoreX = Pong_Activity.screenWidth / 2 + 75;
		// Use context to get hosting activity, context can be cast to activity
		hostActivity = (Pong_Activity) context;

		// gameBall = new Ball(context);
		original = BitmapFactory.decodeResource(getResources(),
				R.drawable.pong_background);
		background = Bitmap.createScaledBitmap(original,
				Pong_Activity.screenWidth, Pong_Activity.screenHeight, false);
		// Set the color of the paint and stuff
		displayText.setColor(Color.WHITE);
		displayText.setTextSize(150);

		// Dont create a new ball if the thread was paused from during the
		// rotation
		if (pongApplication.getMainBall() == null) {
			gameBall = new Ball(context);
		} else {
			gameBall = pongApplication.getMainBall();
		}

		setFocusable(true);
		Log.d(TAG, "mainGame Panel, thread created");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "Surface Created called in MainGamePanel");
		// Put the rectangle creation is on surfaceCreated() because it is
		// called each time the user enters the game.

		if (pongApplication.isLeft() && !pongApplication.isMultiplayer()) {
			computerPlayer = true;
			rectPlayer = new Player_Rect(leftX, false, myContext);
			otherPlayer = new Player_Rect(rightX, computerPlayer, myContext);
			if (pongApplication.getLeft_player_y() > 0
					|| pongApplication.getRight_player_y() > 0) {
				rectPlayer.setY(pongApplication.getLeft_player_y());
				otherPlayer.setY(pongApplication.getRight_player_y());
			}
			Log.d(TAG,
					"Left Y pos after rotation is "
							+ pongApplication.getLeft_player_y());

			// Player is on the right side
		} else if (!pongApplication.isLeft()
				&& !pongApplication.isMultiplayer()) {
			computerPlayer = true;
			rectPlayer = new Player_Rect(rightX, false, myContext);
			otherPlayer = new Player_Rect(leftX, computerPlayer, myContext);
			if (pongApplication.getLeft_player_y() > 0
					|| pongApplication.getRight_player_y() > 0) {
				otherPlayer.setY(pongApplication.getLeft_player_y());
				rectPlayer.setY(pongApplication.getRight_player_y());
			}

		} else if (pongApplication.isMultiplayer()) {
			// sides don't matter here, but rectPlayer is LEFT
			// ** rectPlayer is set left
			pongApplication.setLeft(true);
			computerPlayer = false;
			rectPlayer = new Player_Rect(leftX, false, myContext);
			otherPlayer = new Player_Rect(rightX, computerPlayer, myContext);
			if (pongApplication.getLeft_player_y() > 0
					|| pongApplication.getRight_player_y() > 0) {
				rectPlayer.setY(pongApplication.getLeft_player_y());
				otherPlayer.setY(pongApplication.getRight_player_y());
			}

		}
		Log.d(TAG, "SURFACE CREATED CALLED!");

		if (!thread.running) {
			thread.setRunning(true);
			thread.start();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Let the first index in the 0th position (always) be the
		// rectPlayer (Left side) and let the 1st index position
		// be the otherPlayer (right side)

		int maskedAction = event.getActionMasked();
		switch (maskedAction) {
		// First case is primary finger/touch going down,
		// and the second is for the other touch
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			// rectPlayer.handleTouch((int) event.getX(), (int) event.getY());
			// if (event.getActionIndex() == 1 && !computerPlayer) {
			// otherPlayer.handleTouch(
			// (int) event.getX(event.getActionIndex()),
			// (int) event.getY(event.getActionIndex()));
			// }

			// if(event.getActionIndex()==0){
			int curIndex = event.getActionIndex();
			if (!rectPlayer.isTouched()) {
				rectPlayer.handleTouch((int) event.getX(curIndex),
						(int) event.getY(curIndex),
						event.getPointerId(curIndex));

			}
			if (!otherPlayer.isTouched()) {
				otherPlayer.handleTouch((int) event.getX(curIndex),
						(int) event.getY(curIndex),
						event.getPointerId(curIndex));

			}

			// }
			// if(event.getActionIndex()==1 && !computerPlayer){
			// int curIndex= event.getActionIndex();
			// rectPlayer.handleTouch((int) event.getX(curIndex), (int)
			// event.getY(curIndex), curIndex);
			// otherPlayer.handleTouch((int) event.getX(curIndex), (int)
			// event.getY(curIndex), curIndex);

			// }

			break;

		// Both the primary and secondary finger use the
		// same constant to signal that the finger moved on the screen
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < event.getPointerCount(); i++) {

				// if (i == 0) {
				// if (rectPlayer.isTouched()) {
				// rectPlayer.setY((int) event.getY(i));
				//
				// }
				// }
				//
				// if (i == 1 && !computerPlayer) {
				// if (otherPlayer.isTouched()) {
				// otherPlayer.setY((int) event.getY(i));
				//
				// }
				// }
				//

				if (rectPlayer.getTempPointerID() == event.getPointerId(i)) {
					if (rectPlayer.isTouched()) {
						rectPlayer.setY((int) event.getY(i));
						Log.d(TAG, "RectPlayer is being moved");
					}

				}

				if (otherPlayer.getTempPointerID() == event.getPointerId(i)
						&& !computerPlayer) {
					if (otherPlayer.isTouched()) {
						otherPlayer.setY((int) event.getY(i));
						Log.d(TAG, "otherPlayer is being moved");
					}

				}

			}

			break;

		// The first case is for the initial touch lifting off
		// And the second case is for the other touches.
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// if (event.getActionIndex() == 0){
			// if (rectPlayer.isTouched()) {
			// rectPlayer.setTouched(false);
			// }
			// }
			//
			// if (event.getActionIndex() == 1 && !computerPlayer) {
			// if (otherPlayer.isTouched()) {
			// otherPlayer.setTouched(false);
			// }
			// }
			for (int i = 0; i < event.getPointerCount(); i++) {

				if (rectPlayer.getTempPointerID() == event.getPointerId(i)) {
					if (rectPlayer.isTouched()) {
						rectPlayer.setTouched(false);
					}

				}

				if (otherPlayer.getTempPointerID() == event.getPointerId(i)
						&& !computerPlayer) {
					if (otherPlayer.isTouched()) {
						otherPlayer.setTouched(false);
					}

				}

			}

			break;
		}

		// ***THIS CODE WORKED****

		// // If the finger is on the screen
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// rectPlayer.handleTouch((int) event.getX(), (int) event.getY());
		// Log.d(TAG, "Coordinates X: " + event.getX() + "Y: " + event.getY());
		// }
		// // The finger is on the rectangle to drag
		// if (event.getAction() == MotionEvent.ACTION_MOVE) {
		// if (rectPlayer.isTouched()) {
		// Log.d(TAG, "The rectangle was touched.");
		// // In bounds of rectangle can be dragged.
		// // Update only the Y value, this is PONG baby.
		// rectPlayer.setY((int) event.getY());
		//
		// }
		// }
		//
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// // Player let go
		// if (rectPlayer.isTouched()) {
		// rectPlayer.setTouched(false);
		// }
		// }

		// Do the same thing for otherPlayer, but check if it is a
		// human first

		// if (!computerPlayer) {
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// otherPlayer.handleTouch((int) event.getX(), (int) event.getY());
		// Log.d(TAG,
		// "Coordinates X: " + event.getX() + "Y: " + event.getY());
		// }
		// // The finger is on the rectangle to drag
		// if (event.getAction() == MotionEvent.ACTION_MOVE) {
		// if (otherPlayer.isTouched()) {
		// Log.d(TAG, "The rectangle was touched.");
		// // In bounds of rectangle can be dragged.
		// // Update only the Y value, this is PONG baby.
		// otherPlayer.setY((int) event.getY());
		//
		// }
		// }
		//
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// // Player let go
		// if (otherPlayer.isTouched()) {
		// otherPlayer.setTouched(false);
		// }
		// }
		//
		// }

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Check for the win conditions

		hostActivity.winConditions();

		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawText(String.valueOf(pongApplication.getLeftScore()),
				leftScoreX, leftScoreY, displayText);
		canvas.drawText(String.valueOf(pongApplication.getRightScore()),
				rightScoreX, rightScoreY, displayText);
		// Log.d(TAG, "Left side score is " + pongApplication.getLeftScore());
		rectPlayer.topBottomBound();
		otherPlayer.topBottomBound();
		gameBall.borderCollision(otherPlayer, rectPlayer);
		gameBall.rectangleCollison(rectPlayer);
		gameBall.rectangleCollison(otherPlayer);
		if (computerPlayer) {
			otherPlayer.compterResponse(gameBall);
		}
		if (gameBall.cloneBall != null) {
			gameBall.cloneBall.draw(canvas);
		}
		// rectPlayer.topBottomBound();
		// otherPlayer.topBottomBound();
		gameBall.draw(canvas);
		rectPlayer.draw(canvas);
		otherPlayer.draw(canvas);

	}

	public gameThread getThread() {
		return thread;
	}

	public void setThread(gameThread thread) {
		this.thread = thread;
	}

	@SuppressLint("WrongCall")
	public class gameThread extends Thread {
		boolean running = false;
		private SurfaceHolder surfaceHolder;
		private MainGamePanel gamePanel;
		private Canvas canvas;
		private static final String TAG = "gameThread";
		boolean paused = false;
		Object pauseLock = new Object();

		public gameThread(SurfaceHolder holder, MainGamePanel panel) {
			super("Gamethread");
			this.surfaceHolder = holder;
			this.gamePanel = panel;

		}

		public void setRunning(boolean running) {
			this.running = running;
		}

		public boolean isPaused() {
			return paused;
		}

		// Some methods to handle pausing and resuming the thread
		public void onResume() {
			synchronized (pauseLock) {
				paused = false;
				pauseLock.notifyAll();
			}

		}

		public void onPause() {
			synchronized (pauseLock) {
				paused = true;
			}

		}

		@Override
		public void run() {

			Log.d(TAG, "Starting the game loop through game thread class.");
			Log.d(TAG, "Running variable is " + running);
			// I guess there is some glitch were the thread is stopped, but it
			// still calls once even after the surface is destroyed
			// Just do some null checking

			while (running) {
				synchronized (pauseLock) {
					while (paused) {
						try {
							pauseLock.wait();
						} catch (InterruptedException e) {
							Log.d(TAG,
									"Gamethread was interrupted during pause.");
						}
					}

				}
				// End of Pausing code
				canvas = null;
				try {
					canvas = this.surfaceHolder.lockCanvas();
					// One accessible by one thread at a time
					synchronized (surfaceHolder) {
						// Update the canvas by calling surface view methods.
						if (canvas != null) {
							this.gamePanel.onDraw(canvas);
						}
					}

				} finally {
					// If canvas exists reupdate all the drawn objects
					// to the canvas through
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}

			}

			// Some code to safely pause the code and, not destroy the thread,
			// but cause it to PAUSE it, when the user presses the home button,
			// if
			// the use has pressed the back button they have the intention of
			// leaving
			// that screen, meaning the activity with this SurfaceView as a view
			// will be destroyed as well, and you should shut the thread down in
			// those
			// Overridden methods of the activity.

		}

	}

	/*
	 * Method used to store the data for the paddles and the ball to be returned
	 * when theSurfaceView is recreated after a rotation
	 */
	public void saveData(Player_Rect left, Player_Rect right) {
		pongApplication.setMainBall(gameBall);

	}

	public Player_Rect getRectPlayer() {
		return rectPlayer;
	}

	public void setRectPlayer(Player_Rect rectPlayer) {
		this.rectPlayer = rectPlayer;
	}

	public Player_Rect getOtherPlayer() {
		return otherPlayer;
	}

	public void setOtherPlayer(Player_Rect otherPlayer) {
		this.otherPlayer = otherPlayer;
	}

}
