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

public class MultiplayerDialogFragment extends DialogFragment {

	public static final String LEFT_WIN = "Left Side wins!";
	public static final String RIGHT_WIN = "Right Side wins!";
	public TextView multiBox;
	public static final String TAG = LoseDialogFragment.class.getSimpleName();
	public View inflatedView;

	public interface MultiplayerDialogListener {
		public void onDialogMultiPositiveClick(DialogFragment dialog);

		public void onDialogMultiNegativeClick(DialogFragment dialog);
	}

	MultiplayerDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (MultiplayerDialogListener) activity;
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

		// HERE IS THE CUSTOM VIEW OF THE FRAGMENT
		// Pass null as the parent view because its going in the dialog layout
		inflatedView = inflater.inflate(R.layout.multiplayer_screen, null);
		multiBox = (TextView) inflatedView.findViewById(R.id.multiplayer_box);
		// Find which score won is bigger, at the time that the fragment is
		// called.
		PongApplication pongApplication = (PongApplication) getActivity()
				.getApplication();
		if (pongApplication.getLeftScore() > pongApplication.getRightScore()) {
			// Left score is bigger, left side won
			multiBox.append(LEFT_WIN);
		} else if (pongApplication.getLeftScore() < pongApplication
				.getRightScore()) {
			multiBox.append(RIGHT_WIN);
		}

		build.setView(inflatedView);
		build.setPositiveButton(R.string.positive_win,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener
								.onDialogMultiPositiveClick(MultiplayerDialogFragment.this);
					}
				}).setNegativeButton(R.string.negative_win,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener
								.onDialogMultiNegativeClick(MultiplayerDialogFragment.this);
					}
				});

		return build.create();
	}

}
