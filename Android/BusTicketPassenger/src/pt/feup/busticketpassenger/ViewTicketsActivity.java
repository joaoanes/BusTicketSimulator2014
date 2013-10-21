package pt.feup.busticketpassenger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import pt.feup.busticket.tickets.T1;
import pt.feup.busticket.tickets.Ticket;
import pt.feup.busticketpassenger.ChangeIPAndPortDialogFragment.ChangeIPAndPortDialogListener;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ViewTicketsActivity extends ListActivity implements ChangeIPAndPortDialogListener {
	TicketAdapter adapter;
	BusTicketPassenger app;
	Ticket selected_ticket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_tickets);
		
		app = (BusTicketPassenger) getApplication();
		setTicketAdapter();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_tickets, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.ticket, menu);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		selected_ticket = app.t1_tickets.get(position);
		openContextMenu(v);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.ticket_validate:
				Toast.makeText(this, String.valueOf(selected_ticket.getId()), Toast.LENGTH_SHORT).show();
				
				ChangeIPAndPortDialogFragment dialog = new ChangeIPAndPortDialogFragment();
				
				Bundle args = new Bundle();
				args.putString(ChangeIPAndPortDialogFragment.DEVICE_ID, ChangeIPAndPortDialogFragment.Device.BUS.toString());
				dialog.setArguments(args);
				
				dialog.show(getFragmentManager(), "change bus ip and port");
				return true;
	
			default:
				return super.onContextItemSelected(item);
		}
	}

	void setTicketAdapter() {
		app.t1_tickets.add(new T1(5));
		app.t1_tickets.add(new T1(9));
		app.t1_tickets.add(new T1(7));
		app.t1_tickets.add(new T1(3));
		app.t1_tickets.add(new T1(4));
		adapter = new TicketAdapter(this, R.layout.row_ticket, app.t1_tickets);
		setListAdapter(adapter);
	}

	@Override
	public void onPositiveClick(DialogFragment dialog) {
		SendTicketToBusTask task = new SendTicketToBusTask();
		task.execute(new Ticket[]{});
	}
	
	private class SendTicketToBusTask extends AsyncTask<Ticket, Void, String> {
		InetAddress busAddr;
		Socket socket;
		PrintWriter out;
		BufferedReader in;
		@Override
		protected String doInBackground(Ticket... tickets) {
			//Ticket ticket = tickets[0]; 
			try {
				busAddr = InetAddress.getByName(app.bus_ip);
				Log.i("passenger","before socket");
				socket = new Socket(busAddr, app.bus_port);
				Log.i("passenger","before print");
				out = new PrintWriter(socket.getOutputStream(),true);
				Log.i("passenger","before buffered");
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				Log.i("passenger","before write");
				out.println("world hello");
				
				Log.i("passenger","before read");
				String line;
				while((line = in.readLine()) != null) {
					Log.i("view tickets",line);
					return line;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			Toast.makeText(ViewTicketsActivity.this, result, Toast.LENGTH_SHORT).show();
		}
		
	}

}
