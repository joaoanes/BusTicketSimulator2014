package pt.feup.busticketpassenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BuyActivity extends Activity {
	public static final String QUANTITY_ID = "QUANTITY_ID";

	TextView t1_textview;
	TextView t2_textview;
	TextView t3_textview;
	
	Spinner t1_spinner;
	Spinner t2_spinner;
	Spinner t3_spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		
		t1_spinner = (Spinner) findViewById(R.id.t1_quantity);
		t2_spinner = (Spinner) findViewById(R.id.t2_quantity);
		t3_spinner = (Spinner) findViewById(R.id.t3_quantity);
		
		Integer[] items = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		
		ArrayAdapter<CharSequence> t1_adapter = ArrayAdapter.createFromResource(this, R.array.tickets_quantity, android.R.layout.simple_spinner_item);
		t1_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		t1_spinner.setAdapter(t1_adapter);
		
		ArrayAdapter<CharSequence> t2_adapter = ArrayAdapter.createFromResource(this, R.array.tickets_quantity, android.R.layout.simple_spinner_item);
		t2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		t2_spinner.setAdapter(t2_adapter);

		ArrayAdapter<CharSequence> t3_adapter = ArrayAdapter.createFromResource(this, R.array.tickets_quantity, android.R.layout.simple_spinner_item);
		t3_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		t3_spinner.setAdapter(t3_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buy, menu);
		return true;
	}

	public void buyTickets(View view) {
		BuyTicketsTask task = new BuyTicketsTask();
		task.execute(new Void[]{});

	}

	private class BuyTicketsTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				URL url = new URL("http://ip.jsontest.com/?callback=showMyIP");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				InputStream in = con.getInputStream();
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(in));
					String line;
					String ret = "receiver:";
					while ((line = reader.readLine()) != null) {
						ret += line;
					}
					return ret;
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return "error";
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(BuyActivity.this, result, Toast.LENGTH_SHORT).show();
		}

	}

}
