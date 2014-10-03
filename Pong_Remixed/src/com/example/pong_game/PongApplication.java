package com.example.pong_game;

import java.util.ArrayList;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;

//This class will handle the options, and other settings, like the game modes, 1p or 2p, what side
//the player wants to be on, turning off and on sounds effects etc....
//Apparently this class extends context at one point hmm
public class PongApplication extends Application {
	public static final String TAG = PongApplication.class.getSimpleName();
	// What side the player is on
	boolean left = true;
	boolean multiplayer = false;
	ContentValues difficulty = new ContentValues();
	// Keys for the content values
	public static final String KEY_DIFFICULTY = "difficulty";
	// Create a MediaPlayer to handle the selected sound
	// Makes more sense to contain the media player in here so the sound can be
	// initailzed
	boolean soundOn = true;
	// Set the soundPlayer in here
	public MediaPlayer soundPlayer;

	// Need the Strings to be drawn on the canvas
	int leftScore = 0;
	int rightScore = 0;

	// Starting with the inital scores 0 and 0
	ContentValues unspent = new ContentValues();
	ContentValues lifetime = new ContentValues();

	private LifeScoreDb score_database;

	// A streak to hold how much the player has won
	int streak = 0;
	// Bonus the player earns from the consecutive scores and eventually the
	// streak
	int bonus = 0;

	// How many times the player has scored in a row
	int consec_hits = 0;

	// A new variable to indicate the game has been paused, to be used in the
	// activity
	// the holds the surface view.
	boolean activity_paused;

	// A variable to maintain the state of the game across the configuration
	// change
	// you need to keep and then retrieve references to the ball and the
	// paddles.
	Ball mainBall;

	// Holding the Y to restore on rotation
	int left_player_y = 0;
	int right_player_y = 0;

	// Boolean to check if the user returned to the Title Screen, then you dont
	// need to pause the thread when the user exits (in the onPause method)
	boolean inTitleScreen;

	// ArrayList to hold the storeItem Objects
	ArrayList<storeItem> storeItems;

	//Constant for size of store items, for now just put 10
	final static int NUMBER_OF_STORE_ITEMS=10;
	
	
			
	//STORE ITEM #1, COIN SPIRTE FOR BALL
	final static int COIN_COST=20;
	final static String COIN_DESCRIPTION= "A classic coin skin for the ball, reminds you a certain plumber";
	final Bitmap COIN_SRC = BitmapFactory.decodeResource(getResources(),R.drawable.coin);
	final static String COIN_NAME = "Golden Coin";
	
