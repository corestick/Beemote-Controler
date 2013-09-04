package com.lge.tvlab.udap2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.lge.tv.a2a.client.A2AClientDefault;
import com.lge.tvlab.udap2.upnp.ssdp.SSDP;
import com.lge.tvlab.udap2.upnp.ssdp.SSDPSearchMsg;
import com.lge.tvlab.udap2.upnp.ssdp.SSDPSocket;

public class UDAPManager {
	int timeOut;
	SSDPSearcher searchThread = null;
	Discoverable discoverable = null;
	static HttpClient client = A2AClientDefault.getThreadsafeClient();
	
	public UDAPManager() {
	}

	public void setTimeOut(int millis) {
		this.timeOut = millis;
	}

	public void setDiscoverable(Discoverable discoverable) {
		this.discoverable = discoverable;
	}

	public boolean startSearch() {
		if (searchThread != null)
			stopSearch();

		searchThread = new SSDPSearcher();
		try {
			searchThread.sock = new SSDPSocket();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		searchThread.start();

		return true;
	}

	public void stopSearch() {
		if (searchThread != null) {
			searchThread.cancle();
			try {
				searchThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			searchThread = null;
		}
	}

	private class SSDPSearcher extends Thread {
		boolean isGo = true;
		SSDPSocket sock = null;

		@Override
		public void run() {
			super.run();

			try {
				SSDPSearchMsg searchMsg = new SSDPSearchMsg(SSDP.ST_ServiceAppToApp);
				sock.send(searchMsg.toString());

				sock.setTimeOut(500);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			long startTime = System.currentTimeMillis();
			if (sock != null) {
				while (isGo) {
					if (System.currentTimeMillis() - startTime > timeOut) {
						if (discoverable != null) {
							discoverable.OnDone(true);
						}

						sock.close();
						return;
					}
					try {
						DatagramPacket packet = sock.receive();

						if (discoverable != null) {
							//System.out.println(SSDP.parseStartLine(packet));
							if (SSDP.parseStartLine(packet).equals("HTTP/1.1 200 OK")) {
								discoverable.OnSSDPPacket(packet);
							}
						}
					} catch (SocketTimeoutException e1) {
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}

				discoverable.OnDone(false);

				sock.close();
			}
		}

		public void cancle() {
			isGo = false;
			sock.close();
		}
	}

	public interface Discoverable {
		public void OnSSDPPacket(DatagramPacket packet);

		public void OnDone(boolean isTimeout);
	}

	public static int requestPairing(String ip, int port, String passCode, int recvPort) {
		
		try {
			URI uri = new URI("http", null, ip, port, "/udap/api/pairing", null, null);
			HttpPost post = new HttpPost(uri);
			post.setHeader("Pragma", "no-cache");
			post.setHeader("Cache-Control", "no-cache");
			post.setHeader("User-Agent", "UDAP/2.0");
			post.setHeader("Connection", "Close");
			StringEntity entity = new StringEntity(
					"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"pairing\"><name>hello</name><value>"
							+ passCode + "</value><port>" + recvPort + "</port></api></envelope>", HTTP.UTF_8);
			entity.setContentType("text/xml; charset=UTF-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			return response.getStatusLine().getStatusCode();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static int requestPairingStart(String ip, int port, boolean isShowPasscode) {
		try {
			URI uri = new URI("http", null, ip, port, "/udap/api/pairing", null, null);
			HttpPost post = new HttpPost(uri);
			post.setHeader("Pragma", "no-cache");
			post.setHeader("Cache-Control", "no-cache");
			post.setHeader("User-Agent", "UDAP/2.0");
			post.setHeader("Connection", "Close");
			StringEntity entity = new StringEntity(
					"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"pairing\"><name>"
							+ (isShowPasscode ? "showKey" : "hideKey") + "</name></api></envelope>", HTTP.UTF_8);
			entity.setContentType("text/xml; charset=UTF-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			return response.getStatusLine().getStatusCode();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static int requestPairingByebye(String ip, int port, int recvPort) {
		try {
			URI uri = new URI("http", null, ip, port, "/udap/api/pairing", null, null);
			HttpPost post = new HttpPost(uri);
			post.setHeader("Pragma", "no-cache");
			post.setHeader("Cache-Control", "no-cache");
			post.setHeader("User-Agent", "UDAP/2.0");
			post.setHeader("Connection", "Close");
			StringEntity entity = new StringEntity(
					"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"pairing\"><name>byebye</name><port>"
							+ recvPort + "</port></api></envelope>", HTTP.UTF_8);
			entity.setContentType("text/xml; charset=UTF-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			return response.getStatusLine().getStatusCode();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}
}
