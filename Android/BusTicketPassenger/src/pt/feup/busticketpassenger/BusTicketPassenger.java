package pt.feup.busticketpassenger;

import java.util.ArrayList;

import pt.feup.busticket.tickets.T1;
import pt.feup.busticket.tickets.T2;
import pt.feup.busticket.tickets.T3;
import pt.feup.busticket.tickets.Ticket;
import android.app.Application;
import android.util.SparseArray;

public class BusTicketPassenger extends Application {
	SparseArray<Ticket> bought_tickets = new SparseArray<Ticket>();
	ArrayList<Ticket> t1_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> t2_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> t3_tickets = new ArrayList<Ticket>();
	SparseArray<Ticket> validated_tickets = new SparseArray<Ticket>();
	Ticket last_validated = null;
	
	String bus_ip = "127.0.0.1";
	int bus_port = 3000;
	
	String server_ip = "127.0.0.1";
	int server_port = 3000;
	
	String inspector_ip = "127.0.0.1";
	int inspector_port = 3000;
}
