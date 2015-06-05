package com.uvt.whitelab.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

public class SessionManager {
	
	public static void addQuery(HttpSession session, Query query) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		if (queries == null)
			queries = new ArrayList<Query>();
		queries.add(0, query);
		if (queries.size() > 25)
			queries = queries.subList(queries.size() - 25, queries.size() - 1);
		session.setAttribute("queries", queries);
	}
	
	public static void deleteQuery(HttpSession session, String queryId) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		List<Query> keep = new ArrayList<Query>();
		for (int i = 0; i < queries.size(); i++) {
			Query query = queries.get(i);
			String id = query.getId();
			if (!id.equals(queryId))
				keep.add(query);
		}
		session.setAttribute("queries", keep);
	}

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

	public static Query setCurrentQuery(HttpSession session, String queryId) {
		Query current = null;
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		for (int i = 0; i < queries.size(); i++) {
			Query query = queries.get(i);
			if ((queryId == null && i == 0) || query.getId().equals(queryId)) {
				query.setCurrent(true);
				current = query;
			} else
				query.setCurrent(false);
		}
		session.setAttribute("queries", queries);
		return current;
	}
	
	public static Query getCurrentQuery(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		for (int i = 0; i < queries.size(); i++) {
			Query query = queries.get(i);
			if (query.isCurrent())
				return query;
		}
		return null;
	}
	
	public static int getQueryCount(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		if (queries != null)
			return queries.size();
		return 0;
	}

}
