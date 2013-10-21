package pt.feup.busticketpassenger;

import java.util.ArrayList;
import java.util.List;

import pt.feup.busticket.tickets.Ticket;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TicketAdapter extends ArrayAdapter<Ticket>{
	int layout_id;
	ArrayList<Ticket> tickets;

	public TicketAdapter(Context context, int resource, List<Ticket> objects) {
		super(context, resource, objects);
		this.layout_id = resource;
		this.tickets = (ArrayList<Ticket>) objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater =  ((Activity) getContext()).getLayoutInflater();
			row = inflater.inflate(layout_id, null); // get our custom layout
		}
		Ticket ticket = tickets.get(position);
		
		((TextView) row.findViewById(R.id.textview_ticket_id)).setText(String.valueOf(ticket.getId()));
		((TextView) row.findViewById(R.id.textview_ticket_duration)).setText(String.valueOf(ticket.getDuration()));
		((TextView) row.findViewById(R.id.textview_ticket_price)).setText(String.valueOf(ticket.getPrice()));

		return row;
	}

}


