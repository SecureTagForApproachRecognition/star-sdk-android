package ch.ubique.android.starsdk.sample.parameters;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.android.starsdk.AppConfigManager;
import ch.ubique.android.starsdk.sample.R;
import ch.ubique.android.starsdk.BluetoothPowerLevel;

public class ParametersFragment extends Fragment {

	private static final int MIN_INTERVAL_SCANNING_SECONDS = 60;
	private static final int MAX_INTERVAL_SCANNING_SECONDS = 900;
	private static final int MIN_DURATION_SCANNING_SECONDS = 10;
	private Spinner spinnerPowerLevel;
	private SeekBar seekBarScanInterval;
	private SeekBar seekBarScanDuration;
	private EditText inputScanInterval;
	private EditText inputScanDuration;

	public static ParametersFragment newInstance() {
		return new ParametersFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_parameters, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		seekBarScanInterval = view.findViewById(R.id.parameter_seekbar_scan_interval);
		inputScanInterval = view.findViewById(R.id.parameter_input_scan_interval);
		seekBarScanDuration = view.findViewById(R.id.parameter_seekbar_scan_duration);
		inputScanDuration = view.findViewById(R.id.parameter_input_scan_duration);

		seekBarScanInterval.setMax(MAX_INTERVAL_SCANNING_SECONDS - MIN_INTERVAL_SCANNING_SECONDS);
		seekBarScanInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int intervalDuration = progress + MIN_INTERVAL_SCANNING_SECONDS;
				inputScanInterval.setText(String.valueOf(intervalDuration));
				int newMaxProgress = intervalDuration - 1 - MIN_DURATION_SCANNING_SECONDS;
				adjustNewDurationMaximum(newMaxProgress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setScanInterval(seekBar.getProgress() + MIN_INTERVAL_SCANNING_SECONDS);
			}
		});
		inputScanInterval.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				String input = inputScanInterval.getText().toString();
				if (input.length() == 0) return true;
				try {
					int inputIntervalSeconds = Integer.parseInt(input);
					inputIntervalSeconds =
							Math.min(MAX_INTERVAL_SCANNING_SECONDS, Math.max(MIN_INTERVAL_SCANNING_SECONDS, inputIntervalSeconds));
					inputScanInterval.setText(String.valueOf(inputIntervalSeconds));
					seekBarScanInterval.setProgress(inputIntervalSeconds - MIN_INTERVAL_SCANNING_SECONDS);
					setScanInterval(inputIntervalSeconds);
					hideKeyboard(v);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		});

		seekBarScanDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int scanDuration = progress + MIN_DURATION_SCANNING_SECONDS;
				inputScanDuration.setText(String.valueOf(scanDuration));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setScanDuration(seekBar.getProgress() + MIN_DURATION_SCANNING_SECONDS);
			}
		});
		inputScanDuration.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				String input = inputScanDuration.getText().toString();
				if (input.length() == 0) return true;
				try {
					int inputDurationSeconds = Integer.parseInt(input);
					inputDurationSeconds =
							Math.min(getScanInterval() - 1, Math.max(MIN_DURATION_SCANNING_SECONDS, inputDurationSeconds));
					inputScanDuration.setText(String.valueOf(inputDurationSeconds));
					seekBarScanDuration.setProgress(inputDurationSeconds - MIN_DURATION_SCANNING_SECONDS);
					setScanDuration(inputDurationSeconds);
					hideKeyboard(v);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		});

		spinnerPowerLevel = view.findViewById(R.id.parameter_spinner_power_level);
		ArrayAdapter<BluetoothPowerLevel> powerLevelAdapter =
				new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, BluetoothPowerLevel.values());
		spinnerPowerLevel.setAdapter(powerLevelAdapter);
		spinnerPowerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setAdvertPowerLevel(BluetoothPowerLevel.values()[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
	}

	private void adjustNewDurationMaximum(int durationProgressMaximum) {
		int currentDurationProgress = seekBarScanDuration.getProgress();
		seekBarScanDuration.setMax(durationProgressMaximum);
		if (currentDurationProgress > durationProgressMaximum) {
			setScanDuration(durationProgressMaximum + MIN_DURATION_SCANNING_SECONDS);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		AppConfigManager appConfigManager = AppConfigManager.getInstance(getContext());
		int interval = (int) (appConfigManager.getScanInterval() / 1000);
		seekBarScanInterval.setProgress(interval - MIN_INTERVAL_SCANNING_SECONDS);
		int duration = (int) (appConfigManager.getScanDuration() / 1000);
		seekBarScanDuration.setProgress(duration - MIN_DURATION_SCANNING_SECONDS);

		BluetoothPowerLevel selectedLevel = appConfigManager.getBluetoothPowerLevel();
		spinnerPowerLevel.setSelection(selectedLevel.ordinal());
	}

	private int getScanInterval() {
		return seekBarScanInterval.getProgress() + MIN_INTERVAL_SCANNING_SECONDS;
	}

	private void setScanInterval(int interval) {
		AppConfigManager.getInstance(getContext()).setScanInterval(interval * 1000);
	}

	private void setScanDuration(int duration) {
		AppConfigManager.getInstance(getContext()).setScanDuration(duration * 1000);
	}

	private void setAdvertPowerLevel(BluetoothPowerLevel powerLevel) {
		AppConfigManager.getInstance(getContext()).setBluetoothPowerLevel(powerLevel);
	}

	private void hideKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
