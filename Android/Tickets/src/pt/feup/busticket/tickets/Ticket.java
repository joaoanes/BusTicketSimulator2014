package pt.feup.busticket.tickets;

import java.util.Date;

public abstract class Ticket {
	int id;
	Date validated;
	int bus;
	
	public Ticket(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date isValidated() {
		return validated;
	}

	public void setValidated(Date validated) {
		this.validated = validated;
	}

	public int getBus() {
		return bus;
	}

	public void setBus(int bus) {
		this.bus = bus;
	}
	
	abstract public double getPrice();
	abstract public String getType();
	abstract public int getDuration();
	
}
