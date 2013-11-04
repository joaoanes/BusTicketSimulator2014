package pt.feup.busticketinspector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import pt.feup.busticket.tickets.Ticket;
import android.app.Application;
import android.util.Log;

public class BusTicketInspector extends Application {
	boolean in_select_layout = true;
	int bus_id = -1;
	HashMap<String, Ticket> bus_validated_tickets = new HashMap<String, Ticket>();
	ArrayList<Ticket> tickets_array = new ArrayList<Ticket>();
	Ticket selected_ticket;
	
	boolean processJSONBusTickets(String json) {
		ArrayList<Ticket> tickets = Ticket.getBusTicketsFromJSON(json);
		
		if(tickets == null) {
			return false;
		}
		
		for(Ticket ticket : tickets) {
			long end_date_millis = ticket.getValidated().getTime() + ticket.getDuration()*60000;
			ticket.setValidated(new Date(end_date_millis));
			
			bus_validated_tickets.put(ticket.getId(), ticket);
			tickets_array.add(ticket);
		}
		
		return true;
	}

}
