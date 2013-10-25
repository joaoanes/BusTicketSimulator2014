package pt.feup.busticketbus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.feup.busticket.tickets.SimpleServer;
import pt.feup.busticket.tickets.SimpleServer.SimpleServerListener;

public class MainActivity extends Activity implements SimpleServerListener {
	int port = 6000;
	//ServerSocket server;
	//Socket client;
	SimpleServer server;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
			/*try {
				Log.i("bus","before open server");
				server = new ServerSocket(port);
				Log.i("bus","before accept connection");
				client = server.accept();
				Log.i("bus","before buffered reader");
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				Log.i("bus","before read");

				String line;
				line = in.readLine();
				Log.i("bus",line);

				Log.i("bus","before write");
				PrintWriter out = new PrintWriter(client.getOutputStream(),true);
				out.println("hello world");

				client.close();
				server.close();

				client = null;
				server = null;

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						((TextView) findViewById(R.id.status_id)).setText("Server Closed");
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

		}
	}

	public void openServer(View view) {
		if(server.isOpen()) {
			server.closeConnection(true);
			((TextView) findViewById(R.id.status_id)).setText("Server force close");
		}
		else {
			((TextView) findViewById(R.id.status_id)).setText("Server open");
			(new Thread(new ValidateTicketThread())).start();
		}
		
		/*if(server == null) {
			((TextView) findViewById(R.id.status_id)).setText("Server open");

			Log.i("bus","before thread");
			(new Thread(new ValidateTicketThread())).start();
		}
		else {
			((TextView) findViewById(R.id.status_id)).setText("Server force close");

			if(client != null) {
				try {
					client.close();
					client = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				server.close();
				server = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
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
		if(!forced) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					((TextView) findViewById(R.id.status_id)).setText("Server Closed");
					
				}
			});
		}
		
	}

}
