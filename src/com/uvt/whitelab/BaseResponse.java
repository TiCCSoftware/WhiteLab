/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.uvt.whitelab.util.Query;
import com.uvt.whitelab.util.SessionManager;

/**
 *
 */
public abstract class BaseResponse {

	private final String OUTPUT_ENCODING = "UTF-8";

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected WhiteLab servlet;
	private VelocityContext context = new VelocityContext();
	protected ResourceBundle labels;
	protected Locale locale;
	protected String lastUrl = null;
	protected String lang;
	protected HttpSession session;
	protected Query query;
	protected int queryCount = 0;
	protected String namespace;
	
	protected long startTime = new Date().getTime();

	protected BaseResponse(String ns) {
		namespace = ns;
	}
	
	public WhiteLab getServlet() {
		return this.servlet;
	}

	/**
	 * Initialise this object with
	 * @param argRequest
	 * @param argResponse
	 * @param argServlet
	 */
	public void init(HttpServletRequest argRequest, HttpServletResponse argResponse, WhiteLab argServlet) {
		request = argRequest;
		response = argResponse;
		servlet = argServlet;
		session = validateSession();
	}

	/**
	 * Get the velocity context object
	 *
	 * @return velocity context
	 */
	protected VelocityContext getContext() {
		return context;
	}

	protected void clearContext() {
		context = new VelocityContext();
	}
	
	protected void initQuery() {
		query = null;
		String id = this.getParameter("id", "");
		String patt = this.getParameter("query", "").replaceAll("&", "%26");
		String within = this.getParameter("within", "");
		int view = this.getParameter("view", 1);
		int from = this.getParameter("from", 1);
		boolean editQuery = Boolean.parseBoolean(this.getParameter("edit", "false"));
		boolean deleteQuery = Boolean.parseBoolean(this.getParameter("delete", "false"));
		boolean updateQuery = true;
		
		this.servlet.log("QUERY VIEW: "+view);
		
		if (id.length() > 0 && view != 9 && view != 17) {
			query = SessionManager.getQuery(session, id, from);
			if (query == null || (from <= 4 && patt.length() > 0 && !query.equalPattern(patt,within)))
				id = "";
			else if (query != null && patt.length() == 0)
				updateQuery = false;
		}
		
		if (id.length() == 0 && patt.length() > 0) {
			this.servlet.log("NEW QUERY");
			query = new Query(this);
			id = query.getId();
			if (view != 9 && view != 17)
				SessionManager.addQuery(session, query);
		}
		
		if (query != null && !editQuery && !deleteQuery && updateQuery) {
			query = query.updateQuery(this);
			if (!id.equals(query.getId()) && view != 9 && view != 17)
				SessionManager.addQuery(session, query);
		}
		
		if (query != null && view != 9 && view != 17 && namespace.equals("search"))
			SessionManager.setCurrentQuery(session, query.getId());
	}
	
	protected void initTourQuery() {
		query = null;
		
		String id = this.getParameter("id", "");
		String patt = this.getParameter("query", "").replaceAll("&", "%26");
		String within = this.getParameter("within", "");
		int view = this.getParameter("view", 1);
		int from = 0;
		
		boolean editQuery = Boolean.parseBoolean(this.getParameter("edit", "false"));
		boolean deleteQuery = Boolean.parseBoolean(this.getParameter("delete", "false"));
		boolean updateQuery = true;

		this.servlet.log("TOUR QUERY VIEW: "+view);
		this.servlet.log("TOUR QUERY FROM: "+this.getParameter("from", 0));
		
		if (id.length() > 0 && view != 9 && view != 17) {
			query = SessionManager.getQuery(session, id, from);
			if (query == null || (patt.length() > 0 && !query.equalPattern(patt,within)))
				id = "";
			else if (query != null && patt.length() == 0)
				updateQuery = false;
		}
		
		if (id.length() == 0 && patt.length() > 0) {
			this.servlet.log("NEW TOUR QUERY");
			query = new Query(this);
			id = query.getId();
		}
		
		if (query != null && !editQuery && !deleteQuery && updateQuery) {
			query = query.updateQuery(this);
			query.setFrom(0);
		} else if (deleteQuery) {
			SessionManager.setTour(session, 0);
			query = null;
		}
		
		if (query != null) {
			SessionManager.addQuery(session, query);
			this.getContext().put("tourQuery", query);
		}
	}

