package pt.feup.stockportfolio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


public class HttpHelper {
	HttpClient client;
	String ip = "10.0.2.2";
	int port = 8080;
	
	public class HttpResult {
		public int code;
		public String result;
		
		public HttpResult(int code) {
			this.code = code;
		}
		
		public HttpResult(int code, String result) {
			this(code);
			this.result = result;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getResult() {
			return result;
		}
		
		public String toString() {
			return "Code: " + code + "\nresult:" + result;
		} 
	}
	
	public class QuoteResult {
		public String tick;
		public double value;
		public String date;
		public String time;
		public int exchanged_shares;
		
		public QuoteResult(String tick, double value, String date, String time,
				int exchanged_shares) {
			this.tick = tick;
			this.value = value;
			this.date = date;
			this.time = time;
			this.exchanged_shares = exchanged_shares;
		}
		
		public QuoteResult(String csv) {
			String[] contents = csv.split(",");
			
			this.tick = contents[0].replace("\"", "");
			this.value = Double.parseDouble(contents[1]);
			this.date = contents[2].replace("\"", "");
			this.time = contents[3].replace("\"", "");
			this.exchanged_shares = Integer.parseInt(contents[4]);
		}
		
		public String getTick() {
			return tick;
		}
		public void setTick(String tick) {
			this.tick = tick;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public int getExchanged_shares() {
			return exchanged_shares;
		}
		public void setExchanged_shares(int exchanged_shares) {
			this.exchanged_shares = exchanged_shares;
		}
		
		@Override
		public String toString() {
			return "Quote: " + tick + " " + "Value: " + value;
		}
	}
	
	public class HistoricResult {
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
		
	}
	
	public HttpHelper() {
		client = new DefaultHttpClient();
	}
	
	public HttpHelper(String ip) {
		this();
		this.ip = ip;
	}
	
	public HttpHelper(String ip, int port) {
		this(ip);
		this.port = port;
	}
	
	public HttpResult execute(HttpUriRequest request) throws IOException {
		HttpResponse response = client.execute(request);
		StatusLine statusLine = response.getStatusLine();
		int status_code = statusLine.getStatusCode();
		
		switch(status_code) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_UNAUTHORIZED:
			case HttpStatus.SC_FORBIDDEN:
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				
				return new HttpResult(status_code, responseString);
			default:
				return new HttpResult(status_code);
		
		}
	}
	
	
	public HttpResult executeGet(String domain, int port, String path, List<NameValuePair> args) {
		try {
			String query = URLEncodedUtils.format(args, "utf-8");
			URI uri = URIUtils.createURI("http", domain, port, path, query, "");
			HttpGet get = new HttpGet(uri);
			
			return execute(get);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new HttpResult(-1);
		}
	}
	
	public HttpResult executeGet(String domain, String path, List<NameValuePair> args) {
		return executeGet(domain, 80, path, args);
	}
	
	public HttpResult executeGet(String path, List<NameValuePair> args) {
		return executeGet(ip, port, path, args);
	}
	
	public HttpResult executePost(String domain, int port, String path,List<NameValuePair> args) {
		try {
			URL url = new URL("http", domain, port, path);
			HttpPost post = new HttpPost(url.toURI());
			post.setEntity(new UrlEncodedFormEntity(args));
			
			return execute(post);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new HttpResult(-1);
		}
		
	}
	
	public HttpResult executePost(String path, List<NameValuePair> args) {
		return executePost(ip, port, path, args);
	}
	
	public HttpResult executePost(String domain, String path, List<NameValuePair> args) {
		return executePost(domain, 80, path, args);
	}
	
	public HttpResult login(String username, String password) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		return executePost("/login", nameValuePairs);
	}
	
	public ArrayList<QuoteResult> getTickValues(ArrayList<String> ticks) {
		String tick_name_list = "" + ticks.get(0);
		for(int i = 1; i < ticks.size(); ++i) {
			tick_name_list += "," + ticks.get(i);
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("f", "sl1d1t1v"));
		params.add(new BasicNameValuePair("s", tick_name_list));
		HttpResult result = executeGet("finance.yahoo.com", "/d/quotes", params);
		
		String[] quotes_csv = result.getResult().split("[\\r\\n]+");
		ArrayList<QuoteResult> quotes = new ArrayList<QuoteResult>();
		for(String quote_csv : quotes_csv) {
			quotes.add(new QuoteResult(quote_csv));
		}
		
		return quotes;
	}
	
	public ArrayList<HistoricResult> getHistoric(String tick, int a, int b, int c, int d, int e, int f) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(8);
		params.add(new BasicNameValuePair("a", "" + a));
		params.add(new BasicNameValuePair("b", "" + b));
		params.add(new BasicNameValuePair("c", "" + c));
		params.add(new BasicNameValuePair("d", "" + d));
		params.add(new BasicNameValuePair("e", "" + e));
		params.add(new BasicNameValuePair("f", "" + f));
		params.add(new BasicNameValuePair("g", "d"));
		params.add(new BasicNameValuePair("s", tick));
		
		HttpResult result = executeGet("ichart.finance.yahoo.com", "/table.txt", params);
		
		String[] historic_csv = result.getResult().split("[\\r\\n]+");
		ArrayList<HistoricResult> historic = new ArrayList<HttpHelper.HistoricResult>();
		//first element is nothing
		for(int i = 1; i < historic_csv.length; ++i) {
			historic.add(new HistoricResult(historic_csv[i]));
		}
		
		return historic;
	}
	
	// return for the last 30 days
	public ArrayList<HistoricResult> getHistoric(String tick) {
		Calendar today_cal = Calendar.getInstance();
		Calendar before_cal = Calendar.getInstance();
		before_cal.add(Calendar.DATE, -30);
		
		int a = before_cal.get(Calendar.MONTH);
		int b = before_cal.get(Calendar.DAY_OF_MONTH);
		int c = before_cal.get(Calendar.YEAR);
		int d = today_cal.get(Calendar.MONTH);
		int e = today_cal.get(Calendar.DAY_OF_MONTH);
		int f = today_cal.get(Calendar.YEAR);

		return getHistoric(tick,a, b, c, d, e, f);
	}
	
}