package pt.feup.busticketpassenger;

import java.util.ArrayList;

import pt.feup.busticket.tickets.Ticket;
import android.app.Application;
import android.util.SparseArray;

public class BusTicketPassenger extends Application {
	SparseArray<Ticket> bought_tickets = new SparseArray<Ticket>();
	ArrayList<Ticket> t1_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> t2_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> t3_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> validated_tickets = new ArrayList<Ticket>();
	Ticket selected_ticket = null;
	Ticket selected_validated_ticket = null;
	String token = null;
	
	String bus_ip = "10.0.2.2";
	int bus_port = 5000;
	
	String server_ip = "joaoanes.no-ip.biz";
	int server_port = 8080;
	
	String inspector_ip = "10.0.2.2";
	int inspector_port = 3000;
	
	int getT1Size() {
		return t1_tickets.size();
	}
	
	int getT2Size() {
		return t2_tickets.size();
	}
	
	int getT3Size() {
		return t3_tickets.size();
	}
	
	Ticket getT1Ticket() {
		return t1_tickets.get(0);
	}
	
	Ticket getT2Ticket() {
		return t2_tickets.get(0);
	}
	
	Ticket getT3Ticket() {
		return t3_tickets.get(0);
	}
}
