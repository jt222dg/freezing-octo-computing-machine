package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Application implements Runnable
{
	private Client 		_client;
	private int		 	_port;
	private InetAddress _ipAddress;
	
	public Application(int port, InetAddress ipAddress)
	{
		this._client = new Client();
		
		this._port 		= port;
		this._ipAddress = ipAddress;
	}
	
	@Override
	public void run()
	{
		try
		{
			this._client.connect(this._port, this._ipAddress);
			
			while (true)
			{
				this._client.receive();
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
		finally
		{
			try
			{
				this._client.close(); 
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void send() throws UnknownHostException, IOException
	{
		try
		{
			while (true)
			{
				InputStreamReader sr = new InputStreamReader(System.in);
				BufferedReader br 	 = new BufferedReader(sr);
				String input 		 = br.readLine();
				this._client.send(input);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}