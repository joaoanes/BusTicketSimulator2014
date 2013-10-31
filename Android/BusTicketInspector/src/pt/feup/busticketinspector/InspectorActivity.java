package pt.feup.busticketinspector;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.SimpleServer;
import pt.feup.busticket.tickets.SimpleServer.SimpleServerListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	
	ProgressDialog loadingTicketsDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspector);
		
		select_bus_layout = (LinearLayout) findViewById(R.id.inspector_select_bus);
		bus_tickets_layout = (LinearLayout) findViewById(R.id.inspector_bus_tickets);
		bus_edit_text = (EditText) findViewById(R.id.edittext_bus_id);
		bus_title = (TextView) findViewById(R.id.title_bus);
		app = (BusTicketInspector) getApplication();
		
		loadingTicketsDialog = BusTicketUtils.createProgressDialog(this, "Loading some tickets", this);
		
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
	}
	
	public void getBusTicketsButton(View view) {
		//TODO
		try {
			app.bus_id = Integer.parseInt(bus_edit_text.getText().toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(app.bus_id > -1) {
			Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
			showBusTicketsLayout();
			//this.loadingTicketsDialog.show();
		}
		else {
			Toast.makeText(this, String.valueOf(app.bus_id), Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onCancel(DialogInterface arg0) {
		BusTicketUtils.createAlertDialog(this, "GetTickets", "canceled");
		
	}
	
	private class getBusTicketsTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}
	
	@Override
	public String processInput(String input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onServerClose(boolean forced) {
		// TODO Auto-generated method stub
		
	}

}
