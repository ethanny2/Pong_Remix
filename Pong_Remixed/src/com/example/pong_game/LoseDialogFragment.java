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

/*
 * Simple class to display the use screen. Play again and restart button.
 */
public class LoseDialogFragment extends DialogFragment {

	public static final String TAG = LoseDialogFragment.class.getSimpleName();
	public View inflatedView;
	public interface LoseDialogListener {
		public void onDialogLosePositiveClick(DialogFragment dialog);
		public void onDialogLoseNegativeClick(DialogFragment dialog);
	}

	LoseDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (LoseDialogListener) activity;
		} catch (ClassCastException exception) {
			Log.d(TAG, "Activity must implement the interface");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG, "onCreateDialog called");
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		// Get the layout inflater (needed to put in custom XML)
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		//Streak is over you lose
		PongApplication pongApplication = (PongApplication)getActivity().getApplication();
		pongApplication.setStreak(0);

		// HERE IS THE CUSTOM VIEW OF THE FRAGMENT
		// Pass null as the parent view because its going in the dialog layout
		inflatedView = inflater.inflate(R.layout.lose_screen, null);
		build.setView(inflatedView);
		build.setPositiveButton(R.string.positive_win,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener
								.onDialogLosePositiveClick(LoseDialogFragment.this);
					}
				}).setNegativeButton(R.string.negative_win,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener
								.onDialogLoseNegativeClick(LoseDialogFragment.this);
					}
				});

		return build.create();
	}

	

}
