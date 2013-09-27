package com.lge.tv.a2a.client;

import com.latebutlucky.beemote_controller.KeyboardInfo;

/**
 * Inteface definition for a callback to be invoked when TV send a message.
 * 
 * @author snopy.lee
 * 
 */
public interface A2AMessageListener {
	/**
	 * Called when TV send a message.
	 * 
	 * @param type
	 *            The message type from TV
	 * @param message
	 *            The message string from TV
	 */
	public void onRecieveMessage(String nameType, Object object);
}
