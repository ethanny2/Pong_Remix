package com.example.pong_game;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pong_game.LoseDialogFragment.LoseDialogListener;
import com.example.pong_game.MultiplayerDialogFragment.MultiplayerDialogListener;
import com.example.pong_game.WinDialogFragment.WinDialogListener;

public class Pong_Activity extends Activity implements
		PauseDialogFragment.PauseDialogListener, WinDialogListener,
		LoseDialogListener, MultiplayerDialogListener {

	private static final String TAG = Pong_Activity.class.getSimpleName();
	static int screenWidth;
	static int screenHeight;
	public View image;

	// Get a var for the SurfaceView to handle the thread
	MainGamePanel mainPanel;

	// Retrieve Application from Activity context and change settings
	PongApplication pongApplication;

	// Need a view group to hold the main panel, and programatically display a
	// dialog
	// (read. pause) fragment
	LinearLayout main;

	private static final int MAIN_VIEW_ID = 0;
	WinDialogFragment win_frag;
	PauseDialogFragment pause_frag;
	LoseDialogFragment lose_frag;
	MultiplayerDialogFragment multi_frag;
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;

	private final String KEY_LEFT_Y = "left_paddle";
	private final String KEY_RIGHT_Y = "right_paddle";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Erase the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Make it full Screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Get static reference to screen dimensions in pixels
		Display screen = getWindowManager().getDefaultDisplay();
		Rect rect_screen = new Rect();
		screen.getRectSize(rect_screen);
		screenWidth = rect_screen.width();
		screenHeight = rect_screen.height();
		Log.d(TAG, "The size of the screen is width: " + screenWidth
				+ "height: " + screenHeight);
		// Get the Application reference
		pongApplication = (PongApplication) this.getApplication();

		// Create instance of all the fragments needed
		pause_frag = new PauseDialogFragment();
		pause_frag.setCancelable(false);
		win_frag = new WinDialogFragment();
		win_frag.setCancelable(false);
		lose_frag = new LoseDialogFragment();
		lose_frag.setCancelable(false);
		multi_frag = new MultiplayerDialogFragment();
		multi_frag.setCancelable(false);

		// Getting fragment manager to host the fragment
		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		// See if the bundle has positions to be recorded
		if (savedInstanceState != null) {
			// The screen width changes depending on the orientation,
			// potrait has a larger width than landscape, so if you try to save
			// a
			// a value from potrait to landscape, then the paddle might leave
			// the screen

			if (savedInstanceState.getInt(KEY_LEFT_Y) >= screenHeight) {
				savedInstanceState.putInt(KEY_LEFT_Y, screenHeight
						- Player_Rect.PADDLE_HEIGHT);

			}
			if (savedInstanceState.getInt(KEY_RIGHT_Y) >= screenHeight) {
				savedInstanceState.putInt(KEY_RIGHT_Y, screenHeight
						- Player_Rect.PADDLE_HEIGHT);
			}
			pongApplication.setLeft_player_y(savedInstanceState
					.getInt(KEY_LEFT_Y));
			pongApplication.setRight_player_y(savedInstanceState
					.getInt(KEY_RIGHT_Y));
		}

		// Create a new view, and see if the bundle has any values, to see if
		// the paddles need
		// to be restored.
		mainPanel = new MainGamePanel(this);

		Log.d(TAG, "New mainPanel created");

		// For when the screen is rotated and recreated, check if the user left
		// the
		// pause button on, if so then keep, (the new instance of the thread)
		// paused.
		if (pongApplication.isActivity_paused()) {
			mainPanel.getThread().onPause();
			// pause_frag.show(fragmentManager, "pausing");
		}
		// Instantiate the main view group
		main = new LinearLayout(this);
		main.setId(MAIN_VIEW_ID);
		main.addView(mainPanel);
		Log.d(TAG, "New Linear layout created");
		setContentView(main);
		Log.d(TAG, "SurfaceView mainGamePanel added");
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop called");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume called");
		Log.d(TAG, "Activity is paused " + pongApplication.isActivity_paused());
		if (pongApplication.isActivity_paused()) {
			pause_frag.show(fragmentManager, "pausing");
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy called");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Only use pause the application in onPause() if
		//and the user hit the home or menu button, so they when they
		//resume the game it will automatically be paused. However if they hit the back button
		//then DONT PAUSE IT, checked by the boolean isInTitleScreen, which is set to true
		//if the user hits any of the dialog negative choices.
		if(!pongApplication.isInTitleScreen()){
		pongApplication.setActivity_paused(true);
		}
		Log.d(TAG, "onPause called");
	}

	// What happens to the activity when the back button is pressed.
	@Override
	public void onBackPressed() {
		pause_frag.show(fragmentManager, "pausing");
		// Pause the thread, and the set the paused boolean
		// to true in the application class.
		mainPanel.getThread().onPause();
		pongApplication.setActivity_paused(true);
		Log.d(TAG, "Thread has been paused");
	}

	// Override callback methods to handle dialog responses

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.d(TAG, "Game resumed");
		// Resume thread and set application boolean to false if
		// user clicks "resume"
		//NO PLAYAGAIN, this is the pause button
		pongApplication.setActivity_paused(false);
		mainPanel.getThread().onResume();

	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// Use the default implementation of the back button, if the
		// user hits "quit" and also set the application paused boolean to
		// false, IMPORTANT, IF NOT SET TO FALSE THEN THE THREAD WILL NEVER
		// START
		// AGAIN
		resetGame();
		super.onBackPressed();
		Log.d(TAG, "Game Quit");
	}

	public void winConditions() {
		// If player is on the left side and wins (set to 2 for now)
		if (pongApplication.leftScore == 10 && pongApplication.isLeft()
				&& !pongApplication.isMultiplayer()) {
			Log.d(TAG, "player wins on left side");
			mainPanel.getThread().onPause();
			win_frag.show(fragmentManager, "winning");
		} else if (pongApplication.rightScore == 10
				&& !pongApplication.isLeft()
				&& !pongApplication.isMultiplayer()) {
			// // Player is one the rightside and scores the target score
			Log.d(TAG, "player wins on right side");
			mainPanel.getThread().onPause();
			win_frag.show(fragmentManager, "winning");

		} else if (pongApplication.isMultiplayer()
				&& pongApplication.getLeftScore() == 10
				|| pongApplication.isMultiplayer()
				&& pongApplication.getRightScore() == 10) {
			// // Finally if there is a multiplayer game, there are no points
			// // gained. Or half points. Or no bonuses or something
			// NO POINTS
			Log.d(TAG,
					"multiplayer activates" + pongApplication.isMultiplayer());
			mainPanel.getThread().onPause();
			multi_frag.show(fragmentManager, "Multi-Win");

		} else if (!pongApplication.isLeft() && pongApplication.leftScore == 10
				&& !pongApplication.isMultiplayer()) {
			// Check if the left score is 1, and the player is not there, then
			// the comp won
			Log.d(TAG, "comp wins on left side");
			mainPanel.getThread().onPause();
			lose_frag.show(fragmentManager, "losing");
		} else if (pongApplication.rightScore == 10 && pongApplication.isLeft()
				&& !pongApplication.isMultiplayer()) {
			// If the right reaches the target score, and the player is on the
			// left,
			// then the AI/comp wins
			Log.d(TAG, "player wins on right side" + pongApplication.isLeft()
					+ "isLeft");
			mainPanel.getThread().onPause();
			lose_frag.show(fragmentManager, "losing");
		}

	}

	// Methods for handling the win screen
	@Override
	public void onDialogWinPositiveClick(DialogFragment dialog) {
		// Start the thread and reset the scores, need to also rese
		playAgain();
		mainPanel.getThread().onResume();

	}

	@Override
	public void onDialogWinNegativeClick(DialogFragment dialog) {
		resetGame();
		super.onBackPressed();
	}

	// For the losing fragment
	@Override
	public void onDialogLosePositiveClick(DialogFragment dialog) {
		// Start the game again, need to also reset ball
		playAgain();
		mainPanel.getThread().onResume();

	}

	@Override
	public void onDialogLoseNegativeClick(DialogFragment dialog) {
		// Don't play again, quit the game
		resetGame();
		super.onBackPressed();

	}

	@Override
	public void onDialogMultiPositiveClick(DialogFragment dialog) {
		// Start the game again, need to also reset ball
		playAgain();
		mainPanel.getThread().onResume();
	}

	@Override
	public void onDialogMultiNegativeClick(DialogFragment dialog) {
		resetGame();
		super.onBackPressed();
	}

	
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Need to do some checking to determine if which paddle is left and
		// which one is left
		if (pongApplication.isLeft() && !pongApplication.isMultiplayer()) {
			// Player is left
			outState.putInt(KEY_LEFT_Y, mainPanel.getRectPlayer().getY());
			outState.putInt(KEY_RIGHT_Y, mainPanel.getOtherPlayer().getY());
			Log.d(TAG, "saved Y position's, player is left");
			Log.d(TAG, "Left Y POS: " + mainPanel.getRectPlayer().getY());

		} else if (!pongApplication.isLeft()
				&& !pongApplication.isMultiplayer()) {
			// Player is right
			outState.putInt(KEY_RIGHT_Y, mainPanel.getRectPlayer().getY());
			outState.putInt(KEY_LEFT_Y, mainPanel.getOtherPlayer().getY());
			Log.d(TAG, "saved Y position's, player is right");

		} else if (pongApplication.isMultiplayer()) {
			// Multiplayer, sides dont matter
			outState.putInt(KEY_LEFT_Y, mainPanel.getRectPlayer().getY());
			outState.putInt(KEY_RIGHT_Y, mainPanel.getOtherPlayer().getY());
			Log.d(TAG, "saved Y position's, multiplayer");

		}

	}
	
	/*
	 * Method to be called when the user exits the activity and the game resets everything
	 */
	public void resetGame(){
		pongApplication.setActivity_paused(false);
		pongApplication.setInTitleScreen(true);
		// Also destroy the references that are stored for the scores, paddle,
		// streak, ball
		// and bonus
		pongApplication.setBonus(0);
		pongApplication.setConsec_hits(0);
		pongApplication.setLeftScore(0);
		pongApplication.setRightScore(0);
		pongApplication.setLeft_player_y(0);
		pongApplication.setRight_player_y(0);
		pongApplication.setMainBall(null);
	}
	
	/*
	 * A method to just reset the ball and the score when the player clicks play again.
	 */
	public void playAgain(){
		pongApplication.setLeftScore(0);
		pongApplication.setRightScore(0);
		pongApplication.setConsec_hits(0);
		pongApplication.setMainBall(null);	
	}
	
	

	

}
