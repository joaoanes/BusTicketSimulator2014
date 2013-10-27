package pt.feup.busticket.tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientSocket {
	String ip;
	int port;
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	public ClientSocket(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public void connect() throws IOException {
		InetAddress server_addr = InetAddress.getByName(ip);
		socket = new Socket(server_addr, port);
		out = new PrintWriter(socket.getOutputStream(),true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void send(String data) {
		out.println(data);
	}
	
	public String receive() throws IOException {
		return in.readLine();
	}

	public static String sendAndWait(String ip, int port, String data) {
		ClientSocket socket = new ClientSocket(ip, port);
		String result = null;
		
		try {
			socket.connect();
			socket.send(data);
			result = socket.receive();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
