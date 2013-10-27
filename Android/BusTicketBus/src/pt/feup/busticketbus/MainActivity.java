package pt.feup.busticketbus;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.SimpleServer;
import pt.feup.busticket.tickets.SimpleServer.SimpleServerListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SimpleServerListener, OnCancelListener {
	int port = 6000;
	//ServerSocket server;
	//Socket client;
	SimpleServer server;
	
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dialog = BusTicketUtils.createProgressDialog(this, "waiting for connection", this);
		server = new SimpleServer(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class ValidateTicketThread implements Runnable {

		@Override
		public void run() {
			server.processRequest();
		}
	}

	public void openServer(View view) {
		/*if(server.isOpen()) {
			server.closeConnection(true);
			((TextView) findViewById(R.id.status_id)).setText("Server force close");
		}
		else {*/
			((TextView) findViewById(R.id.status_id)).setText("Server open");
			dialog.show();
			(new Thread(new ValidateTicketThread())).start();
		//}
	}
	
	@Override
	public String processInput(String input) {
		final String test = input;
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, test, Toast.LENGTH_SHORT).show();
				
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
					BusTicketUtils.createAlertDialog(MainActivity.this, "Cenas", "forced");
				}
			});
			
		}
		
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		server.closeConnection(true);
		((TextView) findViewById(R.id.status_id)).setText("Server force close");
	}
	
	

}
