package pt.feup.stockportfolio;

public class QuoteUpdate extends Quote {

	public QuoteUpdate(String tick, int quantity) {
		super(tick, quantity);
		this.isUpdated = true;
	}

}
