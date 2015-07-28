package com.uvt.whitelab.response.explore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.FieldDescriptor;
import com.uvt.whitelab.util.Query;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.SessionManager;

public class NgramsResponse extends BaseResponse {
	
	public NgramsResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		loadPosValues();
		int nsize = this.getSizeFromQuery(this.getParameter("query", "").replaceAll("%5B", "["));
		if (query == null)
			loadMetaDataComponents(false);
		else {
			ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
			
			query.resetStatus();
			query = resultHandler.executeQuery(query,"/hits");
			
			loadMetaDataComponents(true);
			this.getContext().put("query", query);
			this.getContext().put("isStillCounting", SessionManager.isStillCounting(session));
			this.getContext().put("requestUrl", query.getUrl("explore/ngrams", "&from=6", true, new String[]{}));
		}

		this.getContext().put("size", nsize);
		this.getContext().put("showMetaOptions", "no");
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/ngrams"));
	}
	
	private int getSizeFromQuery(String patt) {
		return StringUtils.countMatches(patt, "[");
	}

	private void loadPosValues() {
		LinkedList<FieldDescriptor> fields = this.servlet.getSearchFields();
		
		List<String> options = new ArrayList<String>();
		options.add("<option value=\"\">&lt;any&gt;</option>");
		
		for (FieldDescriptor field : fields) {
			if (field.getSearchField().equals("pos")) {
				for (int i = 1; i <= 12; i++) {
					options.add("<option value=\""+labels.getString("pos."+i+".value")+"\">"+labels.getString("pos."+i+".name")+"</option>");
				}
			}
		}
		
		this.getContext().put("posValues", "<select class=\"input pos\">"+StringUtils.join(options.toArray(),"")+"</select>");
	}

	@Override
	protected void logRequest() {
		this.servlet.log("NgramsResponse");
	}

	@Override
	public NgramsResponse duplicate() {
		return new NgramsResponse("explore");
	}
	
	@Override
	protected void setQueryDefaults() {
		queryDefaults.put("query", "[]");
		queryDefaults.put("view", 10);
		queryDefaults.put("from", 6);
		queryDefaults.put("group", "hit:word");
	}

	@Override
	protected void initQuery() {
		query = null;
		if (this.request.getQueryString() != null && this.request.getQueryString().length() > 0) {
			String id = this.getParameter("id", "");
			String patt = this.getParameter("query", (String) this.getQueryDefault("query", "")).replaceAll("&", "%26");
			String within = this.getParameter("within", "");
			int view = this.getParameter("view", (int) this.getQueryDefault("view", 10));
			int from = this.getParameter("from", (int) this.getQueryDefault("from", 6));
			boolean editQuery = Boolean.parseBoolean(this.getParameter("edit", "false"));
			boolean deleteQuery = Boolean.parseBoolean(this.getParameter("delete", "false"));
			boolean updateQuery = true;
			
	//		this.servlet.log("QUERY VIEW: "+view);
			
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
				query.setFrom(6);
				id = query.getId();
				if (view != 9 && view != 17)
					SessionManager.addQuery(session, query);
			}
			
			if (query != null && !editQuery && !deleteQuery && updateQuery) {
				query = query.updateQuery(this);
				query.setFrom(6);
				if (!id.equals(query.getId()) && view != 9 && view != 17)
					SessionManager.addQuery(session, query);
			}
		}
	}

}
