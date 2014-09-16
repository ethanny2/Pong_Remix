package com.example.pong_game;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuActivity extends Activity {
	PongApplication pongApplication;
	//Constants for selecting difficulty
	static final String diff_easy = "EASY";
	static final String diff_med = "MEDIUM";
	static final String diff_hard = "HARD";
	static final String KEY_DEFAULT="DEF";
	Menu myMenu;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pongApplication = (PongApplication)getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu, menu);
		//Get a reference to the menu
		 myMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Need a default difficulty
		
	MenuItem easyItem =myMenu.findItem(R.id.easy);
	MenuItem mediumItem =myMenu.findItem(R.id.medium);
	MenuItem hardItem =myMenu.findItem(R.id.hard);
		
		switch(item.getItemId()){
		case R.id.easy:
			//Check if other difficulties are already checked.
			if(mediumItem.isChecked() || hardItem.isChecked()){
				Toast.makeText(this, "Uncheck the other difficulties first.", Toast.LENGTH_SHORT).show();
			}else if(item.isChecked()){
				item.setChecked(false);
				Toast.makeText(this, "Default difficulty is easy mode.", Toast.LENGTH_SHORT).show();
				pongApplication.setDifficulty(KEY_DEFAULT);
			}else{
				item.setChecked(true);
				pongApplication.setDifficulty(diff_easy);
			}
			return true;
		
		case R.id.medium:
			//Check if other difficulties are already checked.
			if(easyItem.isChecked() || hardItem.isChecked()){
				Toast.makeText(this, "Uncheck the other difficulties first.", Toast.LENGTH_SHORT).show();
			}else if(item.isChecked()){
				item.setChecked(false);
				Toast.makeText(this, "Default difficulty is easy mode.", Toast.LENGTH_SHORT).show();
				pongApplication.setDifficulty(KEY_DEFAULT);

			}else{
				item.setChecked(true);
				pongApplication.setDifficulty(diff_med);
			}
			return true;
			
		case R.id.hard:
			//Check if other difficulties are already checked.
			if(mediumItem.isChecked() || easyItem.isChecked()){
				Toast.makeText(this, "Uncheck the other difficulties first.", Toast.LENGTH_SHORT).show();
			}else if(item.isChecked()){
				item.setChecked(false);
				Toast.makeText(this, "Default difficulty is easy mode.", Toast.LENGTH_SHORT).show();
				pongApplication.setDifficulty(KEY_DEFAULT);

			}else{
				item.setChecked(true);
				pongApplication.setDifficulty(diff_hard);
			}
			return true;
				
		case R.id.sound:
			//Do something
			if(item.isChecked()){
				//Uncheck and get rid of sound.(In application class.)
				pongApplication.setSoundOn(false);
				item.setChecked(false);
			} else{
				//It is not checked, check it and set sound, in Application class.
				item.setChecked(true);
				pongApplication.setSoundOn(true);
			}
			return true;
			
		case R.id.two_player:
			//Do something
			if(item.isChecked()){
				item.setChecked(false);
				pongApplication.setMultiplayer(false);
			}else{
				item.setChecked(true);
				pongApplication.setMultiplayer(true);
			}
			return true;
		
		case R.id.side:
			//Do something
			if(item.isChecked()){
				item.setChecked(false);
				pongApplication.setLeft(true);
			}else{
				item.setChecked(true);
				pongApplication.setLeft(false);
			}
			return true;
		
		
		
		
		
		}
		
		
		
		
		return super.onOptionsItemSelected(item);
	}
	
	
	

}
