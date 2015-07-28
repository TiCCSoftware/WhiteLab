package com.uvt.whitelab.response.explore;

import org.json.JSONArray;
import org.json.JSONException;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.Query;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.SessionManager;
import com.uvt.whitelab.util.WhitelabDocument;

public class StatisticsResponse extends BaseResponse {
	private int view = -1;
	

	public StatisticsResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		if (query == null)
			loadMetaDataComponents(false);
		else {
			String tab = this.getParameter("tab", "freqlist");
			ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
			
			if (tab.equals("growth")) {
				if (query.getDocPid().length() == 0) {
					query.resetStatus();
					query = resultHandler.executeQuery(query,"/docs");
				}
				String docPid = resultHandler.getNextDocumentPid(query);
				query.setDocPid(docPid);
				WhitelabDocument document = new WhitelabDocument(docPid);
				document.setLemmas(query.getLemmas());
				document.setTypes(query.getTypes());
				resultHandler.loadDocument(document, query, this.lang);
				JSONArray qdata = query.getGrowthData();
				if (qdata == null || qdata.length() == 0) {
					qdata = document.getGrowthData(this.lang, "json", false, 0,0,0);
				} else {
					int x = qdata.length() - 1;
					JSONArray lastRow;
					try {
						lastRow = qdata.getJSONArray(x);
						int y1 = lastRow.getInt(1);
						int y2 = lastRow.getInt(3);
						JSONArray data = document.getGrowthData(this.lang, "json", true, x,y1,y2);
						qdata = concatArray(qdata,data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				query.setGrowthData(qdata);
				query.setLemmas(document.getLemmas());
				query.setTypes(document.getTypes());
				
				query.setDocument(document);
				this.getContext().put("data", query.getGrowthData().toString());
			} else {
				query.resetStatus();
				if (query.getView() == 4)
					query = resultHandler.executeQuery(query,"/docs");
				else
					query = resultHandler.executeQuery(query,null);
			}
			
			loadMetaDataComponents(true);
			this.getContext().put("statstab", tab);
			this.getContext().put("query", query);
			this.getContext().put("isStillCounting", SessionManager.isStillCounting(session));
			this.getContext().put("requestUrl", query.getUrl("explore/statistics", "&from=5", true, new String[]{}));
		}

		this.getContext().put("showMetaOptions", "no");
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/statistics"));
	}
	
	private JSONArray concatArray(JSONArray arr1, JSONArray arr2) throws JSONException {
	    JSONArray result = new JSONArray();
	    for (int i = 0; i < arr1.length(); i++) {
	        result.put(arr1.get(i));
	    }
	    for (int i = 0; i < arr2.length(); i++) {
	        result.put(arr2.get(i));
	    }
	    return result;
	}

	@Override
	protected void logRequest() {
		this.servlet.log("StatisticsResponse");
	}

	@Override
	public StatisticsResponse duplicate() {
		return new StatisticsResponse("explore");
	}
	
	@Override
	protected void setQueryDefaults() {
		queryDefaults.put("query", "[]");
		queryDefaults.put("from", 5);
		String tab = this.getParameter("tab","freqlist");
		if (tab.equals("freqlist") || tab.equals("wordcloud")) {
			queryDefaults.put("view", 12);
			queryDefaults.put("group", "hit:word");
		} else if (tab.equals("doclist") || tab.equals("growth")) {
			queryDefaults.put("view", 4);
			queryDefaults.put("group", "");
		}
	}

	@Override
	protected void initQuery() {
		query = null;
		if (this.request.getQueryString() != null && this.request.getQueryString().length() > 0) {
			view = this.getParameter("view", (int) this.getQueryDefault("view", 12));
			String id = this.getParameter("id", "");
			String patt = this.getParameter("query", (String) this.getQueryDefault("query", "")).replaceAll("&", "%26");
			String within = this.getParameter("within", "");
			int from = this.getParameter("from", (int) this.getQueryDefault("from", 5));
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
//				this.servlet.log("NEW QUERY");
				query = new Query(this);
				query.setFrom(5);
				id = query.getId();
				if (view != 9 && view != 17)
					SessionManager.addQuery(session, query);
			}
			
			if (query != null && !editQuery && !deleteQuery && updateQuery) {
				query = query.updateQuery(this);
				query.setFrom(5);
				if (!id.equals(query.getId()) && view != 9 && view != 17)
					SessionManager.addQuery(session, query);
			}
		}
	}

}
