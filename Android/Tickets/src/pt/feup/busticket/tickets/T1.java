package pt.feup.busticket.tickets;

public class T1 extends Ticket {

	public T1(int id) {
		super(id);
	}

	@Override
	public double getPrice() {
		return 0.50;
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