	/**
	 * Display a specific template, with specific mime type
	 *
	 * @param argT
	 * @param mime
	 */
	protected void displayTemplate(Template argT, String mime) {
		// Set the content headers for the response
		response.setCharacterEncoding(OUTPUT_ENCODING);
		response.setContentType(mime);

		// Merge context into the page template and write to output stream
		try {
			OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), OUTPUT_ENCODING);
			try {
				argT.merge(getContext(), osw);
				osw.flush();
			} finally {
				osw.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String applyTemplate(Template argT) {
		StringWriter writer = new StringWriter();
        argT.merge( getContext(), writer );
        return writer.toString();
	}
	
	protected String applyHtmlTemplate(Template argT) {
		return applyTemplate(argT);
	}

	/**
	 * Display a template with the XML mime type
	 * @param argT
	 */
	protected void displayHtmlTemplate(Template argT) {
		displayTemplate(argT, "text/html");
	}

	/**
	 * Calls the completeRequest and logRequest implementations
	 */
	final public void processRequest() {
		this.getContext().put("startTime", this.startTime);
		try {
			this.getContext().put("memUsageStart", this.servlet.getCurrentMemUsage());
		} catch (MalformedObjectNameException | AttributeNotFoundException
				| InstanceNotFoundException | MBeanException
				| ReflectionException e1) {
			e1.printStackTrace();
		}
		try {
			this.servlet.log("("+this.getClass()+", patt="+this.getParameter("query", "")+") Start memory usage: "+this.servlet.getCurrentMemUsage());
		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
			e.printStackTrace();
		}
		
		this.setLocale();
		
		SessionManager.setTour(session, this.getParameter("tour", 0));

		if (SessionManager.isOnTour(session)) {
			this.initTourQuery();
			if (query != null)
				SessionManager.setCurrentQuery(session, query.getId());
			this.getContext().put("tour", SessionManager.getTourStep(session));
		} else
			this.initQuery();

		this.updateQueryCount();
		
		logRequest();
		
		completeRequest();
		
		try {
			this.getContext().put("memUsageEnd", this.servlet.getCurrentMemUsage());
		} catch (MalformedObjectNameException | AttributeNotFoundException
				| InstanceNotFoundException | MBeanException
				| ReflectionException e1) {
			e1.printStackTrace();
		}
		this.getContext().put("endTime", new Date().getTime());
	}
	
	protected void setLocale() {
		this.locale = (Locale) session.getAttribute("locale");
		if (this.locale == null) {
			this.locale = request.getLocale();
			session.setAttribute("locale", this.locale);
		}
		
		this.lang = this.request.getParameter("lang");
		
		if (this.lang != null && !this.lang.equals(this.locale)) {
			this.locale = new Locale(this.lang);
			session.setAttribute("locale", this.locale);
		} else if (this.lang == null) {
			this.lang = this.locale.getLanguage();
		}
		this.labels = ResourceBundle.getBundle("WhitelabBundle", this.locale);
		
		this.getContext().put("lang", this.lang);
		this.getContext().put("labels", this.labels);
		this.getContext().put("requestUrlNL", this.getURL("lang=nl"));
		this.getContext().put("requestUrlEN", this.getURL("lang=en"));
	}
	
	protected String getURL(String params) {
	    String contextPath = this.request.getContextPath();
	    String servletPath = this.request.getServletPath();
	    String pathInfo = this.request.getPathInfo();

	    StringBuffer url =  new StringBuffer();

	    url.append(contextPath).append(servletPath);

	    if (pathInfo != null) {
	        url.append(pathInfo);
	    }
	    
	    url.append("?").append(params);
	    
	    return url.toString();
	}
	
	protected void updateQueryCount() {
		queryCount = SessionManager.getQueryCount(session);
		this.getContext().put("queryCount", queryCount);
	}

	/**
	 * Returns the value of a servlet parameter, or the default value
	 *
	 * @param name
	 *            name of the parameter
	 * @param defaultValue
	 *            default value
	 * @return value of the paramater
	 */
	public String getParameter(String name, String defaultValue) {
		// get the trimmed parameter value
		String value = request.getParameter(name);

		if(value != null) {
			value = value.trim();

			// if the parameter value is an empty string
			if (value.length() == 0)
				value = defaultValue;
		} else {
			value = defaultValue;
		}

		return value;
	}

	/**
	 * Returns the value of a servlet parameter, or the default value
	 *
	 * @param name
	 *            name of the parameter
	 * @param defaultValue
	 *            default value
	 * @return value of the paramater
	 */
	public int getParameter(String name, int defaultValue) {
		final String stringToParse = getParameter(name, "" + defaultValue);
		try {
			return Integer.parseInt(stringToParse);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public String[] getParameterValues(String name, String defaultValue) {
		String[] values = this.request.getParameterValues(name);

		if(values == null)
//			logger.debug("No values found for label "+name);
			values = new String[]{};

		return values;
	}

	public List<String> getParameterValuesAsList(String name, String defaultValue) {
		return Arrays.asList(getParameterValues(name, defaultValue));
	}

	public Integer getParameter(String name, Integer defaultValue) {
		final String stringToParse = getParameter(name, "" + defaultValue);

		return new Integer(stringToParse);
	}

	/**
	 * Returns the value of a servlet parameter, or the default value
	 *
	 * @param name
	 *            name of the parameter
	 * @param defaultValue
	 *            default value
	 * @return value of the paramater
	 */
	public boolean getParameter(String name, boolean defaultValue) {
		return getParameter(name, defaultValue ? "on" : "").equals("on");
	}

	protected String loadStylesheet(String name) throws IOException {
		// clear string builder
		StringBuilder builder = new StringBuilder();
		builder.delete(0, builder.length());

		BufferedReader br = new BufferedReader(new FileReader(this.servlet.getRealPath() + "WEB-INF/stylesheets/" + name ));

		String line;
		
		this.servlet.log("Loading stylesheet: "+name);

		// read the response from the webservice
		while( (line = br.readLine()) != null )
			builder.append(line);

		br.close();

		return builder.toString();
	}

	protected void loadMetaDataComponents(boolean includeQueryData) {
		Map<String,String> options = this.servlet.getMetadataHtmlGenerator().generateOptions(labels);
		Map<String,String> selectFields = this.servlet.getMetadataHtmlGenerator().loadSelectFields(labels);
		SortedSet<String> filters = this.servlet.getMetadataHtmlGenerator().loadFilters(labels);
		if (includeQueryData && query.getFilters().size() > 0)
			this.getContext().put("queryRules", this.servlet.getMetadataHtmlGenerator().generateQueryRules(labels, filters, options, query));
		this.getContext().put("metaRule", this.servlet.getMetadataHtmlGenerator().generateEmptyRule(labels, filters, options));
		this.getContext().put("filters", filters);
		this.getContext().put("metaOptions",options);
		this.getContext().put("metaSelect",selectFields);
		this.getContext().put("generator", this.servlet.getMetadataHtmlGenerator());
	}

	protected void sendResponse(Map<String,Object> output) {
		long timePassed = new Date().getTime() - this.startTime;
		try {
			this.servlet.log("("+this.getClass()+", patt="+this.getParameter("query", "")+") End memory usage: "+this.servlet.getCurrentMemUsage()+", execution time: "+timePassed);
		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
			e.printStackTrace();
		}
		JSONObject resp = new JSONObject();
		try {
			for (String key : output.keySet()) {
				resp.put(key, output.get(key));
			}
			response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST");
	        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	        response.setHeader("Access-Control-Max-Age", "3600");
			response.setContentType("application/x-javascript; charset=utf-8");
			response.getWriter().write(resp.toString());
			response.getWriter().close();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void sendCsvResponse(String csv) {
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");
	    response.setHeader("Content-Type", "text/csv");
	    response.setHeader("Content-Disposition", "attachment;filename=\"file.csv\"");
	    
	    BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
		    writer.append(csv);
		    writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void sendFileResponse(String contents, String fileName) {
        // Set HTTP headers
		byte[] bytes;
		try {
			bytes = contents.getBytes("UTF-8");
			response.setContentType("application/octet-stream");
	        response.setContentLength(bytes.length);
	        response.setHeader("Content-Disposition", "attachment; filename=\"whitelab_" + fileName + "\"");
	        response.setCharacterEncoding("UTF-8");
	        
	        ServletOutputStream outStream = null;
			try {
				outStream = response.getOutputStream();
				try {
					outStream.write(bytes);
				} finally {
			        outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	protected HttpSession validateSession() {
		HttpSession sesh = request.getSession();
		sesh.setMaxInactiveInterval(30*60);
		return sesh;
	}
	
	protected String getRequestURL(boolean includeHost) {

	    String scheme = request.getScheme();             // http
	    String serverName = request.getServerName();     // hostname.com
	    int serverPort = request.getServerPort();        // 80
	    String contextPath = request.getContextPath();   // /mywebapp
	    String servletPath = request.getServletPath();   // /servlet/MyServlet
	    String pathInfo = request.getPathInfo();         // /a/b;c=123
	    String queryString = request.getQueryString();          // d=789

	    // Reconstruct original requesting URL
	    StringBuffer url =  new StringBuffer();
	    if (includeHost) {
		    url.append(scheme).append("://").append(serverName);
	
		    if ((serverPort != 80) && (serverPort != 443)) {
		        url.append(":").append(serverPort);
		    }
	    }

	    url.append(contextPath).append(servletPath);

	    if (pathInfo != null) {
	        url.append(pathInfo);
	    }
	    if (queryString != null) {
	        url.append("?").append(queryString);
	    }
	    return url.toString();
	}

	/**
	 * Complete the request - automatically called by processRequest()
	 */
	abstract protected void completeRequest();

	/**
	 * Log the request - automatically called by processRequest()
	 */
	abstract protected void logRequest();

	abstract public BaseResponse duplicate();

}
