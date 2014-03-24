package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Client
{
	private String 		     	 _data;
	private DatagramSocket 		 _socket;
	private List<ConnectionInfo> _otherClientsInfo;
	private ConnectionInfo 		 _info;
	private boolean 			 _isServer = false;
	private int 				 _numbClientsThatHasAnswered = 0;
	private long 			     _startTime = 0;
	private long 				 _stopTime = 0;
	private int 				_numbReceivedDatas = 0;
	private boolean 			_testHasStarted = false;
	
	public Client()
	{
		this._otherClientsInfo = new LinkedList<ConnectionInfo>();
		this._otherClientsInfo = Collections.synchronizedList(this._otherClientsInfo);
	}
	
	public void send(String string) throws IOException, UnknownHostException
	{
		for (ConnectionInfo clientInfo : this._otherClientsInfo)
		{
			this.send(string, clientInfo.getPort(), clientInfo.getAddress());
		}
	}
	
	public void send(String string, int port, InetAddress ipAddress) throws IOException, UnknownHostException
	{
		try
		{
			byte[] buffer = string.getBytes();
			DatagramPacket packet = null;
			
			packet = new DatagramPacket(buffer, buffer.length, ipAddress, port);
			this._socket.send(packet);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		} 
	}
	
	public String receiveString() throws IOException
	{
		byte[] buffer = new byte[30000];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		this._socket.receive(packet);
		return new String(buffer, 0, packet.getLength());
	}
	
	public void receive() throws IOException
	{
		byte[] buffer = new byte[30000];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try
		{
			this._socket.receive(packet);
			String receivedData = new String(buffer, 0, packet.getLength());
			
			switch (receivedData)
			{
				case Message.Connect : // Only server receives this
				{
					// Add to server
					ConnectionInfo info = new ConnectionInfo(packet.getPort(), packet.getAddress());
					if (!this._otherClientsInfo.contains(info))
					{
						this._otherClientsInfo.add(info);
					}
					
					this.send(Message.SetOwnAddress, packet.getPort(), packet.getAddress());
					this.send(packet.getPort() + ":" + packet.getAddress().getHostAddress());
					
					for (ConnectionInfo ci : _otherClientsInfo)
					{
						String newClientInfo = ci.getPort() + ":" + ci.getAddress().getHostAddress();
						this.send(Message.AddClient);
						this.send(newClientInfo);
					}
					
					System.out.println("Client connected.");
					
					break;
				}
				case Message.AddClient : 
				{
					String[] clientInfo = this.receiveString().split(":");
					
					// If client has same IP as server
					if (clientInfo[1].equalsIgnoreCase("127.0.0.1"))
					{
						clientInfo[1] = packet.getAddress().getHostAddress();
					}
						
					int port = Integer.parseInt(clientInfo[0]);
					InetAddress address = InetAddress.getByName(clientInfo[1]);
					
					// Add to client
					ConnectionInfo info = new ConnectionInfo(port, address);
					
					if (!info.equals(this._info))
					{
						if (!this._otherClientsInfo.contains(info))
						{
							this._otherClientsInfo.add(info);
							System.out.println("Added client with port " + info.getPort() + " and ip " + info.getAddress().getHostAddress());
						}
					}
					
					break;
				}
				case Message.SetOwnAddress :
				{
					String[] clientInfo = this.receiveString().split(":");
					
					// If client has same IP as server
					if (clientInfo[1].equalsIgnoreCase(_otherClientsInfo.get(0).getAddress().getHostAddress()))
					{
						clientInfo[1] = packet.getAddress().getHostAddress();
					}
						
					int port = Integer.parseInt(clientInfo[0]);
					InetAddress address = InetAddress.getByName(clientInfo[1]);
					
					// Add to client
					this._info = new ConnectionInfo(port, address);
					
					break;
				}
				case Message.RunTest :
				{
					if (!this._isServer)
					{
						this.generateKBsOfData(30 / this._otherClientsInfo.size());
						this.send(Message.IsReady);
					}
					
					break;
				}
				case Message.IsReady :
				{
					if (this._isServer)
					{
						this._numbClientsThatHasAnswered++;
						if (this._numbClientsThatHasAnswered == this._otherClientsInfo.size())
						{
							this._startTime = System.currentTimeMillis();
							this._numbClientsThatHasAnswered = 0;
							
							System.out.println("Test starting");
							this.send(Message.Start);
						}
					}
					
					break;
				}
				case Message.Start :
				{
					if (!this._isServer)
					{
						this._testHasStarted = true;
						this.send(this._data);
					}
					
					break;
				}
				case Message.Done :
				{
					if (this._isServer)
					{
						this._numbClientsThatHasAnswered++;
					
						if (this._numbClientsThatHasAnswered == this._otherClientsInfo.size())
						{
							this._stopTime = System.currentTimeMillis();
							this._numbClientsThatHasAnswered = 0;
							
							System.out.println("Test done: " + (this._stopTime - this._startTime) + " milliseconds");
						}
					}
					
					break;
				}
				default: // When receiving test data
				{
					if (!this._isServer && this._testHasStarted)
					{
						this._numbReceivedDatas++;
						
						if (_numbReceivedDatas == this._otherClientsInfo.size())
						{
							this._numbReceivedDatas = 0;
							this.send(Message.Done);
						}
					}
					
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	private void generateKBsOfData(int kb)
	{
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder builder = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < kb * 10; i++)
		{
		    char c = chars[random.nextInt(chars.length)];
		    builder.append(c);
		}

		this._data = builder.toString();
	}

	public void connect(int port, InetAddress ipAddress) throws SocketException
	{
		try
		{
			this._socket = null;
			if (ipAddress == null) // "Server"
			{
				this._isServer  = true;
				this._socket = new DatagramSocket(port);
				System.out.println("Connected as server");
			}
			else // Client
			{
				this._socket = new DatagramSocket(null);
				
				// Add server
				this._otherClientsInfo.add(new ConnectionInfo(port, ipAddress));
				this.send(Message.Connect);
						
				System.out.println("Connected as client");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void close() throws SocketException
	{
		try
		{
			this._socket.close();
			System.out.println("closed connection.");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
