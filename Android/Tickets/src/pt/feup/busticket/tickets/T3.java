package pt.feup.busticket.tickets;

public class T3 extends Ticket {

	public T3(String id) {
		super(id);
	}

	@Override
	public double getPrice() {
		return 0.45;
	}

	@Override
	public String getType() {
		return "T3";
	}

	@Override
	public int getDuration() {
		return 60;
	}

}
