/**
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology.
 * All rights reserved.
 *
 * @author VGeirnaert
 */
package com.uvt.whitelab.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 */
public class QueryServiceHandler {

	private String webservice;
	private Map<Map<String, String[]>, String> requestsCache;
	public final int maxCacheSize;

	@SuppressWarnings("serial")
	public QueryServiceHandler(String url, int cacheSize) {
		maxCacheSize = cacheSize;
		webservice = url;

		// create a new linkedhashmap with an initial size of the maximum size it's allowed to be
		// a loadfactor of 0.75 and access-order (most recently accessed first) as ordering mode
		// also a remove eldest entry method to remove the last-accessed entry when we
		// reach our size limit
		requestsCache = new LinkedHashMap<Map<String, String[]>, String>(maxCacheSize, 0.75f, true ){
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<Map<String, String[]>, String> eldest) {
				return size() > maxCacheSize;
			}
		};
	}

	public String makeRequest(Map<String, String[]> params) throws IOException {
		// if the same request has already been cached, return that
		if(requestsCache.containsKey(params))
			return getResponseFromCache(params);

		String requestUrl = webservice;

		System.out.println("Request: " + requestUrl);

		// if not, send a request to the webserver
		URL webserviceRequest = new URL(requestUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(webserviceRequest.openStream()));

		// make url parameter string
		StringBuilder builder = new StringBuilder();
		String line;

		// read the response from the webservice
		while( (line = reader.readLine()) != null )
			builder.append(line);

		reader.close();

		String response = builder.toString();

		// also, cache this request
		cacheRequest(params, response);

		return response;
	}

	private void cacheRequest(Map<String, String[]> params, String response) {
		requestsCache.put(params, response);
	}

	/**
	 * Get the response string from the cache, may return null
	 *
	 * @param params
	 * @return String
	 */
	private String getResponseFromCache(Map<String, String[]> params) {
		return requestsCache.get(params);
	}

	/**
	 * Remove a request from the cache
	 *
	 * @param params
	 */
	public void removeRequestFromCache(Map<String, String[]> params) {
		requestsCache.remove(params);
	}

	public String getUrl() {
		return this.webservice;
	}
}
