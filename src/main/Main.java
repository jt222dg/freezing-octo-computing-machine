package main;
import java.io.IOException;
import java.net.UnknownHostException;

import network.Application;

public class Main {
	static public void main(String args[]) throws UnknownHostException, IOException, InterruptedException {
		
		Application app = new Application(5000, "225.4.5.6");
		
		Thread thread = new Thread(app); 
		thread.start();
		app.send();
	}
}
