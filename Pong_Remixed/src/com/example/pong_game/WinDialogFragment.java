package com.example.pong_game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class WinDialogFragment extends DialogFragment {
	public TextView streakView;
	public TextView bonusView;
	public TextView totalView;
	public View inflatedView;
	PongApplication pongApplication;

	public static final String TAG = WinDialogFragment.class.getSimpleName();

	public interface WinDialogListener {
		public void onDialogWinPositiveClick(DialogFragment dialog);

		public void onDialogWinNegativeClick(DialogFragment dialog);
	}

	WinDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "ON ATTACH CALLED");
		super.onAttach(activity);
		try {
			mListener = (WinDialogListener) activity;
		} catch (ClassCastException exception) {
			Log.d(TAG, "Activity must implement the interface");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog called");
		pongApplication = (PongApplication) getActivity().getApplication();
		//When you win increament the winning streak
		pongApplication.incrementStreak();
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		// Get the layout inflater (needed to put in custom XML)
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// HERE IS THE CUSTOM VIEW OF THE FRAGMENT
		// Pass null as the parent view because its going in the dialog layout
		inflatedView = inflater.inflate(R.layout.win_screen, null);

		streakView = (TextView) inflatedView.findViewById(R.id.streak_box);
		bonusView = (TextView) inflatedView.findViewById(R.id.bonus_box);
		totalView = (TextView) inflatedView.findViewById(R.id.total_box);

		// In this method the total points earned will also be calculated.
		// Pass in what you won with
		// Player is on the left side
		if (pongApplication.isLeft() && !pongApplication.isMultiplayer()) {
			calculateScore(pongApplication.getLeftScore());
			// Player is on the right side
		} else if ((!pongApplication.isLeft() && !pongApplication
				.isMultiplayer())) {
			calculateScore(pongApplication.getRightScore());
		}

		build.setView(inflatedView);

		build.setPositiveButton(R.string.positive_win,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener
								.onDialogWinPositiveClick(WinDialogFragment.this);
					}
				}).setNegativeButton(R.string.negative_win,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener
								.onDialogWinNegativeClick(WinDialogFragment.this);
					}
				});

		return build.create();
	}

	// Method to append numbers to the win screen text boxes

	public void appendStreak(int num) {
		streakView.append(String.valueOf(num));
	}

	public void appendBonus(int num) {
		bonusView.append(String.valueOf(num));
	}

	public void appendTotal(int num) {
		totalView.append(String.valueOf(num));
	}

	public void calculateScore(int total) {
		// Check the current difficulty and set the streak multipliers
		// accordingly
		
		//Get the bonus
		int bonus = total;
		// Check if the difficulty key even exists
		if (pongApplication.getContentValueDiff().containsKey(
				PongApplication.KEY_DIFFICULTY)) {
			switch (pongApplication.getDifficulty()) {
			// Easy
			case MenuActivity.diff_easy:
				if (pongApplication.getStreak() > 0
						&& pongApplication.getStreak() <= 4) {
					bonus += 0;
				} else if (pongApplication.getStreak() >= 5
						&& pongApplication.getStreak() < 8) {
					bonus *= 3;
				} else if (pongApplication.getStreak() >= 8
						&& pongApplication.getStreak() >= 10) {
					bonus *= 4;
				}
				break;
			// Medium
			case MenuActivity.diff_med:
				if (pongApplication.getStreak() > 0
						&& pongApplication.getStreak() <= 4) {
					bonus *= 4;
				} else if (pongApplication.getStreak() >= 5
						&& pongApplication.getStreak() < 8) {
					bonus *= 5;
				} else if (pongApplication.getStreak() >= 8
						&& pongApplication.getStreak() >= 10) {
					bonus *= 6;
				}

				break;

			// Hard
			case MenuActivity.diff_hard:
				if (pongApplication.getStreak() > 0
						&& pongApplication.getStreak() <= 4) {
					bonus *= 7;
				} else if (pongApplication.getStreak() >= 5
						&& pongApplication.getStreak() < 8) {
					bonus *= 8;
				} else if (pongApplication.getStreak() >= 8
						&& pongApplication.getStreak() >= 10) {
					bonus *= 10;
				}
				break;

			default:
				if (pongApplication.getStreak() > 0
						&& pongApplication.getStreak() <= 4) {
					bonus += 0;
				} else if (pongApplication.getStreak() >= 5
						&& pongApplication.getStreak() < 8) {
					bonus *= 2;
				} else if (pongApplication.getStreak() >= 8
						&& pongApplication.getStreak() >= 10) {
					bonus *= 2;
				}
				break;
			}
			
			// Get some bonus
			if (pongApplication.getConsec_hits() <= 3) {
				bonus += 3;
			} else if (pongApplication.getConsec_hits() <= 6 && pongApplication.getConsec_hits()>3) {
				bonus += 5;
			} else if (pongApplication.getConsec_hits() >= 10) {
				bonus *= 2;
			}

			// End by attaching the points and inserting into the database

			appendStreak(pongApplication.getStreak());
			//Subtract the original to get only the bonus
			appendBonus(bonus-total);
			appendTotal(total+bonus);

			// Also need to open the database and add the points
			pongApplication.getScore_database().addPoints(total+bonus);

		}
	}

}
