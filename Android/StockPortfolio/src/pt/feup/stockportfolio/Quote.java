package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.graphics.Color;

import pt.feup.stockportfolio.HttpHelper.HistoricResult;

public class Quote {
	String tick;
	int quantity;
	double value;
	boolean isUpdated = false;
	int color = Color.parseColor("#D6DF23");
	ArrayList<HistoricResult> history = new ArrayList<HistoricResult>();
	
	public Quote(String tick, int quantity) {
		this.tick = tick;
		this.quantity = quantity;

		color = ColorFactory.getColor();
	}
	
	public Quote(Quote other)
	{
		tick = other.tick;
		quantity = other.quantity;
		value = other.value;
		isUpdated = other.isUpdated;
		color = other.color;
		history = other.history;
	}

	/***************************************************************************************
	 * PAY ATTENTION
	 ***************************************************************************************/
	//THIS SHOULD NEVER
	//EVER
	//EVER	
	//BE RUN ON THE MAIN THREAD OTHERWISE THE WHOLE CODEBASE WILL EXPLODE
	//ASYNCTASK THAT S#IT
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
