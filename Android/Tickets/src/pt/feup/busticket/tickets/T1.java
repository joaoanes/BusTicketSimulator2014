package pt.feup.busticket.tickets;

public class T1 extends Ticket {

	public T1(String id) {
		super(id);
	}

	@Override
	public double getPrice() {
		return 0.25;
	}

	@Override
	public String getType() {
		return "T1";
	}

	@Override
	public int getDuration() {
		return 15;
	}

}
