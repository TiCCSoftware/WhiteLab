package com.uvt.whitelab.response.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.Query;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.SessionManager;

public class ResultResponse extends BaseResponse {
	
	public ResultResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		
		int view = this.getParameter("view", 1);
		String delete = this.getParameter("delete", "false");
		
		if (query == null && delete.equals("true")) {
			String endTour = this.getParameter("endtour", "false");
			if (endTour.equals("true")) {
				SessionManager.deleteTourQuery(session);
				this.getContext().remove("tourQuery");
				try {
					response.sendRedirect("/whitelab/search/simple");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		updateQueryCount();
		
		if (query != null) {
			view = query.getView();
			if (delete.equals("true")) {
				String endTour = this.getParameter("endtour", "false");
				if (endTour.equals("true")) {
					SessionManager.deleteTourQuery(session);
					this.getContext().remove("tourQuery");
					try {
						response.sendRedirect("/whitelab/search/simple");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					SessionManager.deleteQuery(session, query.getId());
					updateQueryCount();
				}
				query = null;
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
			
			if (query != null && query.getStatus() < 2) {
				ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
				String batch = this.getParameter("batch", "false");
				if (batch.equals("true")) {
					String[] patterns = query.getPattern().split(";");
					query.setPattern(patterns[0]);
					for (int i = 1; i < patterns.length; i++) {
						Map<String,Object> replace = new HashMap<String,Object>();
						replace.put("pattern", patterns[i]);
						Query q = new Query(query,replace);
						q = resultHandler.executeQuery(q,null);
						SessionManager.addQuery(session, q);
					}
					query = resultHandler.executeQuery(query,null);
				} else {
					query = resultHandler.executeQuery(query,null);
				}
			}
		}
		
		if (view == 9 || view == 17) {
			Map<String,Object> output = new HashMap<String,Object>();
			output.put("html", query.getResult());
			sendResponse(output);
		} else {
			this.servlet.log("QUERY COUNT: "+queryCount);
			if (query == null)
				this.servlet.log("QUERY IS NULL");
			if (query == null && queryCount > 0)
				query = SessionManager.setCurrentQuery(session, null);
			else if (query == null && queryCount == 0) {
				try {
					response.sendRedirect("/whitelab/search/simple");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (query != null) {
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
				this.getContext().put("query", query);
				this.getContext().put("queries", queries);
				this.getContext().put("isStillCounting", SessionManager.isStillCounting(session));
				this.getContext().put("requestUrl", query.getUrl("search/results", null, false, new String[]{}));
				int ql = this.getParameter("ql", 0);
				this.getContext().put("ql", ql);
				this.getContext().put("qlBefore", ql - 5);
				this.getContext().put("qlStart", ql + 1);
				this.getContext().put("qlEnd", ql + 5);
			}
			
			this.displayHtmlTemplate(this.servlet.getTemplate("search/results"));
		}
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ResultResponse");
	}

	@Override
	public ResultResponse duplicate() {
		return new ResultResponse("search");
	}

}
