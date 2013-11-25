package pt.feup.stockportfolio;

import java.util.ArrayList;

import pt.feup.stockportfolio.HttpHelper.HistoricResult;

public class Quote {
	String tick;
	int quantity;
	double value;
	boolean isUpdated = false;
	ArrayList<HistoricResult> history = new ArrayList<HistoricResult>();
	
	public Quote(String tick, int quantity) {
		this.tick = tick;
		this.quantity = quantity;
	}

	public boolean update()
	{
		HttpHelper http = new HttpHelper();
		history = http.getHistoric(tick);
		if (history != null)
			isUpdated = true;
		else
			isUpdated = false;
		
		return isUpdated;
	}
	
	public void changeQuantity(int change) {
		quantity += change;
		if(quantity < 0) {
			quantity = 0;
		}
	}
}
