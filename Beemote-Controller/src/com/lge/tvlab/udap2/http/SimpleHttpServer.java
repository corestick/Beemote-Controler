package com.lge.tvlab.udap2.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

public class SimpleHttpServer {
	int desirePort = 8080;
	HttpRequestHandlerRegistry reqistry = null;

	RequestListenerThread mainThread = null;

	public SimpleHttpServer(HttpRequestHandlerRegistry reqistry) {
		this.reqistry = reqistry;
	}

	public SimpleHttpServer(int port, HttpRequestHandlerRegistry reqistry) {
		desirePort = port;
		this.reqistry = reqistry;
	}

	public int startServer() {
		if (mainThread == null) {
			try {
				mainThread = new RequestListenerThread(desirePort, reqistry);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mainThread.start();

			return mainThread.getServerPort();
		}

		return 0;
	}

	public void stopServer() {
		if (mainThread != null) {
			mainThread.close();
			try {
				mainThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getServerPort() {
		if (mainThread != null) {
			return mainThread.getServerPort();
		}
		return 0;
	}

	static class RequestListenerThread extends Thread {

		private ServerSocket serversocket;
		private final HttpParams params;
		private final HttpService httpService;
		private boolean isStop = false;

		public RequestListenerThread(int port,
				HttpRequestHandlerRegistry reqistry) throws IOException {
			try {
				serversocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
				serversocket = new ServerSocket();
			}

			params = new BasicHttpParams();
			params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
					.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
							8 * 1024)
					.setBooleanParameter(
							CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
					.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
					.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
							"HttpComponents/1.1");

			// Set up the HTTP protocol processor
			BasicHttpProcessor httpproc = new BasicHttpProcessor();
			httpproc.addInterceptor(new ResponseDate());
	        httpproc.addInterceptor(new ResponseServer());
	        httpproc.addInterceptor(new ResponseContent());
	        httpproc.addInterceptor(new ResponseConnControl());
			
//			HttpRequestHandlerRegistry reqistry2 = new HttpRequestHandlerRegistry();			

			// Set up the HTTP service
			httpService = new HttpService(httpproc,
					new DefaultConnectionReuseStrategy(),
					new DefaultHttpResponseFactory());
			httpService.setParams(params);
			this.httpService.setHandlerResolver(reqistry);
		}

		public int getServerPort() {
			if (serversocket != null) {
				return serversocket.getLocalPort();
			}

			return 0;
		}

		@Override
		public void run() {
			System.out.println("Listening on port "
					+ this.serversocket.getLocalPort());
			while (!Thread.interrupted() && !isStop) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					System.out.println("Incoming connection from "
							+ socket.getInetAddress());
					conn.bind(socket, this.params);
					
					// Start worker thread
					Thread t = new WorkerThread(this.httpService, conn);
					t.setDaemon(true);
					t.start();
				} catch (InterruptedIOException ex) {
					break;
				} catch (IOException e) {
					e.printStackTrace();
					System.err
							.println("I/O error initialising connection thread: "
									+ e.getMessage());
					break;
				}
			}
		}

		public void close() {
			isStop = true;
			if (serversocket != null) {
				try {
					serversocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class WorkerThread extends Thread {

		private final HttpService httpservice;
		private final HttpServerConnection conn;

		public WorkerThread(final HttpService httpservice,
				final HttpServerConnection conn) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			System.out.println("New connection thread");
			HttpContext context = new BasicHttpContext(null);			
			try {
				while (!Thread.interrupted() && this.conn.isOpen()) {				
					this.httpservice.handleRequest(this.conn, context);
				}
			} catch (ConnectionClosedException ex) {
				System.err
						.println("Client closed connection : " + ex.getMessage());
			} catch (IOException ex) {
				System.err.println("I/O error: " + ex.getMessage());
			} catch (HttpException ex) {
				System.err.println("Unrecoverable HTTP protocol violation: "
						+ ex.getMessage());
			} finally {
				try {
					this.conn.shutdown();
				} catch (IOException ignore) {
				}
			}
		}
	}
}
