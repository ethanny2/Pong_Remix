package com.example.pong_game;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartScreen extends MenuActivity {
	static final String TAG = StartScreen.class.getSimpleName();
	private Button startButton;
	private Button scoresButton;
	private Button storeButton;
	// Getting shared preferences, checking inside the preferences
	// for references to keys-boolean pairs. The key is the name of
	// the item, the boolean is weather it has been bought or not

	SharedPreferences prefs = PreferenceManager
			.getDefaultSharedPreferences(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		Log.d(TAG, "onCreate called");
		startButton = (Button) findViewById(R.id.play);
		scoresButton = (Button) findViewById(R.id.score);
		storeButton = (Button) findViewById(R.id.store);
		PongApplication pongApplication = (PongApplication) getApplication();
		// If there is no database start the table and plug in the initial data
		if (pongApplication.getScore_database() == null) {
			pongApplication.initializeDatabase(this);
			pongApplication.initialData();
		}

		// Check if the difficulty is null,(the game is first installed) and put
		// the key in
		// if it is, to allow the switch statements to not get a NULL and access
		// the default case.
		if (pongApplication.getDifficulty() == null) {
			pongApplication.setDifficulty("DEFAULT");
		}

		// This list should be created once everytime it is null or empty, when
		// the user quits the
		// app and comes back in
		if (pongApplication.getStoreItems().size() <= 0
				|| pongApplication.getStoreItems() == null) {
			if (prefs.getBoolean(pongApplication.COIN_NAME, false)) {
				
			}
			if (prefs.getBoolean(pongApplication.FOREST_DESCRIPTION, false)) {
				
			}
			
			pongApplication.initializeStoreArray();
			// Also check if they bought anything
			// Janky implementation...
		}

		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Inner class cannot access global var, needs its own.
				PongApplication pongApp = (PongApplication) getApplication();
				// You are leaving the title screen, thread is now able to be
				// paused
				// with the menu button, and home button
				pongApp.setInTitleScreen(false);
				startActivity(new Intent(StartScreen.this, Pong_Activity.class));
			}
		});

		scoresButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(StartScreen.this, ScoreActivity.class));
			}
		});

		storeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(StartScreen.this, StoreActivity.class));

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume called");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause called");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop called");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy called");
	}

}
