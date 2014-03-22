package network;

import java.io.IOException;
import java.net.UnknownHostException;

public class Application implements Runnable {

	public Application(int port, String group) {
		this.port = port;
		this.group = group;
		this.client = new Client(this.port, this.group);
	}
	
	@Override
	public void run() {
		
		try {
			this.client.joinGroup();
			System.out.println("joined group..");
			this.client.send("add client");
			
			while (true) {
				this.client.receive();
			}
			
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} finally {
			try {
				client.leaveGroup();
				System.out.println("left group..");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void send() throws UnknownHostException, IOException {
		try {
			while (true) {
				String string = System.console().readLine();
				this.client.send(string);
			}
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	private Client client;
	private int port;
	private String group;
}