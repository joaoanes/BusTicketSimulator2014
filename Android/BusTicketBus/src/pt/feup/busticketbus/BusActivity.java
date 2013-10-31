package pt.feup.busticketbus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.SimpleServer;
import pt.feup.busticket.tickets.SimpleServer.SimpleServerListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BusActivity extends Activity implements SimpleServerListener, OnCancelListener {
	int port = 6000;
	//ServerSocket server;
	//Socket client;
	SimpleServer server;
	
	ProgressDialog dialog;
	
	LinearLayout main_layout;
	LinearLayout select_layout;
	EditText bus_id_edittext;
	
	BusTicketBus app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		
		app = (BusTicketBus) getApplication();
		
		dialog = BusTicketUtils.createProgressDialog(this, "waiting for connection", this);
		server = new SimpleServer(this);
		
		main_layout = (LinearLayout) findViewById(R.id.bus_main_layout);
		select_layout = (LinearLayout) findViewById(R.id.bus_select_layout);
		bus_id_edittext = (EditText) findViewById(R.id.bus_id_edittext);
		
		if(app.in_select_layout) {
			showSelectLayout();
		}
		else {
			showMainLayout();
		}
	}
	
	public void showSelectLayout() {
		app.in_select_layout = true;
		select_layout.setVisibility(View.VISIBLE);
		main_layout.setVisibility(View.GONE);
	}
	
	public void showMainLayout() {
		app.in_select_layout = false;
		select_layout.setVisibility(View.GONE);
		main_layout.setVisibility(View.VISIBLE);
	}
	
	public void onSelectBusButtonClick(View view) {
		try {
			app.bus_id = Integer.parseInt(bus_id_edittext.getText().toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(app.bus_id > -1) {
			Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
			showMainLayout();
		}
		else {
			Toast.makeText(this, String.valueOf(app.bus_id), Toast.LENGTH_SHORT).show();
		}
	}

	class ValidateTicketThread implements Runnable {

		@Override
		public void run() {
			server.processRequest();
		}
	}

	public void openServer(View view) {
		((TextView) findViewById(R.id.status_id)).setText("Server open");
		dialog.show();
		(new Thread(new ValidateTicketThread())).start();
	}
	
	@Override
	public String processInput(String input) {
		final String test = input;
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(BusActivity.this, test, Toast.LENGTH_SHORT).show();
				
			}
		});
		return "hello world";
	}

	@Override
	public void onServerClose(boolean forced) {
		dialog.dismiss();
		if(!forced) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					((TextView) findViewById(R.id.status_id)).setText("Server Closed");
					
				}
			});
		}
		else {
			runOnUiThread(new Runnable() {
				public void run() {
					BusTicketUtils.createAlertDialog(BusActivity.this, "Cenas", "forced");
				}
			});
			
		}
		
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		server.closeConnection(true);
		((TextView) findViewById(R.id.status_id)).setText("Server force close");
	}
}
