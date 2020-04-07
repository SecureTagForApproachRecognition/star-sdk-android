package ch.ubique.android.starsdk.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content),
					(v, insets) -> {
						FrameLayout mainView = findViewById(R.id.main_content);
						FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mainView.getLayoutParams();
						layoutParams.topMargin += insets.getSystemWindowInsetTop();
						layoutParams.bottomMargin += insets.getSystemWindowInsetBottom();
						mainView.setLayoutParams(layoutParams);
						return insets;
					});

			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_fragment_container, HomeFragment.newInstance())
					.commit();
		}
	}

}
