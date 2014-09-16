package com.example.pong_game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class LifeScoreDb {
	public static final String TAG = LifeScoreDb.class.getSimpleName();
	public static final int VERSION = 1;
	public static final String TABLE = "points";
	public static final String DATABASE = "score";
	public static final String C_ID = "_id";
	public static final String C_VALUE = "_value";

	public static final int SPENDABLE_SCORE_ID = 0;
	public static final int UNSPENDABLE_LIFESCORE_ID = 1;

	PongApplication pongApplication;

	// Columns
	public static final String[] POINTS = { C_VALUE };

	// Hold the context
	Context myContext;
	public final DbHelper DbHelp;
	SQLiteDatabase db;

	// Hold Application

	// Constructor initializes the database helper, needs context
	public LifeScoreDb(Context context) {
		this.DbHelp = new DbHelper(context);
		myContext = context;
		pongApplication = (PongApplication) myContext.getApplicationContext();
		Log.d(TAG, "DbHelper class created");
		// Also start the database with
	}

	// Closing the helper
	public void close() {
		this.DbHelp.close();
	}

	// Inset
	public void insert(ContentValues values) {
		SQLiteDatabase db = DbHelp.getWritableDatabase();
		Log.d(TAG, "Attempting to insert values into the database");
		try {
			db.insert(TABLE, null, values);
		} finally {
			db.close();
		}
	}

	// Delete all the data
	public void delete() {
		SQLiteDatabase db = DbHelp.getReadableDatabase();
		db.delete(TABLE, null, null);
		db.close();
	}

	// Query the data base for the unspent score
	public String queryUnspent() {
		SQLiteDatabase db = DbHelp.getReadableDatabase();
		try {
			Cursor myCursor = db.query(TABLE, POINTS, C_ID + "="
					+ SPENDABLE_SCORE_ID, null, null, null, null);
			try {
				myCursor.moveToFirst();
				return String.valueOf(myCursor.getInt(myCursor
						.getColumnIndex(C_VALUE)));
			} finally {
				myCursor.close();
			}
		} finally {
			db.close();
		}
	}

	// Query the database for the lifetime score
	public String queryTotal() {
		SQLiteDatabase db = DbHelp.getReadableDatabase();
		try {
			Cursor myCursor = db.query(TABLE, POINTS, C_ID + "="
					+ UNSPENDABLE_LIFESCORE_ID, null, null, null, null);
			try {
				myCursor.moveToFirst();
				return String.valueOf(myCursor.getInt(myCursor
						.getColumnIndex(C_VALUE)));
			} finally {
				myCursor.close();
			}
		} finally {
			db.close();
		}
	}

	// Query the data base to get score, then subtract values, and insert back
	// in.
	public void spendPoints(int spent) {
		SQLiteDatabase db = DbHelp.getReadableDatabase();
		ContentValues new_val = new ContentValues();
		try {
			Cursor myCursor = db.query(TABLE, POINTS, C_ID + "="
					+ SPENDABLE_SCORE_ID, null, null, null, null);
			try {
				int currentTotal = myCursor.getInt(myCursor
						.getColumnIndex(C_VALUE));
				if (currentTotal > spent) {
					Toast.makeText(myContext, "Not enough points to buy",
							Toast.LENGTH_LONG);
				} else {
					currentTotal -= spent;
					new_val.put(C_VALUE, currentTotal);
				}
				// DO something to place the data back in the table.
				// Call the update
				db.update(TABLE, new_val, C_ID + "=" + SPENDABLE_SCORE_ID, null);
			} finally {
				myCursor.close();
			}

		} finally {
			db.close();
		}

	}

	// Add points to both the lifetime score and the 
	public void addPoints(int points) {
		Log.d(TAG, "Attempting to update score data");
		SQLiteDatabase db = DbHelp.getReadableDatabase();
		ContentValues new_val = new ContentValues();
		ContentValues new_val2 = new ContentValues();
		try {
			Cursor myCursor = db.query(TABLE, POINTS, C_ID + "="
					+ SPENDABLE_SCORE_ID, null, null, null, null);
			Cursor myCursor2 = db.query(TABLE, POINTS, C_ID + "="
					+ UNSPENDABLE_LIFESCORE_ID, null, null, null, null);
			try {
				myCursor.moveToFirst();
				myCursor2.moveToFirst();
				int currentTotal = myCursor.getInt(myCursor
						.getColumnIndex(C_VALUE));
				int lifeTotal2 = myCursor2.getInt(myCursor2
						.getColumnIndex(C_VALUE));
				currentTotal += points;
				lifeTotal2 += points;
				new_val.put(C_VALUE, currentTotal);
				new_val2.put(C_VALUE, lifeTotal2);
				db.update(TABLE, new_val, C_ID + "=" + SPENDABLE_SCORE_ID, null);
				db.update(TABLE, new_val2, C_ID + "="
						+ UNSPENDABLE_LIFESCORE_ID, null);
			} finally {
				myCursor.close();
				myCursor2.close();
			}
			Log.d(TAG, "Score data sucessfully recorded");
		} finally {
			db.close();
		}

	}

	// Inner helper class
	public class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating " + DATABASE);
			String sql = String.format(
					"create table %s (%s INT PRIMARY KEY , %s INTEGER)", TABLE,
					C_ID, C_VALUE);
			db.execSQL(sql);

			// THIS SHOULD ONLY BE DONE ONCE WHEN THE DATABASE IS CREATED
			// it gives unique ids and to columns and gives them an initial
			// value of 0
		//	pongApplication.initializeDatabase(myContext);
			//pongApplication.initialData();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Destroy entire database on upgrade, not going to be upgraded.
			Log.d(TAG, "onUPGRADE BEING CALLED");
			db.execSQL("drop table " + TABLE);
			this.onCreate(db);

		}

	}

}
