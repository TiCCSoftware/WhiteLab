package com.uvt.whitelab.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
//	private String query_id = null;
//	private String query = null;
//	private String group = null;
//	private String sort = null;
	private Query query;
	
//	public ResultHandler(WhiteLab s, String qid, String q) {
//		servlet = s;
//		query_id = qid;
//		query = q;
//		transformer = new XslTransformer();
//	}
	
	public ResultHandler(WhiteLab s, Query q) {
		servlet = s;
		query = q;
		transformer = new XslTransformer();
	}

	public String parseResult(String response,ResourceBundle labels,Integer view) {
		try {
			String stylesheet = getStylesheet(view,false);
			setTransformerDisplayParameters(view,labels);
			return transformer.transform(response, stylesheet);
		} catch (TransformerException | IOException e) {
			e.printStackTrace();
		}
		return "<p>ERROR: Could not parse XML result.</p>";
	}
	
	public JSONArray parseCloudResult(String response,ResourceBundle labels,Integer view) {
		try {
			String stylesheet = getStylesheet(view,true);
			setTransformerDisplayParameters(view,labels);
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

	private void setTransformerDisplayParameters(Integer view,ResourceBundle labels) {
		transformer.clearParameters();
		
		if (query != null) {
			transformer.addParameter("query", query.getPattern());
		}

		transformer.addParameter("query_id",query.getId());
		
		String group = query.getGroup();
		if (group.length() > 0) {
			transformer.addParameter("group_by_name", group);
			transformer.addParameter("group_by_name_clean", group.replaceAll("field:", ""));
		}
		
		String sort = query.getSort();
		if (sort.length() > 0)
			transformer.addParameter("sort_by", sort);

		transformer.addParameter("query_result_url",query.getUrl("search/results", null, false, new String[] { "view", "group", "sort", "first", "number", "docpid", "start", "end" }));
		transformer.addParameter("query_document_url",query.getUrl("search/document", null, false, new String[] { "pattern", "view", "group", "sort", "first", "number", "docpid", "start", "end", "from" }));
		transformer.addParameter("query_export_url",query.getUrl("page/export", null, true, new String[] {}));
		
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

}
