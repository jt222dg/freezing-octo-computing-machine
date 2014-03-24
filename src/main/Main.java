package main;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import network.Application;

public class Main {
	// { port } { IP }
	static public void main(String args[]) throws UnknownHostException, IOException, InterruptedException {
		
		
		if (args.length < 1) {
			System.out.println("Wrong number of arguments, expected { port } or { port } { IP }");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		InetAddress ip = args.length == 2 ? InetAddress.getByName(args[1]) : null;
		
		Application app = new Application(port, ip);
		
		Thread thread = new Thread(app); 
		thread.start();
		app.send();
		
		System.exit(0);
	}
}