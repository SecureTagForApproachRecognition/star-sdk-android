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

import java.nio.charset.Charset;
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

	public static void showInputDialog(Context context, String title, String msg, @Nullable String validityRegex,
			InputDialogClickListener positiveClickListener) {
		View dialogView = LayoutInflater.from(context).inflate(R.layout.view_input_dialog, null);
		TextView msgView = dialogView.findViewById(R.id.input_dialog_msg);
		msgView.setText(msg);
		EditText inputView = dialogView.findViewById(R.id.input_dialog_input);
		View errorView = dialogView.findViewById(R.id.input_dialog_error_text);

		AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(title)
				.setView(dialogView)
				.setPositiveButton(R.string.dialog_confirm_positive_button, null)
				.setNegativeButton(R.string.dialog_confirm_negative_button, (d, which) -> d.dismiss())
				.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
			String input = inputView.getText().toString();
			if (validityRegex == null || input.matches(validityRegex)) {
				String inputBase64 = new String(Base64.encode(input.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP),
						StandardCharsets.UTF_8);
				positiveClickListener.onPositiveButtonClick(inputBase64);
				dialog.dismiss();
			} else {
				errorView.setVisibility(View.VISIBLE);
			}
		});

	}

	public interface InputDialogClickListener {
		void onPositiveButtonClick(String inputBase64);

	}

}
