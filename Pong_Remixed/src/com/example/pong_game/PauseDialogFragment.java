package com.example.pong_game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

//A Dialog that will pop open if the user hits the back button and wants
//to pause the game.

public class PauseDialogFragment extends DialogFragment {

	// It is more organized to deal with the response of the dialog
	// inside the activity that hosts the fragment. In order to get this
	// create a custom interface and have the activity implement it.

	// Create public inner interface, must implement these methods
	public interface PauseDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Create an instance of the interface and hold it in the fragment
	// override the onAttach() of the Fragment class, which is when
	// fragments attaches to activity. Check if the activity is an instance
	// of the listener (it implements it) with a cast.

	PauseDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Use try/catch to check if the listener is implemented by the
		// activity
		try {
			mListener = (PauseDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement the listener");
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// The builder class makes it easy
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.pause_menu)
				.setPositiveButton(R.string.positive_pause,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// To be handled in the activity that
								// hosts the fragment,
								mListener.onDialogPositiveClick(PauseDialogFragment.this);
							}
						})
				.setNegativeButton(R.string.negative_pause,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mListener.onDialogNegativeClick(PauseDialogFragment.this);

							}
						});
		return builder.create();
	}
}
