package com.uvt.whitelab.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uvt.whitelab.WhiteLab;

public class ResultHandler {
	private WhiteLab servlet;
	private XslTransformer transformer = null;
	private ResourceBundle labels;
	
	public ResultHandler(WhiteLab s, ResourceBundle l) {
		servlet = s;
		labels = l;
		transformer = new XslTransformer();
	}
	
	public Query executeQuery(Query query, String trail) {
		this.servlet.log("executing: "+query.getPattern());
		String corpus = this.labels.getString("corpus");
		int view = query.getView();
		
		String html = "<p>ERROR: Could not parse XML result.</p>";
		String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><empty></empty>";
		
		if (trail == null) {
			trail = "/hits";
			if (view == 2 || view == 16 || view == 17)
				trail = "/docs";
		}
		
		if (view >= 8 && view != 9 && view != 17 && query.getGroup().length() == 0)
			html = parseResult(resp,this.labels,query);
		else {
			resp = getBlackLabResponse(corpus, trail, query.getParameters());
			query.setXml(resp);
			String counting = isStillCounting(resp);
			Integer hits = getHitsFromXML(resp);
			Integer docs = getDocsFromXML(resp);

			query.setHits(hits);
			query.setDocs(docs);
			
			if (counting.equalsIgnoreCase("false"))
				query.setStatus(2);
			else
				query.setStatus(1);
			
			if (view >= 8 && view != 9 && view != 17) {
				Integer groups = getGroupsFromXML(resp);
				query.setGroups(groups);
			}
			
			html = parseResult(resp,this.labels,query);
			if (view == 12) {
				System.out.println("PARSING CLOUD");
				JSONArray cloud = parseCloudResult(resp,this.labels,query);
				query.setCloud(cloud);
			}
		}
		query.setResult(html);
		return query;
	}

	public void loadDocument(WhitelabDocument document, Query query, String lang) {
		Map<String,Object> params = query.getParameters();

		ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
		String response = resultHandler.getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+document.getId()+"/contents", params);

		try {
			setTransformerDisplayParameters(document.getId(), query.getPattern(), lang);
			String documentStylesheet = loadStylesheet("article_folia.xsl");
			String htmlResult = transformer.transformArticle(response, documentStylesheet, query.getStart(), query.getEnd());
			document.setContent(htmlResult);
			document.setXml(response);
			document.start = query.getStart();
			document.end = query.getEnd();
			document.count();
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
		
		String meta = resultHandler.getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+document.getId(), params);
		
