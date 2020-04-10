package ch.ubique.android.starsdk.sample.util;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import java.nio.charset.StandardCharsets;

import ch.ubique.android.starsdk.sample.R;

public class DialogUtil {

	public static void showConfirmDialog(Context context, @StringRes int title,
			DialogInterface.OnClickListener positiveClickListener) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(R.string.dialog_confirm_message)
				.setPositiveButton(R.string.dialog_confirm_positive_button, (dialog, which) -> {
					dialog.dismiss();
					positiveClickListener.onClick(dialog, which);
				})
				.setNegativeButton(R.string.dialog_confirm_negative_button, (dialog, which) -> dialog.dismiss())
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
