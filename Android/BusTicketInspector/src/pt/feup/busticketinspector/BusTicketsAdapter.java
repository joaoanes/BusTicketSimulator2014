package pt.feup.busticketinspector;

import java.util.ArrayList;
import java.util.Date;
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

public class BusTicketsAdapter extends ArrayAdapter<Ticket>{
	int layout_id;
	ArrayList<Ticket> tickets;

	public BusTicketsAdapter(Context context, int resource, List<Ticket> objects) {
		super(context, resource, objects);
		this.layout_id = resource;
		this.tickets = (ArrayList<Ticket>) objects;
	}
	
	@SuppressWarnings("deprecation")
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
		Date when = new Date(ticket.getValidated().getTime());
		((TextView) row.findViewById(R.id.textview_ticket_duration)).setText(when.getHours() + ":" + 
				(when.getMinutes() < 10 ? 
						("0" + when.getMinutes()) : when.getMinutes()));
		((TextView) row.findViewById(R.id.textview_ticket_bus)).setText("On bus " + String.valueOf(ticket.getBus()));

		return row;
	}

}