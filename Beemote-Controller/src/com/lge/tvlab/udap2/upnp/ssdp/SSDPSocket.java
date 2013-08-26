/*
 * http://code.google.com/p/android-dlna
 */
package com.lge.tvlab.udap2.upnp.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import android.util.Log;

public class SSDPSocket {
	SocketAddress mSSDPMulticastGroup;
	MulticastSocket mLocalSocket;
	// NetworkInterface mNetIf;

	InetAddress group;

	public SSDPSocket() throws IOException {
		// InetAddress localInAddress = InetAddress.getLocalHost();
		// System.out.println("Local address: " +
		// localInAddress.getHostAddress());

		mSSDPMulticastGroup = new InetSocketAddress(SSDP.ADDRESS, SSDP.PORT);
		mLocalSocket = new MulticastSocket(new InetSocketAddress(SSDP.PORT));

		// mNetIf = NetworkInterface.getByInetAddress(localInAddress);
		group = InetAddress.getByName(SSDP.ADDRESS);
		mLocalSocket.joinGroup(group);
	}

	public void setTimeOut(int millis) {
		try {
			mLocalSocket.setSoTimeout(millis);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/** Used to send SSDP packet */
	public void send(String data) throws IOException {
		DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(),
				mSSDPMulticastGroup);

		mLocalSocket.send(dp);
	}

	/** Used to receive SSDP packet */
	public DatagramPacket receive() throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		String c = new String(dp.getData());
		mLocalSocket.receive(dp);

		return dp;
	}

	/** Close the socket */
	public void close() {
		if (mLocalSocket != null) {
			try {
				// mLocalSocket.leaveGroup(mSSDPMulticastGroup, mNetIf);
				mLocalSocket.leaveGroup(group);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mLocalSocket.close();
		}
	}
}
