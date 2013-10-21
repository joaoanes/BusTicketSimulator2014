package pt.feup.busticket.tickets;

public class T2 extends Ticket {

	public T2(int id) {
		super(id);
	}

	@Override
	public double getPrice() {
		return 1;
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