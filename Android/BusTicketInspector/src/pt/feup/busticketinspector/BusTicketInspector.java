package pt.feup.busticketinspector;

import java.util.ArrayList;

import pt.feup.busticket.tickets.Ticket;

import android.app.Application;

public class BusTicketInspector extends Application {
	boolean in_select_layout = true;
	int bus_id = -1;
	ArrayList<Ticket> bus_validated_tickets = null;

}
