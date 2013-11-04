package pt.feup.busticketpassenger;

import java.util.ArrayList;
import java.util.List;

import pt.feup.busticket.tickets.T1;
import pt.feup.busticket.tickets.T2;
import pt.feup.busticket.tickets.T3;
import pt.feup.busticket.tickets.Ticket;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
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
		
	
		((TextView) row.findViewById(R.id.textview_ticket_type)).setText(ticket.getType());
		
		String color = "#ff0000";
		if (ticket instanceof T1)
		{
			color = "#A4BAA2";
		}
		else if (ticket instanceof T2)
		{
			color = "#569492";
		}
		else if (ticket instanceof T3)
		{
			color = "#41505E";
		}
		((RelativeLayout) row.findViewById(R.id.square)).setBackgroundColor(Color.parseColor(color));
		
		((TextView) row.findViewById(R.id.textview_ticket_duration)).setText(ticket.getValidated().getHours() + ":" + 
				(ticket.getValidated().getMinutes() < 10 ? 
						("0" + ticket.getValidated().getMinutes()) : ticket.getValidated().getMinutes()));
		((TextView) row.findViewById(R.id.textview_ticket_bus)).setText("On bus " + String.valueOf(ticket.getBus()));

		return row;
	}

}


