package com.lge.tv.a2a.client;

/**
 * Represent a TV Information.
 * 
 * @author snopy.lee
 * 
 */
public class A2ATVInfo {
	protected int tvId;
	protected String tvName;
	protected String ipAddress;
	protected int port;
	protected String macAddress;
	protected String uuid;
	protected String sessionId;
	protected boolean isConnected;
	protected String authKey;

	public int getTvId() {
		return tvId;
	}

	/**
	 * Gets a TV's model name.
	 * 
	 * @return the model name.
	 */
	public String getTvName() {
		return tvName;
	}

	/**
	 * Gets a TV's UUID value.
	 * 
	 * @return UUID.
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * Gets a TV's IP address.
	 * 
	 * @return the IPv4 or IPv6 internet address.
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Gets a TV's mac address.
	 * 
	 * @return the network device's mac address.
	 */
	public String getMacAddress() {
		return macAddress;
	}
	
	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		if (tvName != null)
			return tvName.startsWith("tv.") ? tvName.substring(3) : tvName;
		return super.toString();
	}
}
