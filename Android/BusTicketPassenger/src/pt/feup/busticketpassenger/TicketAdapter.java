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

public class TicketAdapter extends ArrayAdapter<Ticket>{
	Context context;
	int layout_id;
	ArrayList<Ticket> tickets;

	public TicketAdapter(Context context, int resource, List<Ticket> objects) {
		super(context, resource, objects);
		
		this.context = context;
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

		return row;
	}

}


