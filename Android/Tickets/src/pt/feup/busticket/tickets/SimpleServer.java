package pt.feup.busticket.tickets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
	int port = 6000;
	ServerSocket server;
	Socket client;
	BufferedReader in;
	PrintWriter out;
	SimpleServerListener listener;
	
	public interface SimpleServerListener {
		public String processInput(String input);
		public void onServerClose(boolean forced);
	}
	
	public SimpleServer(SimpleServerListener listener) {
		this.listener = listener;
	}
	
	public SimpleServer(SimpleServerListener listener, int port) {
		this.listener = listener;
		this.port = port;
	}
	
	public void processRequest() {
		try {
			server = new ServerSocket(port);
			client = server.accept();
			
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String input = in.readLine();
			String output = listener.processInput(input);
			PrintWriter out = new PrintWriter(client.getOutputStream(),true);
			out.println(output);
		}
		catch(Exception e) {
			e.printStackTrace();
			closeConnection(true);
		}
		
		closeConnection();
	}
	
	public boolean isOpen() {
		return server != null;
	}
	
	
	public void closeConnection() {
		closeConnection(false);
	}
	
	public void closeConnection(boolean forced) {
		if(isOpen()) {
			try{
				if(client != null) {
					client.close();
					client = null;
				}
				
				server.close();
				server = null;
				listener.onServerClose(forced);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
