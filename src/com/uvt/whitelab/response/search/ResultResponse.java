package com.uvt.whitelab.response.search;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.Query;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.SessionManager;

public class ResultResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		
		this.getContext().put("startTime", this.startTime);
//		try {
//			this.getContext().put("memUsageStart", this.servlet.getCurrentMemUsage());
//		} catch (MalformedObjectNameException | AttributeNotFoundException
//				| InstanceNotFoundException | MBeanException
//				| ReflectionException e1) {
//			e1.printStackTrace();
//		}
		
		int view = 1;
		
		if (query != null) {
			view = query.getView();
			String delete = this.getParameter("delete", "false");
			if (delete.equals("true")) {
				SessionManager.deleteQuery(session, query.getId());
				updateQueryCount();
				query = null;
				if (queryCount == 0) {
					try {
						response.sendRedirect("/whitelab/search/simple");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (query.getStatus() == 0) {
				String batch = this.getParameter("batch", "false");
				if (batch.equals("true")) {
					String[] patterns = query.getPattern().split(";");
					query.setPattern(patterns[0]);
					for (int i = 1; i < patterns.length; i++) {
						Map<String,Object> replace = new HashMap<String,Object>();
						replace.put("pattern", patterns[i]);
						Query q = new Query(query,replace);
						q = executeQuery(q);
						SessionManager.addQuery(session, q);
					}
					query = executeQuery(query);
				} else {
					query = executeQuery(query);
				}
			}
		}
		
		if (view == 9 || view == 17) {
			Map<String,Object> output = new HashMap<String,Object>();
			output.put("html", query.getResult());
			sendResponse(output);
		}
		
		if (query == null && queryCount > 0)
			query = SessionManager.setCurrentQuery(session, null);
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
		this.getContext().put("query", query);
		this.getContext().put("queries", queries);
		this.getContext().put("isStillCounting", SessionManager.isStillCounting(session));
		this.getContext().put("requestUrl", this.getRequestURL(false));
		int ql = this.getParameter("ql", 0);
		this.getContext().put("ql", ql);
		this.getContext().put("qlBefore", ql - 5);
		this.getContext().put("qlStart", ql + 1);
		this.getContext().put("qlEnd", ql + 5);
		
//		try {
//			this.getContext().put("memUsageEnd", this.servlet.getCurrentMemUsage());
//		} catch (MalformedObjectNameException | AttributeNotFoundException
//				| InstanceNotFoundException | MBeanException
//				| ReflectionException e1) {
//			e1.printStackTrace();
//		}
		this.getContext().put("endTime", new Date().getTime());
		
		this.displayHtmlTemplate(this.servlet.getTemplate("search/results"));
	}
	
	private Query executeQuery(Query q) {
		this.servlet.log("executing: "+q.getPattern());
		String corpus = this.labels.getString("corpus");
		int view = q.getView();
		ResultHandler resultHandler = new ResultHandler(this.servlet, q);
		String html = "<p>ERROR: Could not parse XML result.</p>";
		String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><empty></empty>";
		
		String trail = "/hits";
		if (view == 2 || view == 16 || view == 17)
			trail = "/docs";
		if (view == 9)
			q.setView(1);
		else if (view == 17)
			q.setView(2);
		
		if (view >= 8 && view != 9 && view != 17 && q.getGroup().length() == 0)
			html = resultHandler.parseResult(resp,this.labels,view);
		else {
			resp = getBlackLabResponse(corpus, trail, q.getParameters());
			String counting = resultHandler.isStillCounting(resp);
			Integer hits = resultHandler.getHitsFromXML(resp);
			Integer docs = resultHandler.getDocsFromXML(resp);

			q.setHits(hits);
			q.setDocs(docs);
			if (counting.equalsIgnoreCase("false"))
				q.setStatus(2);
			else
				q.setStatus(1);
			
			if (view >= 8 && view != 9 && view != 17) {
				Integer groups = resultHandler.getGroupsFromXML(resp);
				q.setGroups(groups);
			}
			
			html = resultHandler.parseResult(resp,this.labels,view);
			if (view == 12 && q.getFirst() == 0) {
				JSONArray cloud = resultHandler.parseCloudResult(resp,this.labels,view);
				q.setCloud(cloud);
			}
		}
		q.setResult(html);
		return q;
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ResultResponse");
	}

	@Override
	public ResultResponse duplicate() {
		return new ResultResponse();
	}

}
