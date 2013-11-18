package pt.feup.stockportfolio;

public class Quote {
	String tick;
	int quantity;
	double value;
	
	public Quote(String tick, int quantity) {
		this.tick = tick;
		this.quantity = quantity;
	}

	public String getTick() {
		return tick;
	}

	public void setTick(String tick) {
		this.tick = tick;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public double getTotalValue() {
		return quantity * value;
	}
	
	public void changeQuantity(int change) {
		quantity += change;
		if(quantity < 0) {
			quantity = 0;
		}
	}
}
