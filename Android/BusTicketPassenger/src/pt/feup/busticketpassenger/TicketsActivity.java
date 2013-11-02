package pt.feup.busticketpassenger;

import org.apache.http.HttpStatus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.ClientSocket;
import pt.feup.busticket.tickets.HttpHelper;
import pt.feup.busticket.tickets.Ticket;
import pt.feup.busticketpassenger.ChangeIPAndPortDialogFragment.ChangeIPAndPortDialogListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
		
		if(!app.cached) {
			GetTicketsTask task = new GetTicketsTask();
			task.execute(new Void[]{});
			app.cached = true;
		}
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
		String serialized_ticket;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			serialized_ticket = app.selected_ticket.serialize();
		}
		
		@Override
		protected String doInBackground(Void... v) {
			Log.i("send",serialized_ticket);
			return ClientSocket.sendAndWait(app.bus_ip, app.bus_port, serialized_ticket);
		}

		@Override
		protected void onPostExecute(String result) {
			if(result == null) {
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Validate Ticket", "An error occured");
			}
		
			String result_arr[] = result.split("\\|");
			
			if(result_arr[0].equals("error")) {
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Validate Ticket", "An error occured");
			}
			else if(result_arr[0].equals("validated")) {
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Validate Ticket", "The ticket was validated");
				
				int bus_id = Integer.parseInt(result_arr[1]);
				app.validateTicket(bus_id);
				adapter.notifyDataSetChanged();
				updateTicketsQuantityViews();
				
			}
			else if(result_arr[0].equals("invalid")) {
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Validate Ticket", "The ticket was invalid");
			}
		}

	}

}
