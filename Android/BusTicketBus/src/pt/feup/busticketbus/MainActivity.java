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

public class MainActivity extends Activity {
	int port = 6000;
	String ip = "10.0.2.2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
			try {
				Log.i("bus","before open server");
				ServerSocket server = new ServerSocket(port);
				Log.i("bus","before accept connection");
				Socket client = server.accept();
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
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						((TextView) findViewById(R.id.status_id)).setText("Server Closed");
					}
				});
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void openServer(View view) {
		((TextView) findViewById(R.id.status_id)).setText("Server open");
		
		Log.i("bus","before thread");
		(new Thread(new ValidateTicketThread())).start();
	}

}
