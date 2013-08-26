package com.lge.tv.a2a.client;


/**
 * The interface that apps to use A2A.
 * @author snopy.lee
 * 
 */
public class A2AClientManager {
	private static A2AClient client = new A2AClientDefault();

	private A2AClientManager() {
		
	}
	/**
	 * Use this method to get the default A2AClient object.
	 * @return default {@link A2AClient} object.
	 */
	public static A2AClient getDefaultClient() {
		return client;
	}
}
