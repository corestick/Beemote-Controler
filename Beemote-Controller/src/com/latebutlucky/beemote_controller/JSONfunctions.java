package com.latebutlucky.beemote_controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class JSONfunctions {
	static HttpClient httpclient;

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

	public static String getJSONfromURL(String url, String PhoneNum) {
		InputStream is = null;
		String result = "";
		try {
			httpclient = getThreadsafeClient();
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("phone", PhoneNum);
			HttpResponse response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log.e("kim", "1" + e);
		}

		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb2 = new StringBuffer();
			String line2 = "";
			while ((line2 = in.readLine()) != null) {
				sb2.append(line2 + "\n");
				Log.e("RRRRRRR", line2);
			}

			// BufferedReader reader = null;
			// reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),
			// 8);
			//
			// StringBuilder sb = new StringBuilder();
			// String line = null;
			// while ((line = reader.readLine()) != null) {
			// sb.append(line + "\n");
			// }
			// Log.e("ddd", line);
			is.close();
			result = sb2.toString();
		} catch (Exception e) {
			Log.e("kim", "2" + e);
		}
		return result;
	}

	public static void postSONfromURL(String url, Map params) {

		try {
			HttpPost httpost = new HttpPost(url);
			httpclient = getThreadsafeClient();
			JSONObject holder = getJsonObjectFromMap(params);
			String jsonStr = holder.toString();

			StringEntity se = new StringEntity(jsonStr, "UTF-8");
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			httpost.setEntity(se);

			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			JsonPostAsyncTask mclientPostAsyncTask = new JsonPostAsyncTask();
			mclientPostAsyncTask.execute(httpost);
			// client.execute(httpost);
		} catch (Exception e) {
			Log.e("kim", "1" + e);
		}
	}

	private static JSONObject getJsonObjectFromMap(Map params)
			throws JSONException {

		// all the passed parameters from the post request
		// iterator used to loop through all the parameters
		// passed in the post request
		Iterator iter = params.entrySet().iterator();

		// Stores JSON
		JSONObject holder = new JSONObject();

		// using the earlier example your first entry would get email
		// and the inner while would get the value which would be 'foo@bar.com'
		// { fan: { email : 'foo@bar.com' } }

		// While there is another entry
		while (iter.hasNext()) {
			// gets an entry in the params
			Map.Entry pairs = (Map.Entry) iter.next();

			// creates a key for Map
			String key = (String) pairs.getKey();

			// Create a new map
			Object m = (Object) pairs.getValue();

			// gets the value
			// Iterator iter2 = m.entrySet().iterator();
			// while (iter2.hasNext())
			// {
			// Map.Entry pairs2 = (Map.Entry)iter2.next();
			// data.put((String)pairs2.getKey(), (String)pairs2.getValue());
			// }

			holder.put(key, m);
		}
		return holder;
	}

	public static class JsonPostAsyncTask extends
			AsyncTask<HttpPost, Void, Void> {

		protected Void doInBackground(HttpPost... post) {
			try {
				httpclient.execute(post[0]);
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
}
