package pt.feup.busticketpassenger;

import java.util.Date;

import org.apache.http.HttpStatus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.ClientSocket;
import pt.feup.busticket.tickets.HttpHelper;
import pt.feup.busticket.tickets.T1;
import pt.feup.busticket.tickets.T2;
import pt.feup.busticket.tickets.T3;
import pt.feup.busticketpassenger.ChangeIPAndPortDialogFragment.ChangeIPAndPortDialogListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class TicketsActivity extends Activity implements ChangeIPAndPortDialogListener {
	BusTicketPassenger app;
	TicketAdapter adapter;
	ListView validated_tickets;

	TextView t1_quantity_view;
	TextView t2_quantity_view;
	TextView t3_quantity_view;
	
	ProgressDialog progress_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

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
		Date hello = new Date();
		T1 a = new T1("lolid");
		
		T2 b = new T2("lolid");
		T3 c = new T3("lolid");
		T1 d = new T1("lolid");
		
		a.setValidated(hello);
		b.setValidated(hello);
		c.setValidated(hello);
		d.setValidated(hello);
		
		app.validated_tickets.add(a);
		app.validated_tickets.add(b);
		app.validated_tickets.add(c);
		app.validated_tickets.add(d);
		

		
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
				ChangeIPAndPortDialogFragment dialog = new ChangeIPAndPortDialogFragment();
	
				Bundle args = new Bundle();
				args.putString(ChangeIPAndPortDialogFragment.DEVICE_ID, ChangeIPAndPortDialogFragment.Device.INSPECTOR.toString());
				dialog.setArguments(args);
	
				dialog.show(getFragmentManager(), "change inspector ip and port");
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
				if(app.getT1Size() > 0) {
					app.selected_ticket = app.getT1Ticket();
				}
				else {
					app.selected_ticket = null;
					return;
				}
				break;
			case R.id.button_validate_t2:
				if(app.getT2Size() > 0) {
					app.selected_ticket = app.getT2Ticket();
				}
				else {
					app.selected_ticket = null;
					return;
				}
				break;
			case R.id.button_validate_t3:
				if(app.getT3Size() > 0) {
					app.selected_ticket = app.getT3Ticket();
				}
				else {
					app.selected_ticket = null;
					return;
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
	public void onBusPositiveClick() {
		SendTicketToBusTask task = new SendTicketToBusTask();
		task.execute(new Void[]{});
	}
	
	@Override
	public void onInspectorPositiveClick() {
		SendTicketToInspectorTask task = new SendTicketToInspectorTask();
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
					adapter.notifyDataSetChanged();
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
			serialized_ticket = app.selected_ticket.serialize();
		}
		
		@Override
		protected String doInBackground(Void... v) {
			return ClientSocket.sendAndWait(app.bus_ip, app.bus_port, serialized_ticket);
		}

		@Override
		protected void onPostExecute(String result) {
			if(result == null) {
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Validate Ticket", "An error occured");
				return;
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
	
	private class SendTicketToInspectorTask extends AsyncTask<Void, Void, String> {
		String serialized_ticket;
		@Override
		protected void onPreExecute() {
			serialized_ticket = app.selected_validated_ticket.serialize();
			progress_dialog = BusTicketUtils.createProgressDialog(TicketsActivity.this, "Sending Ticket to Inspector");
			progress_dialog.show();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			return ClientSocket.sendAndWait(app.inspector_ip, app.inspector_port, serialized_ticket);
		}

		@Override
		protected void onPostExecute(String result) {
			progress_dialog.dismiss();
			if(result == null) {
				BusTicketUtils.createAlertDialog(TicketsActivity.this, "Verify Ticket", "An error occured");
				return;
			}
		
			String result_arr[] = result.split("\\|");
			String status = result_arr[0];
			String message = result_arr[1];
			
			if(status.equals("invalid_ticket") || status.equals("invalid_date")) {
				adapter.remove(app.selected_validated_ticket);
				app.selected_ticket = null;
			}
			
			BusTicketUtils.createAlertDialog(TicketsActivity.this, "Verify Ticket", message);
		}
		
	}

}
