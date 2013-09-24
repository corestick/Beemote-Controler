package com.lge.tv.a2a.client;

import java.io.IOException;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;

import com.latebutlucky.beemote_controller.TvAppInfo;

/**
 * Represent App to App Android Client.
 * 
 * @author snopy.lee
 * 
 */
public abstract class A2AClient {
	A2AEventListener eventListener = null;
	A2AMessageListener messageListener = null;
	A2ATVInfo a2atvInfo = null;
	public Vector<TvAppInfo> TvAppList = new Vector<TvAppInfo>();

	public class QueryResultAppID {
		A2ACmdError error;
		long appId;

		public A2ACmdError getError() {
			return error;
		}

		public long getAppId() {
			return appId;
		}
	}

	public class QueryResultStatus {
		A2ACmdError error;
		A2AStatus status;

		public A2ACmdError getError() {
			return error;
		}

		public A2AStatus getA2AStatus() {
			return status;
		}
	}

	public enum A2ACmdError {
		A2ACmdErrorOK, A2ACmdErrorBadRequest, A2ACmdErrorUnauthorized, A2ACmdErrorNotFound, A2ACmdErrorNotAcceptable, A2ACmdErrorRequestTimeout, A2ACmdErrorConflict, A2ACmdErrorInternalServerError, A2ACmdErrorServiceUnavailable, A2ACmdErrorNoCurrentTV, A2ACmdErrorUnknown,
	}

	public enum A2AStatus {
		A2AStatusNone, A2AStatusLoad, A2AStatusRun, A2AStatusRunNotFocused, A2AStatusTerm, A2AStatusUnknown,
	}

	protected A2AClient() {

	}

	/**
	 * Add listener that receive message from TV.
	 * 
	 * @param listener
	 *            the listener invoked for all the callbacks.
	 */
	public void setEventListener(A2AEventListener listener) {
		eventListener = listener;
	}

	/**
	 * Add listener that receive event from TV. (searchResult)
	 * 
	 * @param listener
	 *            the listener invoked for all the callbacks.
	 */
	public void setMessageListener(A2AMessageListener listener) {
		messageListener = listener;
	}

	/**
	 * Select target TV to communication.
	 * 
	 * @param info
	 *            TV info from searchTV.
	 */
	public void setCurrentTV(A2ATVInfo info) {
		this.a2atvInfo = info;
	}

	/**
	 * Return current TV info.
	 * 
	 * @return TV info.
	 */
	public A2ATVInfo getCurrentTV() {
		return a2atvInfo;
	}

	/**
	 * Search TVs that support LG's UDAP protocol.
	 * 
	 * @param context
	 *            Android context
	 * @return he TV infos that available to connect.
	 */
	abstract public boolean searchTV(Context context);

	/**
	 * Request to TV show passcode UI.
	 * 
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError showPasscode() throws IOException;

	/**
	 * Request to TV hide passcode UI.
	 * 
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError hidePasscode() throws IOException;

	/**
	 * Connect to target TV.
	 * 
	 * @param passcode
	 *            the codes TV show.
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError connect(String passcode) throws IOException;

	/**
	 * Disconnect from the TV.
	 * 
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError disconnect() throws IOException;

	abstract public QueryResultAppID queryAppId(String appName)
			throws IOException;

	abstract public QueryResultStatus queryAppStatus(long appId)
			throws IOException;

	/**
	 * Send a char array message to the TV.
	 * 
	 * @param appId
	 *            target TV application ID.
	 * @param messageType
	 *            the message type want to send to TV.
	 * @param message
	 *            the message want to send to TV.
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError sendMessage(long appId, int messageType,
			String message) throws IOException;

	/**
	 * Execution an application.
	 * 
	 * @param appId
	 *            TV Application ID.
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError executeApp(long appId) throws IOException;

	/**
	 * Terminate an application.
	 * 
	 * @param appId
	 *            TV Application ID.
	 * @return command result
	 * @throws IOException
	 *             in case of a problem or the connection was aborted
	 */
	abstract public A2ACmdError terminateApp(long appId) throws IOException;

	abstract public void tvAppQuery() throws IOException;

	abstract public Bitmap tvAppIconQuery(String auid, String appName)
			throws IOException;

	abstract public void exe() throws IOException;

	abstract public void handleKey() throws IOException;

	abstract public void keywordSend(String str) throws IOException;

}
