package pt.feup.busticketpassenger;

import org.apache.http.HttpStatus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.HttpHelper;
import pt.feup.busticket.tickets.HttpHelper.HttpResult;
import pt.feup.busticket.tickets.Ticket;
import pt.feup.busticketpassenger.ConfirmBuyDialogFragment.ConfirmBuyDialogListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class BuyActivity extends Activity implements OnItemSelectedListener, ConfirmBuyDialogListener {
	public static final String QUANTITY_ID = "QUANTITY_ID";

	TextView t1_textview;
	TextView t2_textview;
	TextView t3_textview;

	Spinner t1_spinner;
	Spinner t2_spinner;
	Spinner t3_spinner;

	int t1_qt = 0;
	int t2_qt = 0;
	int t3_qt = 0;

	BusTicketPassenger app;
	ProgressDialog progress_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_buy);

		app = (BusTicketPassenger) getApplication();

		instantiateSpinners();
		
		progress_dialog = BusTicketUtils.createProgressDialog(this, "Connecting with the server");
	}

	@Override
	public void onItemSelected(AdapterView<?> parent_view, View selected_view, int position, long id) {
		int value = (Integer) parent_view.getItemAtPosition(position);
		t1_spinner.getId();

		switch (parent_view.getId()) {
		case R.id.t1_quantity:
			t1_qt = value;
			break;
		case R.id.t2_quantity:
			t2_qt = value;
			break;
		case R.id.t3_quantity:
			t3_qt = value;
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	public void instantiateSpinners() {
		t1_spinner = (Spinner) findViewById(R.id.t1_quantity);
		t2_spinner = (Spinner) findViewById(R.id.t2_quantity);
		t3_spinner = (Spinner) findViewById(R.id.t3_quantity);

		int max = 10 - app.getT1Size();
		max = max < 0 ? 0 : max;
		instantiateSpinner(t1_spinner, max);

		max = 10 - app.getT2Size();
		max = max < 0 ? 0 : max;
		instantiateSpinner(t2_spinner, max);

		max = 10 - app.getT3Size();
		max = max < 0 ? 0 : max;
		instantiateSpinner(t3_spinner, max);
	}

	public void instantiateSpinner(Spinner spinner, int max) {
		Integer[] items = new Integer[max+1];
		for(int i = 0; i <= max; ++i ) {
			items[i] = i;
		}

		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	public void buyTickets(View view) {
		if(t1_qt > 0 || t2_qt > 0 || t3_qt > 0) {
			progress_dialog.show();
			
			BuyTicketsTask task = new BuyTicketsTask();
			task.execute(new Void[]{});
		}
		else {
			BusTicketUtils.createAlertDialog(this, "Buy", "Buy 1 or more tickets");
		}

	}

	private class BuyTicketsTask extends AsyncTask<Void, Void, HttpResult> {

		@Override
		protected HttpResult doInBackground(Void... params) {
			HttpHelper helper = new HttpHelper();
			return helper.buyTickets(app.token, t1_qt, t2_qt, t3_qt, false);
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			progress_dialog.dismiss();
			switch(result.getCode()) {
				case HttpStatus.SC_OK:
					Bundle confirm = Ticket.getBuyResults(result.result);
					
					ConfirmBuyDialogFragment dialog = new ConfirmBuyDialogFragment();
					dialog.setArguments(confirm);
					dialog.show(getFragmentManager(), "confirm buy");
					return;
				default:
					BusTicketUtils.createAlertDialog(BuyActivity.this, "buy", result.toString());
					return;
			}
		}

	}

	@Override
	public void confirmBuy() {
		progress_dialog.show();
		ConfirmBuyTicketsTask task = new ConfirmBuyTicketsTask();
		task.execute(new Void[]{});

	}
	
	private class ConfirmBuyTicketsTask extends AsyncTask<Void, Void, HttpResult> {

		@Override
		protected HttpResult doInBackground(Void... params) {
			HttpHelper helper = new HttpHelper();
			return helper.buyTickets(app.token, t1_qt, t2_qt, t3_qt, true);
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			progress_dialog.dismiss();
			switch(result.getCode()) {
				case HttpStatus.SC_OK:
					boolean success = app.processJSONTickets(result.getResult());
					if(!success) {
						BusTicketUtils.createAlertDialog(BuyActivity.this, "Confirm Buy", result.toString());
					}
					else {
						finish();
					}
					return;
				default:
					BusTicketUtils.createAlertDialog(BuyActivity.this, "buy", result.toString());
					return;
			}
		}

	}

}
