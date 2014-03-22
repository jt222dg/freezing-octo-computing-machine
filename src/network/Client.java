package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client {
	public Client(int port, String group) {
		this.port          = port;
		this.group         = group;
		this.isTestStarter = false;
		this.generateKBsOfData(30);
		this.numOtherClients = 0;
		this.numbClientsAnswered = 0;
		
		this.startTime = 0;
		this.endTime = 0;
	}
	
	public void joinGroup() throws UnknownHostException, IOException {
		try {
			this.socket = new MulticastSocket(port);
			this.socket.joinGroup(InetAddress.getByName(this.group));
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
	
	public void leaveGroup() throws UnknownHostException, IOException {
		if (this.socket != null) {
			try {
				this.socket.leaveGroup(InetAddress.getByName(this.group));
				this.socket.close();
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
	}
	
	public void send(String string) throws IOException, UnknownHostException {
		try {
			if (string.equalsIgnoreCase("run test")) {
				this.isTestStarter = true;
				this.startTime = System.currentTimeMillis();
			}
			
			byte[] buffer = string.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.group), this.port);
			this.socket.send(packet);
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
	
	public void receive() throws IOException {
		byte[] buffer = new byte[30000];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try {
			this.socket.receive(packet);
			String receivedData = new String(buffer, 0, packet.getLength());
			System.out.println(receivedData);
			
			if (receivedData.equalsIgnoreCase("add client")) {
				this.numOtherClients++;
			} else if (!receivedData.equalsIgnoreCase("run test")) {
				if (this.isTestStarter) {
					this.numbClientsAnswered++;
				}
			} else if (receivedData.equalsIgnoreCase("run test")) {
				//System.out.println("running test..");
				this.send(this.data);
			}
			
			if (this.numbClientsAnswered == this.numOtherClients) {
				this.endTime = System.currentTimeMillis();
				//System.out.println("all clients answered");
				//System.out.println("Time taken: " + (this.endTime - this.startTime) + " milliseconds.");
				
				this.numbClientsAnswered = 0;
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} 
	}
	
	private void generateKBsOfData(int kb)
	{
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder builder = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < kb * 10; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    builder.append(c);
		}

		this.data = builder.toString();
	}

	
	private int port;
	private String group;
	private String data;
	private boolean isTestStarter;
	private int numbClientsAnswered;
	public int numOtherClients;
	private long startTime;
	private long endTime;
	
	MulticastSocket socket;
}
