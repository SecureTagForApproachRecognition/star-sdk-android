package ch.ubique.android.starsdk.sample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.ubique.android.starsdk.STARTracing;
import ch.ubique.android.starsdk.TracingStatus;
import ch.ubique.android.starsdk.backend.CallbackListener;
import ch.ubique.android.starsdk.backend.ResponseException;
import ch.ubique.android.starsdk.sample.util.DialogUtil;
import ch.ubique.android.starsdk.sample.util.RequirementsUtil;

public class HomeFragment extends Fragment {

	private static final String TAG = HomeFragment.class.getCanonicalName();

	private static final int REQUEST_CODE_PERMISSION_LOCATION = 1;

	private static final DateFormat DATE_FORMAT_SYNC = SimpleDateFormat.getDateTimeInstance();

	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				checkPermissionRequirements();
				updateSdkStatus();
			}
		}
	};

	private BroadcastReceiver sdkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateSdkStatus();
		}
	};

	public static HomeFragment newInstance() {
		return new HomeFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupUi(view);
	}

	@Override
	public void onResume() {
		super.onResume();
		getContext().registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		getContext().registerReceiver(sdkReceiver, STARTracing.getUpdateIntentFilter());
		checkPermissionRequirements();
		updateSdkStatus();
	}

	@Override
	public void onPause() {
		super.onPause();
		getContext().unregisterReceiver(bluetoothReceiver);
		getContext().unregisterReceiver(sdkReceiver);
	}

	private void setupUi(View view) {
		Button locationButton = view.findViewById(R.id.home_button_location);
		locationButton.setOnClickListener(
				v -> requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
						REQUEST_CODE_PERMISSION_LOCATION));

		Button batteryButton = view.findViewById(R.id.home_button_battery_optimization);
		batteryButton.setOnClickListener(
				v -> startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
						Uri.parse("package:" + getContext().getPackageName()))));

		Button bluetoothButton = view.findViewById(R.id.home_button_bluetooth);
		bluetoothButton.setOnClickListener(v -> BluetoothAdapter.getDefaultAdapter().enable());

		Button refreshButton = view.findViewById(R.id.home_button_sync);
		refreshButton.setOnClickListener(v -> resyncSdk());
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
			checkPermissionRequirements();
			updateSdkStatus();
		}
	}

	private void checkPermissionRequirements() {
		View view = getView();
		Context context = getContext();
		if (view == null || context == null) return;

		boolean locationGranted = RequirementsUtil.isLocationPermissionGranted(context);
		Button locationButton = view.findViewById(R.id.home_button_location);
		locationButton.setEnabled(!locationGranted);
		locationButton.setText(locationGranted ? R.string.req_location_permission_granted
											   : R.string.req_location_permission_ungranted);

		boolean batteryOptDeactivated = RequirementsUtil.isBatteryOptimizationDeactivated(context);
		Button batteryButton = view.findViewById(R.id.home_button_battery_optimization);
		batteryButton.setEnabled(!batteryOptDeactivated);
		batteryButton.setText(batteryOptDeactivated ? R.string.req_battery_deactivated
													: R.string.req_battery_deactivated);

		boolean bluetoothActivated = RequirementsUtil.isBluetoothEnabled();
		Button bluetoothButton = view.findViewById(R.id.home_button_bluetooth);
		bluetoothButton.setEnabled(!bluetoothActivated);
		bluetoothButton.setText(bluetoothActivated ? R.string.req_bluetooth_active
												   : R.string.req_bluetooth_inactive);
	}

	private void resyncSdk() {
		new Thread(() -> {
			try {
				STARTracing.sync(getContext());
				new Handler(getContext().getMainLooper()).post(this::updateSdkStatus);
			} catch (IOException | ResponseException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}).start();
	}

	private void updateSdkStatus() {
		View view = getView();
		Context context = getContext();
		if (context == null || view == null) return;

		TracingStatus status = STARTracing.getStatus(context);

		TextView statusText = view.findViewById(R.id.home_status_text);
		statusText.setText(formatStatusString(status));

		Button buttonStartStopTracking = view.findViewById(R.id.home_button_start_stop_tracking);
		boolean isTracking = status.isTracking_active();
		buttonStartStopTracking.setSelected(isTracking);
		buttonStartStopTracking.setText(getString(isTracking ? R.string.button_tracking_stop
															 : R.string.button_tracking_start));
		buttonStartStopTracking.setOnClickListener(v -> {
			if (isTracking) {
				STARTracing.stop(context);
			} else {
				STARTracing.start(context);
			}
			updateSdkStatus();
		});

		Button buttonReportExposed = view.findViewById(R.id.home_button_report_exposed);
		boolean isExposed = status.isAm_i_exposed();
		buttonReportExposed.setText(getString(isExposed ? R.string.button_report_healed : R.string.button_report_exposed));
		buttonReportExposed.setOnClickListener(
				v -> DialogUtil
						.showConfirmDialog(v.getContext(),
								isExposed ? R.string.dialog_healed_title : R.string.dialog_expose_title,
								(dialog, which) -> sendExposedUpdate(context, isExposed)));
	}

	private SpannableString formatStatusString(TracingStatus status) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		builder.append(getString(status.isTracking_active() ? R.string.status_tracking_active : R.string.status_tracking_inactive))
				.append("\n")
				.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

		long lastSyncDateUTC = status.getLast_sync_date();
		String lastSyncDateString =
				lastSyncDateUTC > 0 ? DATE_FORMAT_SYNC.format(new Date(lastSyncDateUTC)) : "n/a";
		builder.append(getString(R.string.status_last_synced, lastSyncDateString)).append("\n")
				.append(getString(R.string.status_self_exposed, status.isAm_i_exposed())).append("\n")
				.append(getString(R.string.status_been_exposed, status.isWas_contact_exposed())).append("\n")
				.append(getString(R.string.status_number_handshakes, status.getNumber_of_handshakes()));

		if (status.getError() != null) {
			String errorString = status.getError().toString();
			int start = builder.length();
			builder.append("\n\n")
					.append(errorString)
					.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red, null)),
							start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		return new SpannableString(builder);
	}

	private void sendExposedUpdate(Context context, boolean sendHeal) {
		setExposeLoadingViewVisible(true);
		if (sendHeal) {
			STARTracing.sendIWasHealed(context, null, new CallbackListener<Void>() {
				@Override
				public void onSuccess(Void response) {
					DialogUtil.showMessageDialog(context, getString(R.string.dialog_title_success),
							getString(R.string.dialog_message_request_success));
					setExposeLoadingViewVisible(false);
					updateSdkStatus();
				}

				@Override
				public void onError(Throwable throwable) {
					DialogUtil.showMessageDialog(context, getString(R.string.dialog_title_error),
							throwable.getLocalizedMessage());
					Log.e(TAG, throwable.getMessage(), throwable);
					setExposeLoadingViewVisible(false);
				}
			});
		} else {
			STARTracing.sendIWasExposed(context, null, new CallbackListener<Void>() {
				@Override
				public void onSuccess(Void response) {
					DialogUtil.showMessageDialog(context, getString(R.string.dialog_title_success),
							getString(R.string.dialog_message_request_success));
					setExposeLoadingViewVisible(false);
					updateSdkStatus();
				}

				@Override
				public void onError(Throwable throwable) {
					DialogUtil.showMessageDialog(context, getString(R.string.dialog_title_error),
							throwable.getLocalizedMessage());
					Log.e(TAG, throwable.getMessage(), throwable);
					setExposeLoadingViewVisible(false);
				}
			});
		}
	}

	private void setExposeLoadingViewVisible(boolean visible) {
		View view = getView();
		if (view != null) {
			view.findViewById(R.id.home_loading_view_exposed).setVisibility(visible ? View.VISIBLE : View.GONE);
			view.findViewById(R.id.home_button_report_exposed).setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		}
	}

}
