package com.uvt.whitelab.response.search;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.SessionManager;

public class ResultResponse extends BaseResponse {
	private ResultHandler resultHandler = null;

	@Override
	protected void completeRequest() {
		// TODO check if this supports batch search
		
		this.getContext().put("startTime", this.startTime);
//		try {
//			this.getContext().put("memUsageStart", this.servlet.getCurrentMemUsage());
//		} catch (MalformedObjectNameException | AttributeNotFoundException
//				| InstanceNotFoundException | MBeanException
//				| ReflectionException e1) {
//			e1.printStackTrace();
//		}
		
		if (query == null && queryCount > 0)
			query = SessionManager.setCurrentQuery(session, null);
		
		if (query != null) {
			String delete = this.getParameter("delete", "false");
			if (delete.equals("true")) {
				SessionManager.deleteQuery(session, query.getId());
				updateQueryCount();
				if (queryCount == 0) {
					try {
						response.sendRedirect("/whitelab/search/simple");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					query = SessionManager.setCurrentQuery(session, null);
				}
			}
			
			if (query.getStatus() == 0) {
				String corpus = this.labels.getString("corpus");
				Integer view = query.getView();
				resultHandler = new ResultHandler(this.servlet, this.query);
				String html = "<p>ERROR: Could not parse XML result.</p>";
				String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><empty></empty>";
				
				String trail = "/hits";
				if (view == 2 || view == 16)
					trail = "/docs";
				
				if (view >= 8 && query.getGroup().length() == 0)
					html = resultHandler.parseResult(resp,this.labels,view);
				else {
					resp = getBlackLabResponse(corpus, trail, query.getParameters());
					String counting = resultHandler.isStillCounting(resp);
					Integer hits = resultHandler.getHitsFromXML(resp);
					Integer docs = resultHandler.getDocsFromXML(resp);
		
					query.setHits(hits);
					query.setDocs(docs);
					if (counting.equalsIgnoreCase("false"))
						query.setStatus(2);
					else
						query.setStatus(1);
		
					this.getContext().put("hits", hits);
					this.getContext().put("docs", docs);
					
					if (view >= 8) {
						Integer groups = resultHandler.getGroupsFromXML(resp);
						this.getContext().put("groups", groups);
					}
					
					html = resultHandler.parseResult(resp,this.labels,view);
					if (view == 12 && query.getFirst() == 0) {
						JSONArray cloud = resultHandler.parseCloudResult(resp,this.labels,view);
						this.getContext().put("cloud", cloud);
					}
				}
		//		TODO add reload mechanism to stylesheet (directly to BLS)
				query.setResult(html);
			}
			this.getContext().put("query", query);
		}
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
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

	@Override
	protected void logRequest() {
		this.servlet.log("ResultResponse");
	}

	@Override
	public ResultResponse duplicate() {
		return new ResultResponse();
	}

}
