package com.uvt.whitelab.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

public class SessionManager {
	
	public static String addQuery(HttpSession session, Map<String,Object> params) {
		// TODO add params under separate key and extract query pattern + within phrase
		String queryId = UUID.randomUUID().toString();
		List<Map<String,Object>> newQueries = new ArrayList<Map<String,Object>>();
		params.put("id", queryId);
		params.put("current", true);
		newQueries.add(params);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
		if (queries != null && queries.size() > 0) {
			for (int i = 0; i < queries.size(); i++) {
				Map<String,Object> query = queries.get(i);
				query.put("current", false);
				newQueries.add(query);
			}
		}
		queries = newQueries;
		if (queries.size() > 25)
			queries = queries.subList(queries.size() - 25, queries.size() - 1);
		session.setAttribute("queries", queries);
		return queryId;
	}
	
	public static void addQuery(HttpSession session, Query query) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		queries.add(0, query);
		if (queries.size() > 25)
			queries = queries.subList(queries.size() - 25, queries.size() - 1);
		session.setAttribute("queries", queries);
	}
	
//	public static Map<String,Object> getQuery(HttpSession session, String queryId) {
////		@SuppressWarnings("unchecked")
////		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
////		for (int i = 0; i < queries.size(); i++) {
////			Map<String,Object> query = queries.get(i);
////			if (query.get("id").equals(queryId))
////				return query;
////		}
//		return null;
//	}

	public static Query getQuery(HttpSession session, String queryId) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		for (int i = 0; i < queries.size(); i++) {
			Query query = queries.get(i);
			if (query.getId().equals(queryId))
				return query;
		}
		return null;
	}
	
	public static void updateQuery(HttpSession session, String queryId, Map<String,Object> params) {
		if (!params.containsKey("id"))
			params.put("id", queryId);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
		for (int i = 0; i < queries.size(); i++) {
			Map<String,Object> query = queries.get(i);
			if (query.get("id").equals(queryId))
				query = params;
		}
		session.setAttribute("queries", queries);
	}
	
	public static void updateQueryParam(HttpSession session, String queryId, String key, Object value) {
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
		for (int i = 0; i < queries.size(); i++) {
			Map<String,Object> query = queries.get(i);
			if (query.get("id").equals(queryId))
				query.put(key, value);
		}
		session.setAttribute("queries", queries);
	}

//	public static void setCurrentQuery(HttpSession session, String queryId) {
//		@SuppressWarnings("unchecked")
//		List<Map<String,Object>> queries = (List<Map<String,Object>>) session.getAttribute("queries");
//		for (int i = 0; i < queries.size(); i++) {
//			Map<String,Object> query = queries.get(i);
//			if (query.get("id").equals(queryId))
//				query.put("current", true);
//			else
//				query.put("current", false);
//		}
//		session.setAttribute("queries", queries);
//	}

	public static void setCurrentQuery(HttpSession session, String queryId) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		for (int i = 0; i < queries.size(); i++) {
			Query query = queries.get(i);
			if (query.getId().equals(queryId))
				query.setCurrent(true);
			else
				query.setCurrent(false);
		}
		session.setAttribute("queries", queries);
	}
	
	public static String getQueryPattern(Map<String,Object> query) {
		if (query.containsKey("patt")) {
			String q = (String) query.get("patt");
			return q.split(" within")[0];
		}
		return "";
	}
	
	public static String getQueryWithin(Map<String,Object> query) {
		if (query.containsKey("patt")) {
			String q = (String) query.get("patt");
			String w = q.split(" within")[1];
			if (w != null && w.length() > 0)
				return "within"+w;
		}
		return "";
	}

}
