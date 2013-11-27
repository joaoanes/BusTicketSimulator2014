package pt.feup.stockportfolio;

import java.io.Serializable;

public class HistoricResult implements Comparable<HistoricResult>, Serializable {
	public String date;
	public double open;
	public double high;
	public double low;
	public double close;
	public int volume;
	public double adj_close;

	public HistoricResult(String csv) {
		String[] contents = csv.split(",");

		date = contents[0];
		open = Double.parseDouble(contents[1]);
		high = Double.parseDouble(contents[2]);
		low = Double.parseDouble(contents[3]);
		close = Double.parseDouble(contents[4]);
		volume = Integer.parseInt(contents[5]);
		adj_close = Double.parseDouble(contents[6]);
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public double getAdj_close() {
		return adj_close;
	}
	public void setAdj_close(double adj_close) {
		this.adj_close = adj_close;
	}

	@Override
	public String toString() {
		return "Date: " + date + " Open: " + open + " High: " + high + " Low : " + low  + " Close: " + close;
	}

	@Override
	public int compareTo(HistoricResult another) {
		
		return (int) (this.close-another.close);
	}

}