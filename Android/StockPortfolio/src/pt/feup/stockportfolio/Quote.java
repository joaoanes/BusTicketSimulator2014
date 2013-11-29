package pt.feup.stockportfolio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import pt.feup.stockportfolio.HttpHelper.QuoteResult;
import android.graphics.Color;
import android.util.Log;

public class Quote implements Serializable {
	String tick;
	int quantity;
	double value;
	boolean isUpdated = false;
	int color = Color.parseColor("#D6DF23");
	ArrayList<HistoricResult> history = new ArrayList<HistoricResult>();
	String name;
	
	public Quote(String tick, int quantity) {
		this.tick = tick;
		this.quantity = quantity;

		color = ColorFactory.getColor();
	}
	
	public HistoricResult getLast(){
		if (!isUpdated)
			return null;
		return this.history.get(history.size()-1);
	}
	
	public HistoricResult getFromLast(int offset){
		return this.history.get(history.size()-1-offset);
	}
	
	 //two is after one
	public static double getPercentBetween(HistoricResult one, HistoricResult two)
	{
		double delta = two.close - one.close;
		BigDecimal bd = new BigDecimal(delta/one.close * 100);
	    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	public double getPercentage() {
		double delta = value - getLast().close;
		if (delta == 0.0)
			delta = value - getFromLast(1).close;
		BigDecimal bd = new BigDecimal(delta/value * 100);
	    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return delta;
	}
	
	public Quote(Quote other)
	{
		tick = other.tick;
		quantity = other.quantity;
		value = other.value;
		isUpdated = other.isUpdated;
		color = other.color;
		history = other.history;
		name = other.name;
	}

	/***************************************************************************************
	 * PAY ATTENTION
	 * @throws NoInternetException 
	 ***************************************************************************************/
	//THIS SHOULD NEVER
	//EVER
	//EVER	
	//BE RUN ON THE MAIN THREAD OTHERWISE THE WHOLE CODEBASE WILL EXPLODE
	//ASYNCTASK THAT S#IT
	public boolean update() throws NoInternetException
	{
		HttpHelper http = new HttpHelper();
		name = http.getName(this.tick);
		history = http.getHistoric(tick);
		QuoteResult quote_result = http.getTickValue(tick);
		if(value != quote_result.getValue()) {
			Log.i("VALUE DIFFERENCE", String.valueOf(value - quote_result.getValue()));
			value = quote_result.getValue();
		}
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
