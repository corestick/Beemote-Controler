package com.lge.tv.a2a.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.latebutlucky.beemote_controller.App_Errstate;
import com.latebutlucky.beemote_controller.KeyboardInfo;
import com.latebutlucky.beemote_controller.TvAppInfo;
import com.latebutlucky.beemote_controller.TvChannelListInfo;
import com.lge.tvlab.udap2.UDAPManager;
import com.lge.tvlab.udap2.http.SimpleHttpServer;
import com.lge.tvlab.udap2.upnp.ssdp.SSDP;

/**
 * 
 * @author snopy.lee
 * 
 */
public class A2AClientDefault extends A2AClient {
	List<A2ATVInfo> infos = new ArrayList<A2ATVInfo>();
	List<A2ATVInfo> tmpList = null;

	Handler handler = null;
	SimpleHttpServer httpServer = null;
	Context context = null;

	public HttpClient httpclient = null;

	public A2AClientDefault() {
		super();

		handler = new Handler();
	}

	public static DefaultHttpClient getThreadsafeClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
				mgr.getSchemeRegistry()), params);

		return client;
	}

	public boolean searchTV(Context context) {
		infos.clear();

		this.context = context;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock = wifiManager
				.createMulticastLock("A2AClientLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();

		UDAPManager manager = new UDAPManager();
		manager.setTimeOut(5000);
		manager.setDiscoverable(new UDAPManager.Discoverable() {

			@Override
			public void OnSSDPPacket(DatagramPacket packet) {
				// System.out.println("LOCATION: " +
				// SSDP.parseHeaderValue(packet, "LOCATION"));
				// System.out.println("ST: " + SSDP.parseHeaderValue(packet,
				// "ST"));
				// System.out.println("USN: " + SSDP.parseHeaderValue(packet,
				// "USN"));

				URI location = URI.create(SSDP.parseHeaderValue(packet,
						"LOCATION"));
				String st = SSDP.parseHeaderValue(packet, "ST");

				if (st.equals("urn:schemas-udap:service:AppToApp:1")) {
					HttpClient client = getThreadsafeClient();
					HttpGet httpGet = new HttpGet(location);
					httpGet.setHeader("User-Agent", "UDAP/2.0");
					try {
						HttpResponse response = client.execute(httpGet);

						HttpEntity entity = response.getEntity();
						A2ATVInfo tvinfo = new A2ATVInfo();

						try {
							boolean inEnvelope = false;
							boolean inDevice = false;
							boolean inModelName = false;
							boolean inUuid = false;
							boolean inPort = false;

							XmlPullParser parser = XmlPullParserFactory
									.newInstance().newPullParser();
							parser.setInput(new StringReader(EntityUtils
									.toString(entity)));
							int eventType = parser.getEventType();
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {
								case XmlPullParser.START_TAG:
								case XmlPullParser.END_TAG:
									boolean isStart = eventType == XmlPullParser.START_TAG ? true
											: false;

									if (parser.getName().equals("envelope")) {
										inEnvelope = isStart;
									}
									if (parser.getName().equals("device")) {
										inDevice = isStart;
									}
									if (parser.getName().equals("modelName")) {
										inModelName = isStart;
									}
									if (parser.getName().equals("uuid")) {
										inUuid = isStart;
									}
									if (parser.getName().equals("port")) {
										inPort = isStart;
									}
									break;
								case XmlPullParser.TEXT:
									if (inEnvelope) {
										if (inDevice) {
											if (inModelName) {
												tvinfo.tvName = parser
														.getText();
											}
											if (inUuid) {
												tvinfo.uuid = parser.getText();
											}
										}
										if (inPort) {
											tvinfo.port = Integer
													.parseInt(parser.getText());
										}
									}
									break;
								default:
									break;
								}
								eventType = parser.next();
							}

							tvinfo.ipAddress = location.getHost();
							tmpList = new ArrayList<A2ATVInfo>();
							tmpList.add(tvinfo);
							infos.add(tvinfo);
							if (eventListener != null) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										eventListener.onSearchEvent(tmpList,
												false);
									}
								});
							}

						} catch (XmlPullParserException e) {
							e.printStackTrace();
						}

					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void OnDone(boolean isTimeout) {
				if (eventListener != null) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							eventListener.onSearchEvent(infos, true);
						}
					});
				}
			}
		});
		infos.clear();

		return manager.startSearch();
	}

	synchronized public A2ACmdError showPasscode() throws IOException {
		if (a2atvInfo != null) {
			int ret = UDAPManager.requestPairingStart(a2atvInfo.ipAddress,
					a2atvInfo.port, true);
			return parseError(ret);
		}
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public A2ACmdError hidePasscode() throws IOException {
		if (a2atvInfo != null) {
			int ret = UDAPManager.requestPairingStart(a2atvInfo.ipAddress,
					a2atvInfo.port, false);
			return parseError(ret);
		}

		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public A2ACmdError connect(String passcode) throws IOException {

		if (a2atvInfo != null) {
			if (httpServer != null) {
				httpServer.stopServer();
			}

			HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
			registry.register("/udap/api/event", new HttpRequestHandler() {
				boolean inEnvelope = false;
				boolean inName = false;
				boolean inValue = false;
				boolean inMode = false;
				boolean inState = false;
				boolean inAction = false;
				boolean inDetail = false;
				String nameType = null;

				KeyboardInfo keyboardInfo;
				App_Errstate app_Errstate;

				@Override
				public void handle(HttpRequest request, HttpResponse response,
						HttpContext context) throws HttpException, IOException {
					String method = request.getRequestLine().getMethod()
							.toUpperCase(Locale.ENGLISH);
					System.out.println(request);
					if (!method.equals("GET") && !method.equals("POST")) {
						throw new MethodNotSupportedException(method
								+ " method not supported");
					}

					if (request instanceof HttpEntityEnclosingRequest) {
						HttpEntity entity = ((HttpEntityEnclosingRequest) request)
								.getEntity();
						if (entity != null) {
							try {
								XmlPullParser parser = XmlPullParserFactory
										.newInstance().newPullParser();
								parser.setInput(new StringReader(EntityUtils
										.toString(entity)));
								int eventType = parser.getEventType();
								while (eventType != XmlPullParser.END_DOCUMENT) {
									switch (eventType) {
									case XmlPullParser.START_TAG:
									case XmlPullParser.END_TAG:
										boolean isStart = eventType == XmlPullParser.START_TAG ? true
												: false;

										if (parser.getName().equals("envelope")) {
											inEnvelope = isStart;
										}
										if (parser.getName().equals("name")) {
											inName = isStart;
										}
										if (parser.getName().equals("value")) {
											inValue = isStart;
										}
										if (parser.getName().equals("mode")) {
											inMode = isStart;
										}
										if (parser.getName().equals("state")) {
											inState = isStart;
										}
										if (parser.getName().equals("action")) {
											inAction = isStart;
										}
										if (parser.getName().equals("detail")) {
											inDetail = isStart;
										}
										break;
									case XmlPullParser.TEXT:
										if (inEnvelope) {
											if (inName) {
												if (parser.getText().equals(
														"KeyboardVisible")) {
													keyboardInfo = new KeyboardInfo();
													keyboardInfo.name = parser
															.getText();
													nameType = "KeyboardVisible";
												} else if (parser
														.getText()
														.equals("Mobilehome_App_Errstate")) {
													app_Errstate = new App_Errstate();
													nameType = "AppErrstate";
													Log.e("nameType", nameType);
												}
											}
											if (inValue) {
												keyboardInfo.value = parser
														.getText();
											}
											if (inMode) {
												keyboardInfo.mode = parser
														.getText();
											}
											if (inState) {
												keyboardInfo.state = parser
														.getText();
											}
											if (inState) {
												keyboardInfo.state = parser
														.getText();
											}
											if (inAction) {
												app_Errstate.action = parser
														.getText();
											}
											if (inDetail) {
												app_Errstate.detail = parser
														.getText();
											}
										}
										break;

									default:
										break;

									}
									eventType = parser.next();
								}
							} catch (XmlPullParserException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// if (contents.indexOf(MAGIC_STR) > 0) {
							// try {
							// msgType = Integer.parseInt(contents.substring(
							// 0,
							// contents.indexOf(MAGIC_STR)));
							// contents = contents
							// .substring(contents
							// .indexOf(MAGIC_STR)
							// + MAGIC_STR
							// .length());
							// } catch (Exception e) {
							// }
							// }
							//
							if (messageListener != null) {
								if (nameType.equals("KeyboardVisible")) {
									messageListener.onRecieveMessage(
											"KeyboardVisible", keyboardInfo);
								} else if (nameType.equals("AppErrstate")) {
									messageListener.onRecieveMessage(
											"AppErrstate", app_Errstate);
								}
							}
							response.setStatusCode(HttpStatus.SC_OK);
						}
					}
				}
			});

			httpServer = new SimpleHttpServer(registry);
			httpServer.startServer();

			int ret = UDAPManager.requestPairing(a2atvInfo.ipAddress,
					a2atvInfo.port, passcode, httpServer.getServerPort());
			A2ACmdError error = parseError(ret);

			if (error == A2ACmdError.A2ACmdErrorOK) {
				a2atvInfo.isConnected = true;
				httpclient = getThreadsafeClient();
			}
			return parseError(ret);
		}

		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public A2ACmdError disconnect() throws IOException {
		if (a2atvInfo != null) {
			int ret = UDAPManager.requestPairingByebye(a2atvInfo.ipAddress,
					a2atvInfo.port,
					httpServer != null ? httpServer.getServerPort() : 0);
			a2atvInfo.isConnected = false;
			httpclient = null;
			if (httpServer != null)
				httpServer.stopServer();
			return parseError(ret);
		}
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public QueryResultAppID queryAppId(String appName)
			throws IOException {
		QueryResultAppID queryResultAppID = new QueryResultAppID();
		queryResultAppID.appId = 0l;

		if (a2atvInfo != null) {
			if (a2atvInfo.isConnected == false) {
				queryResultAppID.error = A2ACmdError.A2ACmdErrorUnauthorized;
				return queryResultAppID;
			}
			int statusCode = 0;

			try {
				URI uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/apptoapp/data/"
						+ URLEncoder.encode(appName));
				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("User-Agent", "UDAP/2.0");
				httpGet.setHeader("Connection", "Close");

				HttpResponse response = httpclient.execute(httpGet);
				HttpEntity entity = response.getEntity();

				statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					queryResultAppID.appId = Long.parseLong(EntityUtils
							.toString(entity));
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			queryResultAppID.error = parseError(statusCode);
			return queryResultAppID;
		}
		queryResultAppID.error = A2ACmdError.A2ACmdErrorNoCurrentTV;
		return queryResultAppID;
	}

	synchronized public QueryResultStatus queryAppStatus(long appId)
			throws IOException {
		QueryResultStatus queryResultAppID = new QueryResultStatus();
		queryResultAppID.status = A2AStatus.A2AStatusUnknown;

		if (a2atvInfo != null) {
			if (a2atvInfo.isConnected == false) {
				queryResultAppID.error = A2ACmdError.A2ACmdErrorUnauthorized;
				return queryResultAppID;
			}
			int statusCode = 0;

			try {
				URI uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/apptoapp/data/" + appId
						+ "/status");
				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("User-Agent", "UDAP/2.0");

				HttpResponse response = httpclient.execute(httpGet);

				HttpEntity entity = response.getEntity();

				statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					String status = EntityUtils.toString(entity);
					queryResultAppID.status = parseStatus(status);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			queryResultAppID.error = parseError(statusCode);
			return queryResultAppID;
		}
		queryResultAppID.error = A2ACmdError.A2ACmdErrorNoCurrentTV;
		return queryResultAppID;
	}

	synchronized public A2ACmdError executeApp(long appId) throws IOException {
		if (a2atvInfo != null) {
			if (a2atvInfo.isConnected == false)
				return A2ACmdError.A2ACmdErrorUnauthorized;
			int statusCode = 0;

			try {
				URI uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/apptoapp/command/"
						+ appId + "/run");

				HttpPost httpPost = new HttpPost(uri);
				httpPost.setHeader("User-Agent", "UDAP/2.0");
				httpPost.setHeader("Connection", "Close");

				HttpResponse response = httpclient.execute(httpPost);
				HttpEntity entity = response.getEntity();

				Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return parseError(statusCode);
		}
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public A2ACmdError terminateApp(long appId) throws IOException {
		if (a2atvInfo != null) {
			if (a2atvInfo.isConnected == false)
				return A2ACmdError.A2ACmdErrorUnauthorized;
			int statusCode = 0;

			try {
				URI uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/apptoapp/command/"
						+ appId + "/term");

				HttpPost httpPost = new HttpPost(uri);
				httpPost.setHeader("User-Agent", "UDAP/2.0");
				httpPost.setHeader("Connection", "Close");

				HttpResponse response = httpclient.execute(httpPost);
				HttpEntity entity = response.getEntity();

				Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return parseError(statusCode);
		}
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public A2ACmdError sendMessage(long appId, int messageType,
			String message) throws IOException {
		if (a2atvInfo != null) {
			if (a2atvInfo.isConnected == false)
				return A2ACmdError.A2ACmdErrorUnauthorized;
			int statusCode = 0;

			try {
				URI uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/apptoapp/command/"
						+ appId + "/send");

				HttpPost httpPost = new HttpPost(uri);
				httpPost.setHeader("User-Agent", "UDAP/2.0");
				if (messageType == 0)
					httpPost.setEntity(new StringEntity(message, "UTF-8"));
				else {
					StringEntity entity = new StringEntity(messageType
							+ "~*(a)$^$(a)*~" + message, "UTF-8");
					Log.i("CSnopy", entity.getContentLength() + ","
							+ messageType + "~*(a)$^$(a)*~" + message);
					httpPost.setEntity(entity);
				}

				HttpResponse response = httpclient.execute(httpPost);
				HttpEntity entity = response.getEntity();

				Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return parseError(statusCode);
		}
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public A2ACmdError sendRemoteKey(int keyValue)
			throws IOException {
		if (a2atvInfo != null) {
			if (a2atvInfo.isConnected == false)
				return A2ACmdError.A2ACmdErrorUnauthorized;
			int statusCode = 0;

			try {
				URI uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/roap/api/command");

				HttpPost httpPost = new HttpPost(uri);
				// httpPost.setHeader("User-Agent", "UDAP/2.0");
				httpPost.setHeader("Connection", "Close");
				httpPost.setHeader("Content-Type", "application/atom+xml");
				{
					StringEntity entity = new StringEntity(
							"<?xml version=\"1.0\" encoding=\"utf-8\"?><command><name>HandleKeyInput</name><value>"
									+ keyValue + "</value></command>", "UTF-8");
					Log.i("CSnopy", entity.getContentLength() + "");
					httpPost.setEntity(entity);
				}

				HttpResponse response = httpclient.execute(httpPost);
				HttpEntity entity = response.getEntity();

				Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return parseError(statusCode);
		}
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public Bitmap tvAppIconQuery(String auid, String appName)
			throws IOException {
		URI uri = null;
		int statusCode = 0;
		Bitmap bitmap = null;

		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port
						+ "/udap/api/data?target=appicon_get&auid=" + auid
						+ "&appname=URL_Encode(" + appName + ")");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("User-Agent", "UDAP/2.0");
			httpGet.setHeader("Connection", "Keep-Alive");

			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inStream = null;
				inStream = entity.getContent();
				bitmap = BitmapFactory.decodeStream(inStream);
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
			}
		}
		return bitmap;
	}

	synchronized public void tvAppQuery() throws IOException {
		URI uri = null;
		int statusCode = 0;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port
						+ "/udap/api/data?target=applist_get&type=1"
						+ "&index=0" + "&number=0");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("User-Agent", "UDAP/2.0");
			httpGet.setHeader("Connection", "Close");

			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			boolean inEnvelope = false;
			boolean inData = false;
			boolean inAuid = false;
			boolean inName = false;
			boolean inCpid = false;

			TvAppInfo tvInfo = null;
			
			try {
				XmlPullParser parser = XmlPullParserFactory.newInstance()
						.newPullParser();
				parser.setInput(new StringReader(EntityUtils.toString(entity)));
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
					case XmlPullParser.END_TAG:
						boolean isStart = eventType == XmlPullParser.START_TAG ? true
								: false;

						if (parser.getName().equals("envelope")) {
							inEnvelope = isStart;
						}
						if (parser.getName().equals("data")) {
							inData = isStart;
						}
						if (parser.getName().equals("auid")) {
							inAuid = isStart;
						}
						if (parser.getName().equals("name")) {
							inName = isStart;
						}
						if (parser.getName().equals("cpid")) {
							inCpid = isStart;
						}
						break;
					case XmlPullParser.TEXT:
						if (inEnvelope) {
							if (inData) {
								if (inAuid) {
//									Log.e("TVAPP", tvInfo.auid);
									
									if(tvInfo != null)
										TvAppList.add(tvInfo);
									
									tvInfo = new TvAppInfo();
									tvInfo.auid = parser.getText();
								}
								if (inName) {
//									 Log.e("TVAPPname", tvInfo.name);

									tvInfo.name = parser.getText();
								}
								if (inCpid) {
									tvInfo.cpid = parser.getText();
								}
							}
						}
						break;

					default:
						break;

					}
					eventType = parser.next();
				}
				
				if(tvInfo != null)
					TvAppList.add(tvInfo);

			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i = 0; i< TvAppList.size(); i++)
			{
				Log.e("RRR", "a-->" + TvAppList.get(i).auid);
				Log.e("RRR", "n-->" + TvAppList.get(i).name);
				Log.e("RRR", "c-->" + TvAppList.get(i).cpid);
			}
			
			// statusCode = response.getStatusLine().getStatusCode();
			// if (statusCode == HttpURLConnection.HTTP_OK) {
			// BufferedReader in = new BufferedReader(new InputStreamReader(
			// response.getEntity().getContent()));
			// StringBuffer sb = new StringBuffer();
			// String line = "";
			// while ((line = in.readLine()) != null) {
			// sb.append(line);
			// }
			//
			// System.out.println(sb);
			// }
		}
	}

	synchronized public void tvListQuery() throws IOException {
		URI uri = null;
		int statusCode = 0;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/data?target=channel_list");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("User-Agent", "UDAP/2.0");
			httpGet.setHeader("Connection", "Close");

			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			// statusCode = response.getStatusLine().getStatusCode();
			// if (statusCode == HttpURLConnection.HTTP_OK) {
			// BufferedReader in = new BufferedReader(new InputStreamReader(
			// response.getEntity().getContent()));
			// StringBuffer sb = new StringBuffer();
			// String line = "";
			// while ((line = in.readLine()) != null) {
			// sb.append(line);
			// }
			//
			// System.out.println(sb);
			// }

			boolean inEnvelope = false;
			boolean inData = false;
			boolean inMajor = false;
			boolean inMinor = false;
			boolean inPhysicalNum = false;
			boolean inChname = false;

			TvChannelListInfo tvListInfo = new TvChannelListInfo();

			try {
				XmlPullParser parser = XmlPullParserFactory.newInstance()
						.newPullParser();
				parser.setInput(new StringReader(EntityUtils.toString(entity)));
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
					case XmlPullParser.END_TAG:
						boolean isStart = eventType == XmlPullParser.START_TAG ? true
								: false;

						if (parser.getName().equals("envelope")) {
							inEnvelope = isStart;
						}
						if (parser.getName().equals("data")) {
							inData = isStart;
						}
						if (parser.getName().equals("major")) {
							inMajor = isStart;
						}
						if (parser.getName().equals("minor")) {
							inMinor = isStart;
						}
						if (parser.getName().equals("physicalNum")) {
							inPhysicalNum = isStart;
						}
						if (parser.getName().equals("chname")) {
							inChname = isStart;
						}
						break;
					case XmlPullParser.TEXT:
						if (inEnvelope) {
							if (inData) {
								if (inChname) {
									tvListInfo.chname = parser.getText();
									Log.e("TVChannelname", tvListInfo.chname);
									TvChannelList.add(tvListInfo);
									tvListInfo = new TvChannelListInfo();
									Log.e("TVTvChannelListSize",
											TvChannelList.size() + "");
								}
								if (inPhysicalNum) {
									tvListInfo.PhysicalNum = parser.getText();
									// Log.e("TVAPPcpid",
									// tvListInfo.PhysicalNum);

								}
								if (inMajor) {
									tvListInfo.Major = parser.getText();
									// Log.e("TVAPP", tvListInfo.Major);
								}
								if (inMinor) {
									tvListInfo.Minor = parser.getText();
									// Log.e("TVAPP", tvListInfo.Minor);
								}
							}
						}
						break;

					default:
						break;

					}
					eventType = parser.next();
				}
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	synchronized public void TvAppExe(String auid, String appName,
			String contentId) throws IOException {
		URI uri = null;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/command");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "Close");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"command\"><name>AppExecute</name><auid>"
								+ auid
								+ "</auid><appname>"
								+ appName
								+ "</appname><contentId>"
								+ contentId
								+ "</contentId>" + "</api></envelope>",
						HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);
				httpclient.execute(post);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	synchronized public void TvAppTerminate(String auid, String appName)
			throws IOException {
		URI uri = null;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/command");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "Close");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"command\"><name>AppTerminate</name><auid>"
								+ auid
								+ "</auid><appname>"
								+ appName
								+ "</appname>" + "</api></envelope>",
						HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);
				httpclient.execute(post);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	synchronized public void keywordSend(String str) throws IOException {
		URI uri = null;
		int statusCode = 0;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/event");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "Keep-Alive");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"event\"><name>TextEdited</name><state>"
								+ "Editing"
								+ "</state><value>"
								+ str
								+ "</value>" + "</api></envelope>", HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);
				HttpResponse response = httpclient.execute(post);
				statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					StringBuffer sb = new StringBuffer();
					String line = "";
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}

					System.out.println(sb);
				}
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	synchronized public void KeyCodeSend(String keycode) throws IOException {
		URI uri = null;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.ipAddress + ":"
						+ a2atvInfo.port + "/udap/api/command");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "Close");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"command\"><name>HandleKeyInput</name>"
								+ "<value>"
								+ keycode
								+ "</value>"
								+ "</api></envelope>", HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);
				HttpResponse response = httpclient.execute(post);
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static private A2ACmdError parseError(int hdcpError) {
		A2ACmdError error = A2ACmdError.A2ACmdErrorUnknown;

		switch (hdcpError) {
		case 200:
			error = A2ACmdError.A2ACmdErrorOK;
			break;
		case 400:
			error = A2ACmdError.A2ACmdErrorBadRequest;
			break;
		case 401:
			error = A2ACmdError.A2ACmdErrorUnauthorized;
			break;
		case 404:
			error = A2ACmdError.A2ACmdErrorNotFound;
			break;
		case 406:
			error = A2ACmdError.A2ACmdErrorNotAcceptable;
			break;
		case 408:
			error = A2ACmdError.A2ACmdErrorRequestTimeout;
			break;
		case 409:
			error = A2ACmdError.A2ACmdErrorConflict;
			break;
		case 500:
			error = A2ACmdError.A2ACmdErrorInternalServerError;
			break;
		case 503:
			error = A2ACmdError.A2ACmdErrorServiceUnavailable;
			break;
		default:
			error = A2ACmdError.A2ACmdErrorUnknown;
			break;
		}

		Log.d("CSnopy", error + "");
		return error;
	}

	static private A2AStatus parseStatus(String status) {
		A2AStatus error = A2AStatus.A2AStatusUnknown;

		if (status.equals("NONE"))
			error = A2AStatus.A2AStatusNone;
		else if (status.equals("LOAD"))
			error = A2AStatus.A2AStatusLoad;
		else if (status.equals("RUN"))
			error = A2AStatus.A2AStatusRun;
		else if (status.equals("RUNNF"))
			error = A2AStatus.A2AStatusRunNotFocused;
		else if (status.equals("TERM"))
			error = A2AStatus.A2AStatusTerm;
		else if (status.equals("UNKNOWN"))
			error = A2AStatus.A2AStatusUnknown;

		Log.d("CSnopy", error + "");
		return error;
	}

}
