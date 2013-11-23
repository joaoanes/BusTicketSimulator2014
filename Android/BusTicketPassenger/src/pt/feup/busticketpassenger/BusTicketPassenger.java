package pt.feup.busticketpassenger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import pt.feup.busticket.tickets.Ticket;
import android.app.Application;

public class BusTicketPassenger extends Application {
	HashMap<String, Ticket> bought_tickets = new HashMap<String, Ticket>();
	ArrayList<Ticket> t1_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> t2_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> t3_tickets = new ArrayList<Ticket>();
	ArrayList<Ticket> validated_tickets = new ArrayList<Ticket>();
	Ticket selected_ticket = null;
	Ticket selected_validated_ticket = null;
	String token = null;
	boolean cached = false;
	
	String bus_ip = "10.0.2.2";
	int bus_port = 5000;
	
	String server_ip = "joaoanes.no-ip.biz";
	//String server_ip = "10.0.2.2";
	int server_port = 8080;
	
	String inspector_ip = "10.0.2.2";
	int inspector_port = 6000;
	
	void reset() {
		bought_tickets = new HashMap<String, Ticket>();
		t1_tickets = new ArrayList<Ticket>();
		t2_tickets = new ArrayList<Ticket>();
		t3_tickets = new ArrayList<Ticket>();
		validated_tickets = new ArrayList<Ticket>();
		selected_ticket = null;
		selected_validated_ticket = null;
		cached = false;
	}
	
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
	
	public boolean processJSONTickets(String json) {
		ArrayList<Ticket> tickets = Ticket.getTicketsFromJSON(json);
		if(tickets == null) {
			return false;
		}

		for(Ticket ticket : tickets) {
			if(!ticket.isValidated()) {
				bought_tickets.put(ticket.getId(), ticket);

				if(ticket.getType().equals("T1")) {
					t1_tickets.add(ticket);
				}
				else if(ticket.getType().equals("T2")) {
					t2_tickets.add(ticket);
				}
				else if(ticket.getType().equals("T3")) {
					t3_tickets.add(ticket);
				}
			}
			else {
				validated_tickets.add(ticket);
			}
		}
		return true;
	}
	
	public void validateTicket(int bus_id) {
		Ticket ticket = selected_ticket;
		bought_tickets.remove(ticket.getId());
		
		if(ticket.getType().equals("T1")) {
			t1_tickets.remove(ticket);
		}
		else if(ticket.getType().equals("T2")) {
			t2_tickets.remove(ticket);
		}
		else if(ticket.getType().equals("T3")) {
			t3_tickets.remove(ticket);
		}
		ticket.setBus(bus_id);
		ticket.setValidated(new Date());
		validated_tickets.add(ticket);
	}
}
