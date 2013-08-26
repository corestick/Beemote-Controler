package com.lge.tv.a2a.client;

import java.util.List;

/**
 * Inteface definition for a callback to be invoked when TV send a event or
 * internal event occured.
 * 
 * @author snopy.lee
 * 
 */
public interface A2AEventListener {
	/**
	 * Called when TV send a message.
	 * 
	 * @param message
	 *            The msg from TV
	 */
	public void onRecieveEvent(String message);
	
	/**
	 * Called when TV search engine find some TVs.
	 * 
	 * @param message
	 *            The msg from TV
	 */
	public void onSearchEvent(List<A2ATVInfo> infos, boolean isEnd);
}
