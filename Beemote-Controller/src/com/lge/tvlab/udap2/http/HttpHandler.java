package com.lge.tvlab.udap2.http;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class HttpHandler implements HttpRequestHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		// TODO Auto-generated method stub
		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")
				&& !method.equals("POST")) {
			throw new MethodNotSupportedException(method
					+ " method not supported");
		}
		
		
	}

}
