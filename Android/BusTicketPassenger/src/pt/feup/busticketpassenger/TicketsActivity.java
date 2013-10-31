package pt.feup.busticketpassenger;

import org.apache.http.HttpStatus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.ClientSocket;
import pt.feup.busticket.tickets.HttpHelper;
import pt.feup.busticket.tickets.T1;
import pt.feup.busticket.tickets.T2;
import pt.feup.busticket.tickets.T3;
import pt.feup.busticketpassenger.ChangeIPAndPortDialogFragment.ChangeIPAndPortDialogListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TicketsActivity extends Activity implements ChangeIPAndPortDialogListener {
	BusTicketPassenger app;
	TicketAdapter adapter;
	ListView validated_tickets;

	TextView t1_quantity_view;
	TextView t2_quantity_view;
	TextView t3_quantity_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tickets);

		app = (BusTicketPassenger) getApplication();

		instantiateTicketsQuantityViews();
		setListView();
		registerForContextMenu(validated_tickets);

		GetTicketsTask task = new GetTicketsTask();
		task.execute(new Void[]{});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateTicketsQuantityViews();
	}

	void instantiateTicketsQuantityViews() {
		t1_quantity_view = (TextView) findViewById(R.id.tickets_t1_quantity);
		t2_quantity_view = (TextView) findViewById(R.id.tickets_t2_quantity);
		t3_quantity_view = (TextView) findViewById(R.id.tickets_t3_quantity);
	}


	void updateTicketsQuantityViews() {
		updateTicketQuantityView(1);
		updateTicketQuantityView(2);
		updateTicketQuantityView(3);
	}

	void updateTicketQuantityView(int id) {
		TextView view;
		int quantity;
		switch (id) {
		case 1:
			view = t1_quantity_view;
			quantity = app.getT1Size();
			break;
		case 2:
			view = t2_quantity_view;
			quantity = app.getT2Size();
			break;
		case 3:
			view = t3_quantity_view;
			quantity = app.getT3Size();
			break;
		default:
			return;
		}
		String text = quantity + " ticket";
		if(quantity != 1) {
			text += "s";
		}
		view.setText(text);
	}


	void setListView() {
		validated_tickets = (ListView) findViewById(R.id.tickets_validated);

		validated_tickets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				app.selected_validated_ticket = app.validated_tickets.get(position);
				openContextMenu(view);
			}

		});

		setTicketAdapter();		
	}

	void setTicketAdapter() {
		T1 t1 = new T1("5");
		t1.setBus(36);
		T2 t2 = new T2("9");
		t2.setBus(67);
		T3 t3 = new T3("13");
		t3.setBus(34);
		T3 t4 = new T3("87");
		t4.setBus(45);
		app.validated_tickets.add(t1);
		app.validated_tickets.add(t2);
		app.validated_tickets.add(t3);
		app.validated_tickets.add(t4);
		adapter = new TicketAdapter(this, R.layout.row_ticket, app.validated_tickets);
		validated_tickets.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tickets, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if(v == validated_tickets) {
			getMenuInflater().inflate(R.menu.validated_ticket, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ticket_show_inspector:
			Toast.makeText(this,String.valueOf(app.selected_validated_ticket.getId()), Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void buyTickets(View view) {
		Intent intent = new Intent(this, BuyActivity.class);
		startActivity(intent);
	}

	public void validateTicket(View view) {
		switch (view.getId()) {
		case R.id.button_validate_t1:
			Toast.makeText(this, "T1", Toast.LENGTH_SHORT).show();
			if(app.getT1Size() > 0) {
				app.selected_ticket = app.getT1Ticket();
			}
			else {
				app.selected_ticket = null;
			}
			break;
		case R.id.button_validate_t2:
			Toast.makeText(this, "T2", Toast.LENGTH_SHORT).show();
			if(app.getT2Size() > 0) {
				app.selected_ticket = app.getT2Ticket();
			}
			else {
				app.selected_ticket = null;
			}
			break;
		case R.id.button_validate_t3:
			Toast.makeText(this, "T3", Toast.LENGTH_SHORT).show();
			if(app.getT3Size() > 0) {
				app.selected_ticket = app.getT3Ticket();
			}
			else {
				app.selected_ticket = null;
			}
			break;
		default:
			return;
		}

		ChangeIPAndPortDialogFragment dialog = new ChangeIPAndPortDialogFragment();

		Bundle args = new Bundle();
		args.putString(ChangeIPAndPortDialogFragment.DEVICE_ID, ChangeIPAndPortDialogFragment.Device.BUS.toString());
		dialog.setArguments(args);

		dialog.show(getFragmentManager(), "change bus ip and port");
	}

	@Override
	public void onPositiveClick(DialogFragment dialog) {
		SendTicketToBusTask task = new SendTicketToBusTask();
		task.execute(new Void[]{});
	}

	private class GetTicketsTask extends AsyncTask<Void, Void, HttpHelper.HttpResult> {

		@Override
		protected HttpHelper.HttpResult doInBackground(Void... params) {
			HttpHelper helper = new HttpHelper();
			return helper.getTickets(app.token);
		}

		@Override
		protected void onPostExecute(HttpHelper.HttpResult result) {
			switch(result.getCode()) {
			case HttpStatus.SC_OK:
				boolean success = app.processJSONTickets(result.getResult());
				if(!success) {
					BusTicketUtils.createAlertDialog(TicketsActivity.this, "Get Tickets", result.toString());
				}
				else {
					updateTicketsQuantityViews();
				}
				break;
			default:
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Get Tickets", result.toString());
				break;
			}
		}

	}

	private class SendTicketToBusTask extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... v) {
			
			return ClientSocket.sendAndWait(app.bus_ip, app.bus_port, "world hello");
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(TicketsActivity.this, result, Toast.LENGTH_SHORT).show();
		}

	}

}