	//Store ITEM #2, A FOREST BACKGROUND
	final static int FOREST_COST=40;
	final static String FOREST_DESCRIPTION= "Turns the back into a lush hand drawn forest. Don't get lost!";
	final Bitmap FOREST_SRC = BitmapFactory.decodeResource(getResources(),R.drawable.forest_background);
	final static String FOREST_NAME = "Verdant Forest";
	
	
	
	
	
	
	public void onCreate() {
		super.onCreate();

	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isMultiplayer() {
		return multiplayer;
	}

	public void setMultiplayer(boolean multiplayer) {
		this.multiplayer = multiplayer;
	}

	public boolean isSoundOn() {
		return soundOn;
	}

	public void setSoundOn(boolean soundOn) {
		this.soundOn = soundOn;
	}

	public MediaPlayer getSoundPlayer() {
		return soundPlayer;
	}

	public void setSoundPlayer(MediaPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	public int getLeftScore() {
		return leftScore;
	}

	public void setLeftScore(int leftScore) {
		this.leftScore = leftScore;
	}

	public int getRightScore() {
		return rightScore;
	}

	public void setRightScore(int rightScore) {
		this.rightScore = rightScore;
	}

	// Adds one point to the score and returns the updated string.
	public void addLeftScore() {
		leftScore++;
	}

	public void addRightScore() {
		rightScore++;
	}

	public LifeScoreDb getScore_database() {
		return score_database;
	}

	public void initialData() {
		Log.d(TAG, "Attempting to insert initial values into the database...");
		unspent.put(LifeScoreDb.C_ID, LifeScoreDb.UNSPENDABLE_LIFESCORE_ID);
		unspent.put(LifeScoreDb.C_VALUE, 0);
		lifetime.put(LifeScoreDb.C_ID, LifeScoreDb.SPENDABLE_SCORE_ID);
		lifetime.put(LifeScoreDb.C_VALUE, 0);
		score_database.insert(unspent);
		score_database.insert(lifetime);
		Log.d(TAG, "Data sucessfully initialized");
	}

	public void initializeDatabase(Context context) {
		score_database = new LifeScoreDb(context);
		Log.d(TAG, "Database created, initializeDatabase called");
	}

	public int getStreak() {
		return streak;
	}

	public void incrementStreak() {
		this.streak++;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public int getConsec_hits() {
		return consec_hits;
	}

	public void incrementHits() {
		consec_hits++;
	}

	public boolean isActivity_paused() {
		return activity_paused;
	}

	public void setActivity_paused(boolean activity_paused) {
		this.activity_paused = activity_paused;
	}

	public Ball getMainBall() {
		return mainBall;
	}

	public void setMainBall(Ball mainBall) {
		this.mainBall = mainBall;
	}

	public int getLeft_player_y() {
		return left_player_y;
	}

	public void setLeft_player_y(int left_player_y) {
		this.left_player_y = left_player_y;
	}

	public int getRight_player_y() {
		return right_player_y;
	}

	public void setRight_player_y(int right_player_y) {
		this.right_player_y = right_player_y;
	}

	public void setDifficulty(String diff) {
		difficulty.put(KEY_DIFFICULTY, diff);
	}

	public String getDifficulty() {
		// Hope this Works.... it does
		return difficulty.getAsString(KEY_DIFFICULTY);
	}

	public ContentValues getContentValueDiff() {
		return difficulty;
	}

	public void setStreak(int streak) {
		this.streak = streak;
	}

	public void setConsec_hits(int consec_hits) {
		this.consec_hits = consec_hits;
	}

	public boolean isInTitleScreen() {
		return inTitleScreen;
	}

	public void setInTitleScreen(boolean inTitleScreen) {
		this.inTitleScreen = inTitleScreen;
	}

	public ArrayList<storeItem> getStoreItems() {
		return storeItems;
	}

	public void setStoreItems(ArrayList<storeItem> storeItems) {
		this.storeItems = storeItems;
	}
	
	/*
	 * Helper method to initialize the array list of store items to some
	 * constant value, how every man items are in the store for that 
	 * current update
	 */
	public void initializeStoreArray(){
		storeItems = new ArrayList<storeItem>(NUMBER_OF_STORE_ITEMS);
		//Item Number #1 coin sprite for the ball
		storeItem coinItem  = new storeItem(COIN_COST, COIN_DESCRIPTION, COIN_SRC, COIN_NAME, false);
		storeItem forestItem = new storeItem(FOREST_COST, FOREST_DESCRIPTION, FOREST_SRC, FOREST_NAME, false);
		//put in arrayList
		storeItems.add(coinItem);
		storeItems.add(forestItem);
		
	}
	
 
	

	// Was going to make an ArrayList of Hashmaps, but... it would be too much
	// of a
	// pain to set up the "unlocked" boolean that would keep track of what the
	// player
	// bought, so custom inner class it is
	public class storeItem {
		int cost;
		String description;
		Bitmap image;
		String name;
		boolean unlocked = false;

		/*
		 * Constructor to set fields
		 */
		public storeItem(int cost, String description, Bitmap image,
				String name, boolean unlocked) {
			this.cost = cost;
			this.description = description;
			this.image = image;
			this.name = name;
		}

		public void setUnlocked(boolean val){
			unlocked=val;
		}
		
		
	}

}
