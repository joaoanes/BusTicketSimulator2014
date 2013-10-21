package pt.feup.busticketpassenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.feup.busticketpassenger.TicketQuantityDialogFragment.TicketQuantityDialogListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BuyActivity extends Activity implements TicketQuantityDialogListener{
	public static final String QUANTITY_ID = "QUANTITY_ID";

	TextView t1_textview;
	TextView t2_textview;
	TextView t3_textview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		t1_textview = (TextView) findViewById(R.id.t1_quantity);
		t2_textview = (TextView) findViewById(R.id.t2_quantity);
		t3_textview = (TextView) findViewById(R.id.t3_quantity);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buy, menu);
		return true;
	}

	public void changeQuantity(View view) {

		TicketQuantityDialogFragment dialog = new TicketQuantityDialogFragment();

		int id = view.getId();

		Bundle args = new Bundle();
		args.putInt(QUANTITY_ID, id);
		dialog.setArguments(args);

		dialog.show(getFragmentManager(), "ticket quantity");
	}

	@Override
	public void onPositiveClick(DialogFragment dialog, int quantity, int button_id) {
		String qnt = String.valueOf(quantity);

		switch (button_id) {
		case R.id.button_change_t1:
			t1_textview.setText(qnt);
			break;
		case R.id.button_change_t2:
			t2_textview.setText(qnt);
			break;
		case R.id.button_change_t3:
			t3_textview.setText(qnt);
			break;
		default:
			return;
		}		
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
