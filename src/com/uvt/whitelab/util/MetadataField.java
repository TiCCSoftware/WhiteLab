/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MetadataField {
	private String name;
	private ResourceBundle labels;
	private int total;
	private int subtotal = 0;
	private List<String> valueLabels = new ArrayList<String>();
	private JSONArray fv = new JSONArray();
	private String unknown = "";
	private boolean completeList = false;
	private JSONObject data;
	
	public MetadataField(String fieldName, ResourceBundle lbls) {
		name = fieldName;
		labels = lbls;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getValues() {
		return valueLabels;
	}
	
	public boolean isComplete() {
		return completeList;
	}
	
	public Integer numberOfValues() {
		return valueLabels.size();
	}
	
	public Integer getTotal() {
		return total;
	}
	
	public Integer getSubTotal() {
		return subtotal;
	}
	
	public JSONObject getData() {
		return data;
	}
	
	public void load(boolean withValues) {
		String resp = getBlackLabResponse(this.labels.getString("corpus"), "/fields/"+name);
		Document xml = convertStringToDocument(resp);
		
		total = Integer.parseInt(this.labels.getString("documents.total"));
		subtotal = 0;
		valueLabels = new ArrayList<String>();
		fv = new JSONArray();
		
		try {
			XPath xPath =  XPathFactory.newInstance().newXPath();
			String expr = "//valueListComplete";
			String complete = xPath.compile(expr).evaluate(xml);
			if (complete.equals("false"))
				completeList = false;
			else
				completeList = true;
			expr = "//unknownValue";
			unknown = xPath.compile(expr).evaluate(xml);

			expr = "//value";
			NodeList nodeList = (NodeList) xPath.compile(expr).evaluate(xml, XPathConstants.NODESET);
			for (int n = 0; n < nodeList.getLength(); n++) {
				Node node = nodeList.item(n);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) node;
					String name = el.getAttribute("text");
					int value = Integer.parseInt(el.getTextContent());
					subtotal = subtotal + value;
					if (withValues) {
						valueLabels.add(name);
						JSONObject f = new JSONObject();
						f.put("name", name);
						f.put("size", value);
						fv.put(f);
					}
				}
			}

			if (withValues) {
				if (!completeList) {
					int value =  total - subtotal;
					if (!valueLabels.contains(unknown)) {
						JSONObject f = new JSONObject();
						f.put("name", this.labels.getString(unknown));
						f.put("size", value);
						fv.put(f);
					} else {
						JSONObject f = new JSONObject();
						f.put("name", this.labels.getString("other"));
						f.put("size", value);
						fv.put(f);
					}
				}
				
				data = new JSONObject();
				data.put("name", name);
				data.put("children", fv);
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected String getBlackLabResponse(String corpus, String trail) {
		String url = this.labels.getString("baseUrl")+"/"+corpus+trail;
		
		QueryServiceHandler webservice = new QueryServiceHandler(url, 1);
		try {
			String response = webservice.makeRequest(new HashMap<String, String[]>());
			System.out.println("Response received");
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

}
