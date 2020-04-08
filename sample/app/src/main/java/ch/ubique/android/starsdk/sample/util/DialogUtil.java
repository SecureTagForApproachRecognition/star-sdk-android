package ch.ubique.android.starsdk.sample.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.StringRes;

import ch.ubique.android.starsdk.sample.R;

public class DialogUtil {

	public static void showConfirmDialog(Context context, @StringRes int title, DialogInterface.OnClickListener positiveClickListener) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.dialog_expose_title)
				.setMessage(R.string.dialog_expose_message)
				.setPositiveButton(R.string.dialog_expose_positive_button, (dialog, which) -> {
					dialog.dismiss();
					positiveClickListener.onClick(dialog, which);
				})
				.setNegativeButton(R.string.dialog_expose_negative_button, (dialog, which) -> dialog.dismiss())
				.show();
	}

	public static void showMessageDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> dialog.dismiss())
				.show();
	}

}
