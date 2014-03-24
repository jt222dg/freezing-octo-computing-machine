package network;

import java.net.InetAddress;

public class ConnectionInfo
{
	private InetAddress _address;
	private int _port;

	public ConnectionInfo(int port, InetAddress address)
	{
		this._port 	  = port;
		this._address = address;
	}

	public InetAddress getAddress()
	{
		return this._address;
	}

	public int getPort()
	{
		return this._port;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		ConnectionInfo other = (ConnectionInfo) obj;
		if (_address == null || other._address == null)
		{
			return false;
		}
		
		if (_port != other._port)
		{
			return false;
		}
		
		/*if (!_address.equals(other._address))
		{
			return false;
		}*/
		
		return true;
	}
	
	
}
