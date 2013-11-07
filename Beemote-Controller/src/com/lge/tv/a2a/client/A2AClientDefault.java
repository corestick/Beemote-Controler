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
import org.apache.http.HttpVersion;
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
import org.apache.http.params.CoreProtocolPNames;
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
import android.os.AsyncTask;
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
	

	public HttpClient httpclient = getThreadsafeClient();
	public HttpResponse response = null;
	public HttpEntity entity = null;
	public String stringEn = null;
	public String TvListEn = null;
	public Bitmap AppIcon;

	public A2AClientDefault() {
		super();

		handler = new Handler();
	}

	public static DefaultHttpClient getThreadsafeClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
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
						// mGetClientAsyncTask = new ClientGetAsyncTask();
						// mGetClientAsyncTask.execute(httpGet);
						// waitThread waitth = new waitThread();
						// waitth.run();
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
								nameType = null;
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
												Log.e("aaaa", parser.getText());
												if (parser.getText().equals(
														"KeyboardVisible")) {
													keyboardInfo = new KeyboardInfo();
													keyboardInfo.name = parser
															.getText();
													nameType = "KeyboardVisible";
												} else if (parser
														.getText()
														.equals("Mobilehome_App_Errstate")) {													
													nameType = "AppErrstate";
													Log.e("nameType", nameType);
												} else if (parser.getText()
														.equals("3DMode")) {
													nameType = "3DMode";
													Log.e("nameType", nameType);
												} else if (parser
														.getText()
														.equals("ChannelChanged")) {
													nameType = "ChannelChanged";
													Log.e("nameType", nameType);
												} else if (parser.getText()
														.equals("TextEdited")) {
													keyboardInfo = new KeyboardInfo();
													keyboardInfo.name = parser
															.getText();
													nameType = "TextEdited";
													Log.e("nameType", nameType);
												} else if (parser
														.getText()
														.equals("CursorVisible")) {
													nameType = "CursorVisible";
													Log.e("nameType", nameType);
												}
											}
											if (inValue
													&& (nameType
															.equals("KeyboardVisible") || nameType
															.equals("TextEdited"))) {
												keyboardInfo.value = parser
														.getText();
											}
											if (inMode
													&& nameType
															.equals("KeyboardVisible")) {
												keyboardInfo.mode = parser
														.getText();
											}
											if (inState
													&& nameType
															.equals("TextEdited")) {
												keyboardInfo.state = parser
														.getText();
											}
											if (inAction
													&& nameType
															.equals("AppErrstate")) {
												app_Errstate.action = parser
														.getText();
											}
											if (inDetail
													&& nameType
															.equals("AppErrstate")) {
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
		UDAPManager.mHttpAsyncTask.cancel(true);
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

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(httpPost);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(httpPost);
				// HttpEntity entity = response.getEntity();

				// Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			initValue();
			return parseError(statusCode);
		}
		initValue();
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

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(httpPost);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(httpPost);
				// Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			initValue();
			return parseError(statusCode);
		}
		initValue();
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

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(httpPost);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(httpPost);
				// Log.d("CSnopy", EntityUtils.toString(entity));

				statusCode = response.getStatusLine().getStatusCode();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			initValue();
			return parseError(statusCode);
		}
		initValue();
		return A2ACmdError.A2ACmdErrorNoCurrentTV;
	}

	synchronized public Bitmap tvAppIconQuery(String auid, String appName)
			throws IOException {
		URI uri = null;
		int statusCode = 0;

		if (a2atvInfo != null) {
			AppIcon = null;
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

			AppIconAsyncTask mAppAsyncTask = new AppIconAsyncTask();
			mAppAsyncTask.execute(httpGet);

			while (true) {
				if (AppIcon != null)
					break;
			}
			mAppAsyncTask.cancel(true);
			Log.e("RRRR", AppIcon.toString());

			// mAppAsyncTask.cancel(true);

			// HttpResponse response = httpclient.execute(httpGet);
			// HttpEntity entity = response.getEntity();
			// if (entity != null) {
			// InputStream inStream = null;
			// inStream = entity.getContent();
			// bitmap = BitmapFactory.decodeStream(inStream);
			// if (response.getEntity() != null) {
			// response.getEntity().consumeContent();
			// }
			// }
		}
		return AppIcon;
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

			ClientGetAsyncTask mGetClientAsyncTask = new ClientGetAsyncTask();
			mGetClientAsyncTask.execute(httpGet);
			while (true) {
				if (response != null) {
					break;
				}
			}
			while (true) {
				if (stringEn != null) {
					break;
				}
			}
			mGetClientAsyncTask.cancel(true);
			// HttpResponse response = httpclient.execute(httpGet);
			// HttpEntity entity = response.getEntity();
			boolean inEnvelope = false;
			boolean inData = false;
			boolean inAuid = false;
			boolean inName = false;
			boolean inCpid = false;

			TvAppInfo tvInfo = null;

			try {
				XmlPullParser parser = XmlPullParserFactory.newInstance()
						.newPullParser();
				parser.setInput(new StringReader(stringEn));
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
									// Log.e("TVAPP", tvInfo.auid);

									if (tvInfo != null)
										TvAppList.add(tvInfo);

									tvInfo = new TvAppInfo();
									tvInfo.auid = parser.getText();
								}
								if (inName) {
									// Log.e("TVAPPname", tvInfo.name);

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

				if (tvInfo != null)
					TvAppList.add(tvInfo);

			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < TvAppList.size(); i++) {
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
		initValue();
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

			TvListAsyncTask mTvListAsyncTask = new TvListAsyncTask();
			mTvListAsyncTask.execute(httpGet);
			while (true) {
				if (response != null)
					break;
			}
			while (true) {
				if (TvListEn != null)
					break;
			}
			mTvListAsyncTask.cancel(true);
			// HttpResponse response = httpclient.execute(httpGet);
			// HttpEntity entity = response.getEntity();

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
				parser.setInput(new StringReader(TvListEn));
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
									String chname = parser.getText();
									boolean sameName = false;
									for (int i = 0; i < TvChannelList.size(); i++) {
										if (TvChannelList.get(i).chname
												.equals(chname)) {
											sameName = true;
											break;
										}
									}
									if (sameName == false) {
										tvListInfo.chname = chname;
										TvChannelList.add(tvListInfo);
										Log.e("TVChannelname",
												tvListInfo.chname);

									}
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
			initValue();
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

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);				
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
				// httpclient.execute(post);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initValue();
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

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
				// httpclient.execute(post);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initValue();
		}
	}

	synchronized public void keywordSend(String state, String value)
			throws IOException {
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
								+ state
								+ "</state><value>"
								+ value
								+ "</value>" + "</api></envelope>", HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(post);

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
			initValue();
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
				post.setHeader("Connection", "Keep-Alive");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"command\"><name>HandleKeyInput</name>"
								+ "<value>"
								+ keycode
								+ "</value>"
								+ "</api></envelope>", HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(post);
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initValue();
		}
	}

	synchronized public void cursorVisible() throws IOException {
		URI uri = null;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.getIpAddress() + ":"
						+ a2atvInfo.getPort() + "/udap/api/event");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "close");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"event\"><name>CursorVisible</name>"
								+ "<value>"
								+ "true"
								+ "</value>"
								+ "<mode>"
								+ "auto" + "</mode>" + "</api></envelope>",
						HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(post);

				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initValue();
		}
	}

	synchronized public void moveMouse(int x, int y) throws IOException {

		Log.e("RRR", "x=" + x + ", y=" + y);

		URI uri = null;
		int statusCode = 0;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.getIpAddress() + ":"
						+ a2atvInfo.getPort() + "/udap/api/command");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "Keep-Alive");
				
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"command\"><name>HandleTouchMove</name>"
								+ "<x>"
								+ x
								+ "</x>"
								+ "<y>"
								+ y
								+ "</y>"
								+ "</api></envelope>", HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(post);
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initValue();
		}
	}

	synchronized public void HandleTouchClick() throws IOException {
		URI uri = null;
		int statusCode = 0;
		if (a2atvInfo != null) {
			try {
				uri = new URI("http://" + a2atvInfo.getIpAddress() + ":"
						+ a2atvInfo.getPort() + "/udap/api/command");
				HttpPost post = new HttpPost(uri);
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("User-Agent", "UDAP/2.0");
				post.setHeader("Connection", "close");
				StringEntity entity = new StringEntity(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><envelope><api type=\"command\"><name>HandleTouchClick</name>"
								+ "</api></envelope>", HTTP.UTF_8);
				entity.setContentType("text/xml; charset=UTF-8");
				post.setEntity(entity);

				ClientPostAsyncTask mPostClientAsyncTask = new ClientPostAsyncTask();
				mPostClientAsyncTask.execute(post);
				while (true) {
					if (response != null)
						break;
				}
				mPostClientAsyncTask.cancel(true);
				// HttpResponse response = httpclient.execute(post);
				if (response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initValue();
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

	public class ClientPostAsyncTask extends AsyncTask<HttpPost, Void, Void> {

		protected Void doInBackground(HttpPost... post) {
			try {
				response = httpclient.execute(post[0]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onCancelled() {
			super.onCancelled();
		}

	}

	public class ClientGetAsyncTask extends AsyncTask<HttpGet, Void, Void> {

		@Override
		protected Void doInBackground(HttpGet... get) {
			try {
				response = httpclient.execute(get[0]);
				entity = response.getEntity();
				stringEn = EntityUtils.toString(entity);
				Log.e("RRRR", response.toString());
				Log.e("RRRR", entity.toString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	public class TvListAsyncTask extends AsyncTask<HttpGet, Void, Void> {

		@Override
		protected Void doInBackground(HttpGet... get) {
			try {
				response = httpclient.execute(get[0]);
				entity = response.getEntity();
				TvListEn = EntityUtils.toString(entity);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	public class AppIconAsyncTask extends AsyncTask<HttpGet, Void, Void> {

		@Override
		protected Void doInBackground(HttpGet... get) {
			try {
				response = httpclient.execute(get[0]);
				entity = response.getEntity();
				if (entity != null) {
					InputStream inStream = null;
					try {
						inStream = entity.getContent();
						AppIcon = BitmapFactory.decodeStream(inStream);
						if (response.getEntity() != null) {
							response.getEntity().consumeContent();
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

	public void initValue() {
		response = null;
		entity = null;
		stringEn = null;
		TvListEn = null;
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
