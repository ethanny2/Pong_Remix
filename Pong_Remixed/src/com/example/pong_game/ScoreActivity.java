package com.example.pong_game;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ScoreActivity extends Activity {

	PongApplication pongApplication;
	TextView unspent_text;
	TextView lifetime_text;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Erase the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Make it full Screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.scores_layout);
		pongApplication = (PongApplication) getApplication();
		unspent_text = (TextView) findViewById(R.id.unspent);
		lifetime_text = (TextView) findViewById(R.id.lifetime);
		// CharSequence normal=unspent_text.getText();

		// TESTING UPDATE METHOD... WORKS!
		// pongApplication.getScore_database().addPoints(10);
		unspent_text.append(": "
				+ pongApplication.getScore_database().queryUnspent());
		lifetime_text.append(": "
				+ pongApplication.getScore_database().queryTotal());
		// unspent_text.append(normal);

	}

}
