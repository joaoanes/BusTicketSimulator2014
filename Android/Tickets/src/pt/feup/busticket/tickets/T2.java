package pt.feup.busticket.tickets;

public class T2 extends Ticket {

	public T2(String id) {
		super(id);
	}

	@Override
	public double getPrice() {
		return 0.35;
	}

	@Override
	public String getType() {
		return "T2";
	}

	@Override
	public int getDuration() {
		return 30;
	}

}
