package pt.feup.stockportfolio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashActivity extends Activity {

	boolean canProceed = true;

	public class QuoteUpdateCreator extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			HttpHelper http = new HttpHelper();
			Looper.prepare();
			for (Quote q : Utils.myQuotes)
			{
				double v = 0.0;
				try 
				{
					v = http.getTickValue(q.tick).value;
				} 
				catch (NoInternetException e) {
					// TODO Auto-generated catch block
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showToastAndDie();
							

						}

					});
					return null;
				} 
				if (v != q.value)
					Utils.myUpdates.add(new QuoteUpdate(q, v));
			}
			return null;
		}

	}

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

		try {
			Void[] args = {};
			new QuoteUpdateCreator().execute(args).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Handler().postDelayed(new Runnable() {



			@Override
			public void run() {
			
				Intent i = new Intent(SplashActivity.this, QuotesActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				
				onDestroy();
				finish();
			}
		}, 5000);
	}

	void showToastAndDie()
	{
		Toast.makeText(this, "Connection problem.", Toast.LENGTH_SHORT).show();
		for (Quote q : Utils.myQuotes)
			q.isUpdated = true;
	}

}