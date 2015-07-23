/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab;

import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.uvt.whitelab.response.ErrorResponse;
import com.uvt.whitelab.response.ExportResponse;
import com.uvt.whitelab.response.HomeResponse;
import com.uvt.whitelab.response.TreemapResponse;
import com.uvt.whitelab.response.explore.CorpusResponse;
import com.uvt.whitelab.response.explore.ExploreDocumentResponse;
import com.uvt.whitelab.response.explore.NgramsResponse;
import com.uvt.whitelab.response.explore.StatisticsResponse;
import com.uvt.whitelab.response.search.AdvancedResponse;
import com.uvt.whitelab.response.search.ExpertResponse;
import com.uvt.whitelab.response.search.ExtendedResponse;
import com.uvt.whitelab.response.search.ResultResponse;
import com.uvt.whitelab.response.search.SearchDocumentResponse;
import com.uvt.whitelab.response.search.SimpleResponse;
import com.uvt.whitelab.util.FieldDescriptor;
import com.uvt.whitelab.util.MetadataField;
import com.uvt.whitelab.util.MetadataHtmlGenerator;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.WhitelabDocument;

/**
 *
 */
public class WhiteLab extends HttpServlet {

	private final String VELOCITY_PROPERTIES = "/WEB-INF/config/velocity.properties";
	private String realPath = "";
	private VelocityContext context;
	private Map<String, Template> templates = new HashMap<String, Template>();
	private Map<String, BaseResponse> responses = new HashMap<String, BaseResponse>();
	private Logger logger;
	private XMLConfiguration xmlConfig;
	private Map<String,LinkedList<WhitelabDocument>> documents = new HashMap<String,LinkedList<WhitelabDocument>>();
	public String contextRoot;
	private MetadataHtmlGenerator generator;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);

		logger = Logger.getLogger("Whitelab");
		try {
			log("Initializing Whitelab, Memory usage: "+getCurrentMemUsage());
		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e1) {
			e1.printStackTrace();
		}
		
		documents.put("nl", new LinkedList<WhitelabDocument>());
		documents.put("en", new LinkedList<WhitelabDocument>());
		
		loadFields();

		try {
			startVelocity(cfg);
			
			// Find extracted WAR path
			realPath = cfg.getServletContext().getRealPath("/");
			
			// Find our context root
			contextRoot = cfg.getServletContext().getContextPath();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// initialise responses
		responses.put(contextRoot + "/page/treemap", new TreemapResponse("explore"));
		responses.put(contextRoot + "/page/export", new ExportResponse("home"));
		responses.put(contextRoot + "/page/home", new HomeResponse("home"));
		responses.put(contextRoot + "/page/error", new ErrorResponse("error"));
		responses.put("home", new HomeResponse("home"));
		responses.put("error", new ErrorResponse("error"));

		// initialise Explore responses
		responses.put(contextRoot + "/explore/corpus", new CorpusResponse("explore"));
		responses.put(contextRoot + "/explore/statistics", new StatisticsResponse("explore"));
		responses.put(contextRoot + "/explore/ngrams", new NgramsResponse("explore"));
		responses.put(contextRoot + "/explore/document", new ExploreDocumentResponse("explore"));
		
		// initialise Search response
		responses.put(contextRoot + "/search/simple", new SimpleResponse("search"));
		responses.put(contextRoot + "/search/extended", new ExtendedResponse("search"));
		responses.put(contextRoot + "/search/advanced", new AdvancedResponse("search"));
		responses.put(contextRoot + "/search/expert", new ExpertResponse("search"));
		responses.put(contextRoot + "/search/results", new ResultResponse("search"));
		responses.put(contextRoot + "/search/document", new SearchDocumentResponse("search"));
		
		try {
			log("Done initializing Whitelab, Memory usage: "+getCurrentMemUsage());
		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e1) {
			e1.printStackTrace();
		}
	}
	
	private void loadFields() {
		generator = new MetadataHtmlGenerator(this);
		ResourceBundle labels = ResourceBundle.getBundle("WhitelabBundle", new Locale("nl"));
		ResultHandler resultHandler = new ResultHandler(this, labels);
		String resp = resultHandler.getBlackLabResponse(labels.getString("blsUrlInternal") + "/" + labels.getString("corpus"));
		Document xml = convertStringToDocument(resp);
		generator.init(labels, xml);
	}
	
	public MetadataHtmlGenerator getMetadataHtmlGenerator() {
		return generator;
	}
	
	public List<MetadataField> getMetadataFields() {
		return generator.getFilterFields();
	}
	
	public LinkedList<FieldDescriptor> getSearchFields() {
		return generator.getSearchFields();
	}
 
    private static Document convertStringToDocument(String xmlStr) {
        try {  
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }

	@Override
	public void log(String msg) {
		this.logger.info(msg);
	}

	/**
	 * Start the templating engine
	 * @param argConfig the configuration object
	 * @throws Exception
	 */
	private void startVelocity(ServletConfig argConfig) throws Exception {
		
		context = new VelocityContext();
		Velocity.setApplicationAttribute("javax.servlet.ServletContext", argConfig.getServletContext());
		
		Velocity.init(argConfig.getServletContext().getRealPath(VELOCITY_PROPERTIES));
	}

	/**
	 * Get the velocity template
	 * @param argName name of the template
	 * @return velocity template
	 */
	public synchronized Template getTemplate(String argName) {
		argName = argName + ".vm";

		// if the template exists
		if(Velocity.resourceExists(argName)) {
			// if the template was already loaded
			if(templates.containsKey(argName)) {
				return templates.get(argName);
			}

			// template wasn't loaded yet - try to load it now
			try {
				// load the template
				Template t = Velocity.getTemplate(argName, "utf-8");
				// store it
				templates.put(argName, t);
				return t;
			} catch (Exception e) {
				// Something went wrong, we die
				throw new RuntimeException(e);
			}

		}

		// The template doesn't exist so we'll display an error page

		// it is important that the error template is available
		// or we'll end up in an infinite loop
		context.put("error", "Unable to find template " + argName);
		return getTemplate("error");
	}
	
	public XMLConfiguration getConfig() {
		return xmlConfig;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		processRequest(request, response);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
		
		BaseResponse br;
		System.out.println("MAINSERVLET - Request URI: "+request.getRequestURI());
		// get corresponding response object
		if(responses.containsKey(request.getRequestURI())) {
			br = responses.get(request.getRequestURI()).duplicate();
		} else {
			// if there is no corresponding response object
			// redirect to home
			br = responses.get("home");
		}
		
		br.init(request, response, this);
		br.processRequest();

	}

	public String getRealPath() {
		return realPath;
	}
	
	public boolean hasDocument(String docPid, String lang) {
		for (WhitelabDocument doc : documents.get(lang)) {
			if (doc.id.equals(docPid))
				return true;
		}
		return false;
	}
	
	public WhitelabDocument getDocument(String docPid, String lang) {
		for (WhitelabDocument doc : documents.get(lang)) {
			if (doc.id.equals(docPid))
				return doc;
		}
		return null;
	}
	
	public void addDocument(WhitelabDocument doc, String lang) {
		documents.get(lang).add(doc);
		
		if (documents.get(lang).size() > 10)
			documents.put(lang, (LinkedList<WhitelabDocument>) documents.get(lang).subList(documents.size() - 10, documents.size() - 1));
	}
	
	public String getCurrentMemUsage() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		MBeanServer connection = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectInstance> set = connection.queryMBeans(new ObjectName("java.lang:type=Memory"), null);
		ObjectInstance oi = set.iterator().next();
		// replace "HeapMemoryUsage" with "NonHeapMemoryUsage" to get non-heap mem
		Object attrValue = connection.getAttribute(oi.getObjectName(), "HeapMemoryUsage");
		if( !( attrValue instanceof CompositeData ) ) {
		    System.out.println( "attribute value is instanceof [" + attrValue.getClass().getName() +
		            ", exiting -- must be CompositeData." );
		    return "";
		}
		// replace "used" with "max" to get max
		return ((CompositeData)attrValue).get("used").toString();
	}

}

