package pt.feup.busticket.tickets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
	String ip = "joaoanes.no-ip.biz";
	//String ip = "10.0.2.2";
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
	
	public HttpResult executeGet(String path, List<NameValuePair> args) {
		try {
			String query = URLEncodedUtils.format(args, "utf-8");
			URI uri = URIUtils.createURI("http", ip, port, path, query, "");
			HttpGet get = new HttpGet(uri);
			
			return execute(get);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new HttpResult(-1);
		}
	}
	
	public HttpResult executePost(String path, List<NameValuePair> args) {
		try {
			URL url = new URL("http", ip, port, path);
			HttpPost post = new HttpPost(url.toURI());
			post.setEntity(new UrlEncodedFormEntity(args));
			
			return execute(post);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new HttpResult(-1);
		}
		
	}
	
	public HttpResult login(String username, String password) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		return executePost("/login", nameValuePairs);
	}
	
	public HttpResult register(String username, String password, String name, String card_type, String card_validity, String card_number) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("card_type", card_type));
		nameValuePairs.add(new BasicNameValuePair("card_validity", card_validity));
		nameValuePairs.add(new BasicNameValuePair("card_number", card_number));
		return executePost("/register", nameValuePairs);
	}
	
	public HttpResult getTickets(String token) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("auth", token));
		return executeGet("/tickets/user", nameValuePairs);
	}
	
	public HttpResult buyTickets(String token, int t1_qt, int t2_qt, int t3_qt, boolean confirm) {
		String path = "/tickets/buy?auth=" + token;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("t_num_1", String.valueOf(t1_qt)));
		params.add(new BasicNameValuePair("t_num_2", String.valueOf(t2_qt)));
		params.add(new BasicNameValuePair("t_num_3", String.valueOf(t3_qt)));
		if(confirm) {
			params.add(new BasicNameValuePair("confirm", "on"));
		}
		return executePost(path, params);
	}
	
	public HttpResult validateTicket(String id, int bus_id ,int user_id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ticket_id", id));
		params.add(new BasicNameValuePair("bus_id", String.valueOf(bus_id)));
		params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
		return executePost("/tickets/validate", params);
	}
	
	public HttpResult getBusTickets(int bus_id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("bus_id", String.valueOf(bus_id)));
		return executeGet("/tickets/bus", params);
	}
}