		try {
			setTransformerDisplayParameters(document.getId(), query.getPattern(), lang);
			String metadataStylesheet = loadStylesheet("article_metadata.xsl");
			String metaResult = transformer.transform(meta, metadataStylesheet);
			document.setMetadata(metaResult);
			document.setMetaXml(meta);
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void loadDocument(WhitelabDocument document, String lang, int start, int end) {
		Map<String,Object> params = new HashMap<String,Object>();

		ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
		String response = resultHandler.getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+document.getId()+"/contents", params);

		try {
			setTransformerDisplayParameters(document.getId(), "", lang);
			String documentStylesheet = loadStylesheet("article_folia.xsl");
			String htmlResult = transformer.transformArticle(response, documentStylesheet, start, end);
			document.setContent(htmlResult);
			document.setXml(response);
			document.start = start;
			document.end = end;
			document.count();
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
		
		String meta = resultHandler.getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+document.getId(), params);
		
		try {
			setTransformerDisplayParameters(document.getId(), "", lang);
			String metadataStylesheet = loadStylesheet("article_metadata.xsl");
			String metaResult = transformer.transform(meta, metadataStylesheet);
			document.setMetadata(metaResult);
			document.setMetaXml(meta);
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public String parseResult(String response,ResourceBundle labels,Query query) {
		try {
			String stylesheet = getStylesheet(query.getView(),false);
			setTransformerDisplayParameters(query,labels);
			return transformer.transform(response, stylesheet);
		} catch (TransformerException | IOException e) {
			e.printStackTrace();
		}
		return "<p>ERROR: Could not parse XML result.</p>";
	}
	
	public JSONArray parseCloudResult(String response,ResourceBundle labels,Query query) {
		try {
			String stylesheet = getStylesheet(query.getView(),true);
			setTransformerDisplayParameters(query,labels);
			String str = transformer.transform(response, stylesheet);
			JSONArray cloud = new JSONArray(str);
			cloud = cleanCloudLemmas(cloud);
			return cloud;
		} catch (TransformerException | IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getStylesheet(Integer view, boolean check) throws IOException {
//		this.servlet.log("STYLESHEET VIEW: "+view);
		String stylesheet = "";
		if (view == 1) {
			return loadStylesheet("perhitresults.xsl");
		} else if (view == 2) {
			return loadStylesheet("perdocresults.xsl");
		} else if (view == 4) {
			return loadStylesheet("perdocstatsresults.xsl");
		} else if (view == 8) {
			return loadStylesheet("groupperhitresults.xsl");
		} else if (view == 9) {
			return loadStylesheet("hitgroup.xsl");
		} else if (view == 10) {
			return loadStylesheet("groupperhitngramresults.xsl");
		} else if (view == 12 && !check) {
			return loadStylesheet("groupperhitstatsresults.xsl");
		} else if (view == 12 && check) {
			return loadStylesheet("wordcloudnopos.xsl");
		} else if (view == 16) {
			return loadStylesheet("groupperdocresults.xsl");
		} else if (view == 17) {
			return loadStylesheet("docgroup.xsl");
		}
		return stylesheet;
	}

	private String loadStylesheet(String name) throws IOException {
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

	private void setTransformerDisplayParameters(Query query,ResourceBundle labels) {
		int view = query.getView();
		transformer.clearParameters();

		transformer.addParameter("context_root", this.servlet.contextRoot);
		
		if (query != null) {
			transformer.addParameter("query", query.getPattern());
			try {
				transformer.addParameter("query_filter", query.getFilterUrlParameters(false));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		transformer.addParameter("query_id",query.getId());
		
		String group = query.getGroup();
		if (group.length() > 0) {
			transformer.addParameter("group_by_name", group);
			String cleanGroup = group.replaceAll("field:", "").replaceAll("hit:", "");
			transformer.addParameter("group_by_name_clean", cleanGroup);
		}
		
		String sort = query.getSort();
		if (sort.length() > 0)
			transformer.addParameter("sort_by", sort);

		int from = query.getFrom();
		if (from <= 4) {
			transformer.addParameter("query_result_url",query.getUrl("search/results", null, false, new String[] { "view", "group", "sort", "first", "number", "docpid", "start", "end" }));
			transformer.addParameter("query_document_url",query.getUrl("search/document", null, false, new String[] { "pattern", "view", "group", "sort", "first", "number", "docpid", "start", "end", "from" }));
			transformer.addParameter("query_export_url",query.getUrl("page/export", null, true, new String[] {}));
		} else if (from == 5) {
			transformer.addParameter("query_result_url",query.getUrl("explore/statistics", null, false, new String[] { "view", "group", "sort", "first", "number", "docpid", "start", "end" }));
			transformer.addParameter("query_document_url",query.getUrl("explore/document", null, false, new String[] { "pattern", "view", "group", "sort", "first", "number", "docpid", "start", "end", "from" }));
			transformer.addParameter("query_export_url",query.getUrl("page/export", "&from=5", true, new String[] {}));
		} else if (from == 6) {
			transformer.addParameter("query_result_url",query.getUrl("explore/ngrams", null, false, new String[] { "view", "group", "sort", "first", "number", "docpid", "start", "end" }));
			transformer.addParameter("query_document_url",query.getUrl("explore/document", null, false, new String[] { "pattern", "view", "group", "sort", "first", "number", "docpid", "start", "end", "from" }));
			transformer.addParameter("query_export_url",query.getUrl("page/export", "&from=6", true, new String[] {}));
		}
		
		transformer.addParameter("per_hit",labels.getString("result.per_hit"));
		transformer.addParameter("per_doc",labels.getString("result.per_doc"));
		transformer.addParameter("grouped_per_hit",labels.getString("result.grouped_per_hit"));
		transformer.addParameter("grouped_per_doc",labels.getString("result.grouped_per_doc"));
		transformer.addParameter("grouped_header",labels.getString("result.grouped_header"));
		transformer.addParameter("context_left",labels.getString("context") + " " + labels.getString("left"));
		transformer.addParameter("context_right",labels.getString("context") + " " + labels.getString("right"));
		transformer.addParameter("group",labels.getString("result.group"));
		transformer.addParameter("hits",labels.getString("result.hits"));
		transformer.addParameter("documents",labels.getString("result.documents"));
		transformer.addParameter("detailed_conc",labels.getString("result.detailed_concordances"));
		transformer.addParameter("load_more",labels.getString("result.loadmore"));
		transformer.addParameter("word",labels.getString("word"));
		transformer.addParameter("lemma",labels.getString("lemma"));
		transformer.addParameter("pos",labels.getString("pos"));
		transformer.addParameter("result_export",labels.getString("result.export"));
		transformer.addParameter("result_pagination_show",labels.getString("result.pagination.show"));
		transformer.addParameter("result_per_page",labels.getString("result.per_page"));
		transformer.addParameter("result_titles",labels.getString("result.titles"));
		transformer.addParameter("result_page",labels.getString("result.page"));
		transformer.addParameter("result_go",labels.getString("result.go"));
		transformer.addParameter("result_of",labels.getString("result.of"));
		transformer.addParameter("result_by",labels.getString("result.by"));
		transformer.addParameter("collection_name", labels.getString("document.meta.field.collection"));
		transformer.addParameter("title_name", labels.getString("document.meta.field.title"));
		transformer.addParameter("author_name", labels.getString("document.meta.field.author"));
		transformer.addParameter("date_name", labels.getString("document.meta.field.date"));
		transformer.addParameter("pos_name", labels.getString("result.pos_name"));
		transformer.addParameter("document_id", labels.getString("document.meta.docid"));
		transformer.addParameter("document_title", labels.getString("document.meta.doctitle"));
		transformer.addParameter("document_author", labels.getString("document.meta.author"));
		transformer.addParameter("collection", labels.getString("document.meta.collection"));
		transformer.addParameter("token_count", labels.getString("document.meta.token_count"));
		transformer.addParameter("view_doc", labels.getString("document.view"));
		
		if (view == 8)
			addGroupOptions(true,labels);
		else if (view == 16)
			addGroupOptions(false,labels);
	}
	
	private void setTransformerDisplayParameters(String docPid, String pattern, String lang) {
		transformer.clearParameters();
//		String query = this.getParameter("query", "");
		transformer.addParameter("query", pattern);
		if (pattern.length() == 0)
			transformer.addParameter("whitelab_page", "explore");
		else
			transformer.addParameter("whitelab_page", "search");
		transformer.addParameter("doc_id", docPid);
		transformer.addParameter("lang", lang);
		
		transformer.addParameter("title_name", this.labels.getString("document.meta.field.title"));
		transformer.addParameter("author_name", this.labels.getString("document.meta.field.author"));
		transformer.addParameter("description_name", this.labels.getString("document.meta.field.description"));
		transformer.addParameter("document_id_name", this.labels.getString("document.meta.field.docid"));
		transformer.addParameter("texttype_name", this.labels.getString("document.meta.field.texttype"));
		transformer.addParameter("collection_name", this.labels.getString("document.meta.field.collection"));
		transformer.addParameter("license_code_name", this.labels.getString("document.meta.field.licensecode"));
		transformer.addParameter("license_date_name", this.labels.getString("document.meta.field.licensedate"));
		transformer.addParameter("country_name", this.labels.getString("document.meta.field.country"));
		transformer.addParameter("continent_name", this.labels.getString("document.meta.field.continent"));
		transformer.addParameter("language_name", this.labels.getString("document.meta.field.language"));
		
		transformer.addParameter("by",this.labels.getString("result.by"));
		transformer.addParameter("document_id", this.labels.getString("document.meta.docid"));
		transformer.addParameter("texttype", this.labels.getString("document.meta.texttype"));
		transformer.addParameter("collection", this.labels.getString("document.meta.collection"));
		transformer.addParameter("license_code", this.labels.getString("document.meta.licensecode"));
		transformer.addParameter("license_date", this.labels.getString("document.meta.licensedate"));
		transformer.addParameter("country", this.labels.getString("document.meta.country"));
		transformer.addParameter("continent", this.labels.getString("document.meta.continent"));
		transformer.addParameter("language", this.labels.getString("document.meta.language"));
		
	}

	private void addGroupOptions(boolean addHitFields,ResourceBundle labels) {
		List<String> options = new ArrayList<String>();
		
		if (addHitFields) {
			String[] fields = new String[]{"word","lemma","pos"};
			for (int f = 0; f < fields.length; f++) {
				String field = fields[f];

				//Generate option HTML
				String option = "<option value=\"hit:"+field+"\">hit "+labels.getString(field)+"</option>";
				options.add(option);
				option = "<option value=\"wordleft:"+field+"\">"+labels.getString(field)+" "+labels.getString("left")+"</option>";
				options.add(option);
				option = "<option value=\"wordright:"+field+"\">"+labels.getString(field)+" "+labels.getString("right")+"</option>";
				options.add(option);
			}
		}
		
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			//Generate option HTML
			String option = "<option value=\"field:"+dataField.getName()+"\">"+labels.getString("metadataFields."+dataField.getName())+"</option>";
			options.add(option);
		}
		
		transformer.addParameter("options", StringUtils.join(options.toArray(),""));
	}

	public String isStillCounting(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<stillCounting>(.+?)</stillCounting>");
			final Matcher matcher = pattern.matcher(response);
			
			if (matcher.find())
				return matcher.group(1);
		}
		return "false";
	}

	public Integer getGroupsFromXML(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<numberOfGroups>(.+?)</numberOfGroups>");
			final Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}
		return -1;
	}

	public Integer getDocsFromXML(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<numberOfDocs>(.+?)</numberOfDocs>");
			final Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}
		return -1;
	}

	public Integer getHitsFromXML(String response) {
		if (response != null) {
			final Pattern pattern = Pattern.compile("<numberOfHits>(.+?)</numberOfHits>");
			final Matcher matcher = pattern.matcher(response);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}
		return -1;
	}

	public JSONArray cleanCloudLemmas(JSONArray cloud) {
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

	public String getBlackLabResponse(String corpus, String trail, Map<String,Object> parameters) {
		String url = getBlackLabURL(corpus,trail,parameters);
		return getBlackLabResponse(url);
	}

	public String getBlackLabResponse(String url) {
		QueryServiceHandler webservice = new QueryServiceHandler(url, 1);
		try {
			String response = webservice.makeRequest(new HashMap<String, String[]>());
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getBlackLabURL(String corpus, String trail, Map<String,Object> params) {
		String url = this.labels.getString("blsUrlInternal")+ "/" + corpus + trail;
//		this.lastUrl = url;
		String parameters = getParameterStringExcept(new String[]{}, params);
		
		if (parameters.length() > 0) {
			url = url + "?" + parameters;
		}
		return url;
	}

	protected String getParameterStringExcept(String[] except, Map<String,Object> params) {
		String parameters = "";
		
		for (String key : params.keySet()) {
			if (!Arrays.asList(except).contains(key)) {
				if (parameters.length() > 0)
					parameters = parameters + "&" + key + "=" + params.get(key);
				else
					parameters = key + "=" +params.get(key);
			}
		}
		
		parameters = parameters.replaceAll(" ", "%20");
		return parameters;
	}

	public String getNextDocumentPid(Query query) {
		String response = query.getXml();
		String currentDocPid = query.getDocPid();
		boolean n = false;
		if (currentDocPid.length() == 0)
			n = true;
		if (response != null) {
			final Pattern pattern = Pattern.compile("<docPid>(.+?)</docPid>");
			final Matcher matcher = pattern.matcher(response);
			while (matcher.find()) {
				String docPid = matcher.group(1);
				if (docPid.length() > 0 && n)
					return docPid;
				if (docPid.length() > 0 && docPid.equals(currentDocPid))
					n = true;
			}
		}
		return null;
	}

}
