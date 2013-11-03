package pt.feup.busticketinspector;

import java.util.Date;

import org.apache.http.HttpStatus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.HttpHelper;
import pt.feup.busticket.tickets.HttpHelper.HttpResult;
import pt.feup.busticket.tickets.SimpleServer;
import pt.feup.busticket.tickets.SimpleServer.SimpleServerListener;
import pt.feup.busticket.tickets.Ticket;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InspectorActivity extends Activity implements SimpleServerListener, OnCancelListener {
	int port = 7000;
	SimpleServer server;
	
	LinearLayout select_bus_layout;
	LinearLayout bus_tickets_layout;
	EditText bus_edit_text;
	BusTicketInspector app;
	TextView bus_title;
	ListView validated_tickets_list;
	
	BusTicketsAdapter adapter;
	
	ProgressDialog progress_dialog;
	
	String status, message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspector);
		
		select_bus_layout = (LinearLayout) findViewById(R.id.inspector_select_bus);
		bus_tickets_layout = (LinearLayout) findViewById(R.id.inspector_bus_tickets);
		bus_edit_text = (EditText) findViewById(R.id.edittext_bus_id);
		bus_title = (TextView) findViewById(R.id.title_bus);
		
		app = (BusTicketInspector) getApplication();
		
		setListView();
		
		server = new SimpleServer(this, port);
		
		if(app.in_select_layout) {
			showSelectBusLayout();
		}
		else {
			showBusTicketsLayout();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inspector, menu);
		return true;
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		getMenuInflater().inflate(R.menu.ticket, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		long date_millis = app.selected_ticket.getValidated().getTime();
		
		switch (item.getItemId()) {
			case R.id.remove_15_min:
				app.selected_ticket.setValidated(new Date(date_millis - 15*60000));
				break;
			case R.id.remove_30_min:
				app.selected_ticket.setValidated(new Date(date_millis - 30*60000));
				break;
			case R.id.remove_60_min:
				app.selected_ticket.setValidated(new Date(date_millis - 60*60000));
				break;
			case R.id.remove_ticket:
				app.bus_validated_tickets.remove(app.selected_ticket.getId());
				adapter.remove(app.selected_ticket);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
		
		adapter.notifyDataSetChanged();
		return true;
	}
	
	public void setListView() {
		validated_tickets_list = (ListView) findViewById(R.id.bus_validated_tickets);
		
		validated_tickets_list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				app.selected_ticket = app.tickets_array.get(position);
				openContextMenu(view);
			}
		});
		
		adapter = new BusTicketsAdapter(this, R.layout.row_ticket, app.tickets_array);
		validated_tickets_list.setAdapter(adapter);
		registerForContextMenu(validated_tickets_list);
	}
	
	void showSelectBusLayout() {
		app.in_select_layout = true;
		select_bus_layout.setVisibility(View.VISIBLE);
		bus_tickets_layout.setVisibility(View.GONE);
	}
	
	void showBusTicketsLayout() {
		app.in_select_layout = false;
		bus_title.setText("Bus " + app.bus_id);
		select_bus_layout.setVisibility(View.GONE);
		bus_tickets_layout.setVisibility(View.VISIBLE);
		
		GetBusTicketsTask task = new GetBusTicketsTask();
		task.execute(new Void[]{});
	}
	
	public void getBusTicketsButton(View view) {
		try {
			app.bus_id = Integer.parseInt(bus_edit_text.getText().toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(app.bus_id > -1) {
			showBusTicketsLayout();
		}
		else {
			Toast.makeText(this, String.valueOf(app.bus_id), Toast.LENGTH_SHORT).show();
		}
	}
	
	private class GetBusTicketsTask extends AsyncTask<Void, Void, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = BusTicketUtils.createProgressDialog(InspectorActivity.this, "Retriving tickets from bus " + app.bus_id);
			progress_dialog.show();			
		}

		@Override
		protected HttpResult doInBackground(Void... arg0) {
			HttpHelper helper = new HttpHelper();
			return helper.getBusTickets(app.bus_id);
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			progress_dialog.dismiss();
			
			switch(result.getCode()) {
				case HttpStatus.SC_OK:
					if(app.processJSONBusTickets(result.getResult())) {
						adapter.notifyDataSetChanged();
					}
					else {
						BusTicketUtils.createAlertDialog(InspectorActivity.this, "cenas", result.toString());
					}
					break;
				default:
					BusTicketUtils.createAlertDialog(InspectorActivity.this, "cenas", result.toString());
					break;
			}
		}
		
	}
	
	public void onReceiveTicketClick(View view) {
		progress_dialog = BusTicketUtils.createProgressDialog(this, "Waiting for ticket", this);
		progress_dialog.show();
		(new Thread(new VerifyUserTicket())).start();
	}
	
	private class VerifyUserTicket implements Runnable {

		@Override
		public void run() {
			server.processRequest();
		}
		
	}
	
	@Override
	public void onCancel(DialogInterface arg0) {
		server.closeConnection(true);
	}
	
	@Override
	public String processInput(String input) {
		Ticket ticket = Ticket.deserialize(input);
		
		if(ticket.getBus() != app.bus_id) {
			status = "invalid_bus";
			message = "The ticket was not validated on this bus";
		}
		else if(!app.bus_validated_tickets.containsKey(ticket.getId())) {
			status = "invalid_ticket";
			message = "The ticket is invalid";
		}
		else {
			Ticket compare = app.bus_validated_tickets.get(ticket.getId());
			long current_time = (new Date()).getTime();
			
			if(compare.getValidated().getTime() < current_time) {
				status = "invalid_date";
				message = "The ticket has expired";
			}
			else {
				status = "valid";
				message = "The ticket is valid";
			}
		}
		
		return status + "|" + message;
	}

	@Override
	public void onServerClose(boolean forced) {
		progress_dialog.dismiss();
		if(!forced) {
			runOnUiThread(new Runnable() {
				public void run() {
					BusTicketUtils.createAlertDialog(InspectorActivity.this, "Verify Ticket", message);
				}
			});
		}
		else {
			runOnUiThread(new Runnable() {
				public void run() {
					BusTicketUtils.createAlertDialog(InspectorActivity.this, "Verify Ticket", "The connection was closed");
				}
			});
			
		}
		
	}

}
