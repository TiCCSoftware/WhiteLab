/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.MetadataField;
import com.uvt.whitelab.util.StringCleaner;
import com.uvt.whitelab.util.XslTransformer;

public class QueryResponse extends BaseResponse {
	private XslTransformer transformer = new XslTransformer();
	private Integer view = 1;
	private boolean getCount = false;

	@Override
	protected void completeRequest() {
		
		String corpus = this.labels.getString("corpus");
		getCount = this.getParameter("count", false);
		
		Map<String,Object> output = new HashMap<String,Object>();
		output.put("startTime", this.startTime);
		try {
			output.put("memUsageStart", this.servlet.getCurrentMemUsage());
		} catch (MalformedObjectNameException | AttributeNotFoundException
				| InstanceNotFoundException | MBeanException
				| ReflectionException e1) {
			e1.printStackTrace();
		}
		String v = this.getParameter("view", "");
		
		if (this.params.keySet().size() > 0 || v.length() > 0) {
			view = this.getParameter("view", 1);
			this.servlet.log("VIEW: "+v+" "+view);
			
			String trail = "/hits";
			if (view == 2 || view == 4 || view == 16)
				trail = "/docs";
			
			if (view >= 8 && !this.params.containsKey("group")) {
				
				String html = "<p>ERROR: Could not parse XML result.</p>";
				try {
					String stylesheet = getStylesheet(view,true);
					html = parseResult("<?xml version=\"1.0\" encoding=\"UTF-8\"?><empty></empty>",stylesheet);
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				output.put("id", this.getParameter("id", 1));
				output.put("html", html);
				
				try {
					output.put("memUsageEnd", this.servlet.getCurrentMemUsage());
				} catch (MalformedObjectNameException | AttributeNotFoundException
						| InstanceNotFoundException | MBeanException
						| ReflectionException e1) {
					e1.printStackTrace();
				}
				output.put("endTime", new Date().getTime());
				
				sendResponse(output);
				
			} else {
			
				String resp = getBlackLabResponse(corpus, trail, this.params);
				String counting = isStillCounting(resp);
				Integer hits = getHitsFromXML(resp);
				Integer docs = getDocsFromXML(resp);

				output.put("id", this.getParameter("id", 1));
				output.put("hits", hits);
				output.put("docs", docs);
				output.put("counting", counting);
				
				if (view >= 8) {
					Integer groups = getGroupsFromXML(resp);
					output.put("groups", groups);
				}
				
				if (!getCount) {
					String html = "<p>ERROR: Could not parse XML result.</p>";
					try {
						String stylesheet = getStylesheet(view,false);
						html = parseResult(resp,stylesheet);
						if (view == 12 && !this.params.containsKey("first")) {
							String wordcloudStylesheet = getStylesheet(view,true);
							String str = parseResult(resp,wordcloudStylesheet);
							JSONArray cloud = new JSONArray(str);
							cloud = cleanCloudLemmas(cloud);
							output.put("cloud", cloud);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
		
					output.put("html", html);
					
				}
				output.put("view", view);
				
				try {
					output.put("memUsageEnd", this.servlet.getCurrentMemUsage());
				} catch (MalformedObjectNameException | AttributeNotFoundException
						| InstanceNotFoundException | MBeanException
						| ReflectionException e1) {
					e1.printStackTrace();
				}
				output.put("endTime", new Date().getTime());
				
				sendResponse(output);
				
			}
		} else {
			output.put("html", "<p>ERROR: Insufficient parameters.</p>");
			output.put("hits", 0);
			output.put("docs", 0);
			output.put("counting", "false");
			
			try {
				output.put("memUsageEnd", this.servlet.getCurrentMemUsage());
			} catch (MalformedObjectNameException | AttributeNotFoundException
					| InstanceNotFoundException | MBeanException
					| ReflectionException e1) {
				e1.printStackTrace();
			}
			output.put("endTime", new Date().getTime());
			
			sendResponse(output);
		}
	}

	private JSONArray cleanCloudLemmas(JSONArray cloud) {
		for (int i = 0; i < cloud.length(); i++) {
			try {
				JSONObject line = cloud.getJSONObject(i);
				if (line.has("lemma"))
					line.put("clean", StringCleaner.clean(line.getString("lemma")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return cloud;
	}

	private String isStillCounting(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<stillCounting>(.+?)</stillCounting>");
			final Matcher matcher = pattern.matcher(response);
			
			if (matcher.find())
				return matcher.group(1);
		}
		return "false";
	}

	private Integer getGroupsFromXML(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<numberOfGroups>(.+?)</numberOfGroups>");
			final Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}
		return -1;
	}

	private Integer getDocsFromXML(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<numberOfDocs>(.+?)</numberOfDocs>");
			final Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}
		return -1;
	}

	private Integer getHitsFromXML(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<numberOfHits>(.+?)</numberOfHits>");
			final Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}
		return -1;
	}

	private String getStylesheet(Integer view, boolean check) throws IOException {
		String stylesheet = "";
		if (view == 1) {
			stylesheet = loadStylesheet("perhitresults.xsl");
		} else if (view == 2) {
			stylesheet = loadStylesheet("perdocresults.xsl");
		} else if (view == 4) {
			stylesheet = loadStylesheet("perdocstatsresults.xsl");
		} else if (view == 8) {
			stylesheet = loadStylesheet("groupperhitresults.xsl");
		} else if (view == 10) {
			stylesheet = loadStylesheet("groupperhitngramresults.xsl");
		} else if (view == 12 && !check) {
			stylesheet = loadStylesheet("groupperhitstatsresults.xsl");
		} else if (view == 12 && check) {
			stylesheet = loadStylesheet("wordcloudnopos.xsl");
		} else if (view == 16) {
			stylesheet = loadStylesheet("groupperdocresults.xsl");
		}
		return stylesheet;
	}

	private String parseResult(String response,String stylesheet) {
		try {
			setTransformerDisplayParameters();
			return transformer.transform(response, stylesheet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void setTransformerDisplayParameters() throws UnsupportedEncodingException {
		transformer.clearParameters();
		transformer.addParameter("urlparamwithoutstart", this.lastUrl + "?" + getParameterStringExcept(new String[] {"first"}));
		transformer.addParameter("urlparamwithoutvieworgroup", this.lastUrl + "?" + getParameterStringExcept(new String[] {"view", "groupBy"}));
		transformer.addParameter("urlparamwithoutsort", this.lastUrl + "?" + getParameterStringExcept(new String[] {"sortBy"}));
		if (this.params.containsKey("patt")) {
			transformer.addParameter("urlparamquery", URLEncoder.encode((String) this.params.get("patt"), "UTF-8"));
			transformer.addParameter("query", (String) this.params.get("patt"));
		}
		transformer.addParameter("baseurl", this.labels.getString("baseUrl"));
		transformer.addParameter("webserviceurl", this.labels.getString("baseUrl") + "/" + this.labels.getString("corpus"));
		transformer.addParameter("backendRequestUrl", this.lastUrl + "?" + getParameterStringExcept(new String[] {}));

		transformer.addParameter("query_id",this.getParameter("id", "1"));
		transformer.addParameter("per_hit",this.labels.getString("result.per_hit"));
		transformer.addParameter("per_doc",this.labels.getString("result.per_doc"));
		transformer.addParameter("grouped_per_hit",this.labels.getString("result.grouped_per_hit"));
		transformer.addParameter("grouped_per_doc",this.labels.getString("result.grouped_per_doc"));
		transformer.addParameter("context_left",this.labels.getString("context") + " " + this.labels.getString("left"));
		transformer.addParameter("context_right",this.labels.getString("context") + " " + this.labels.getString("right"));
		transformer.addParameter("word",this.labels.getString("word"));
		transformer.addParameter("lemma",this.labels.getString("lemma"));
		transformer.addParameter("pos",this.labels.getString("pos"));
		transformer.addParameter("result_export",this.labels.getString("result.export"));
		transformer.addParameter("result_pagination_show",this.labels.getString("result.pagination.show"));
		transformer.addParameter("result_per_page",this.labels.getString("result.per_page"));
		transformer.addParameter("result_titles",this.labels.getString("result.titles"));
		transformer.addParameter("result_page",this.labels.getString("result.page"));
		transformer.addParameter("result_go",this.labels.getString("result.go"));
		transformer.addParameter("result_of",this.labels.getString("result.of"));
		transformer.addParameter("by",this.labels.getString("result.by"));
		transformer.addParameter("max", this.getParameter("number", "50"));
		
		String groupBy_name = this.getParameter("groupBy", "");
		transformer.addParameter("groupBy_name", groupBy_name);
		groupBy_name = groupBy_name.replaceAll("field:", "");
		transformer.addParameter("groupBy_name_clean", groupBy_name);

		transformer.addParameter("collection_name", this.labels.getString("result.collection_name"));
		transformer.addParameter("title_name", this.labels.getString("result.title_name"));
		transformer.addParameter("author_name", this.labels.getString("result.author_name"));
		transformer.addParameter("date_name", this.labels.getString("result.date_name"));
		transformer.addParameter("lemma_name", this.labels.getString("result.lemma_name"));
		transformer.addParameter("pos_name", this.labels.getString("result.pos_name"));
		
		if (view == 8)
			addGroupOptions(true);
		else if (view == 16)
			addGroupOptions(false);
	}

	private void addGroupOptions(boolean addHitFields) {
		List<String> options = new ArrayList<String>();
		
		if (addHitFields) {
			String[] fields = new String[]{"word","lemma","pos"};
			for (int f = 0; f < fields.length; f++) {
				String field = fields[f];

				//Generate option HTML
				String option = "<option value=\"hit:"+field+"\">hit "+this.labels.getString(field)+"</option>";
				options.add(option);
				option = "<option value=\"wordleft:"+field+"\">"+this.labels.getString(field)+" "+this.labels.getString("left")+"</option>";
				options.add(option);
				option = "<option value=\"wordright:"+field+"\">"+this.labels.getString(field)+" "+this.labels.getString("right")+"</option>";
				options.add(option);
			}
		}
		
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			//Generate option HTML
			String option = "<option value=\"field:"+dataField.getName()+"\">"+this.labels.getString("metadataFields."+dataField.getName())+"</option>";
			options.add(option);
		}
		
		transformer.addParameter("options", StringUtils.join(options.toArray(),""));
	}
	
	@Override
	protected void logRequest() {
		this.servlet.log("QueryResponse");
	}

	@Override
	public QueryResponse duplicate() {
		return new QueryResponse();
	}

}
