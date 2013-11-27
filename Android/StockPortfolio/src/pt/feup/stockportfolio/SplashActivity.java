package pt.feup.stockportfolio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);

		Log.i("Load", "Loading quote files");

		try {
			FileInputStream fis = openFileInput(Utils.FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Utils.myQuotes = (ArrayList<Quote>) ois.readObject();
			for(Quote quote : Utils.myQuotes) {
				quote.isUpdated = false;
			}
			ois.close();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.i("Load", "file not found");
		} catch (StreamCorruptedException e) {
			//e.printStackTrace();
			Log.i("Load", "corrupted file");
		} catch (IOException e) {
			//e.printStackTrace();
			Log.i("Load", "IO Exception");
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
			Log.i("Load", "file not found");
		}

		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashActivity.this, QuotesActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}