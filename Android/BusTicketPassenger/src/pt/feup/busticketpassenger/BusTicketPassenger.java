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
	ArrayList<T1> t1_tickets = new ArrayList<T1>();
	ArrayList<T2> t2_tickets = new ArrayList<T2>();
	ArrayList<T3> t3_tickets = new ArrayList<T3>();
	SparseArray<Ticket> validated_tickets = new SparseArray<Ticket>();
	Ticket last_validated = null;
}
