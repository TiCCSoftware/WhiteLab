package com.uvt.whitelab.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

public class SessionManager {
	
	public static void addQuery(HttpSession session, Query query) {
		if (query.getFrom() == 0) {
			session.setAttribute("tourQuery", query);
		} else if (query.getFrom() <= 4) {
			@SuppressWarnings("unchecked")
			List<Query> queries = (List<Query>) session.getAttribute("queries");
			if (queries == null)
				queries = new ArrayList<Query>();
			queries.add(0, query);
			if (queries.size() > 25)
				queries = queries.subList(queries.size() - 25, queries.size() - 1);
			session.setAttribute("queries", queries);
		} else if (query.getFrom() == 5) {
			session.setAttribute("statsQuery", query);
		} else if (query.getFrom() == 6) {
			session.setAttribute("ngramsQuery", query);
		}
	}
	
	public static void deleteQuery(HttpSession session, String queryId) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		if (queries != null) {
			List<Query> keep = new ArrayList<Query>();
			for (int i = 0; i < queries.size(); i++) {
				Query query = queries.get(i);
				String id = query.getId();
				if (!id.equals(queryId))
					keep.add(query);
			}
			session.setAttribute("queries", keep);
		}
	}

	public static Query getQuery(HttpSession session, String queryId, int from) {
		if (from == 0 && SessionManager.isOnTour(session)) { // tour
			return (Query) session.getAttribute("tourQuery");
		} else if (from <= 4) { // search
			@SuppressWarnings("unchecked")
			List<Query> queries = (List<Query>) session.getAttribute("queries");
			if (queries != null) {
				for (int i = 0; i < queries.size(); i++) {
					Query query = queries.get(i);
					if (query.getId().equals(queryId))
						return query;
				}
			}
		} else if (from == 5) { // explore/statistics
			Query statsQuery = (Query) session.getAttribute("statsQuery");
			if (statsQuery != null && statsQuery.getId().equals(queryId))
				return statsQuery;
			else
				session.removeAttribute("statsQuery");
		} else if (from == 6) { // explore/ngrams
			Query ngramsQuery = (Query) session.getAttribute("ngramsQuery");
			if (ngramsQuery != null && ngramsQuery.getId().equals(queryId))
				return ngramsQuery;
			else
				session.removeAttribute("ngramsQuery");
		}
		return null;
	}

	public static Query setCurrentQuery(HttpSession session, String queryId) {
		Query current = null;
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		if (queries != null) {
			for (int i = 0; i < queries.size(); i++) {
				Query query = queries.get(i);
				if ((queryId == null && i == 0) || query.getId().equals(queryId)) {
					query.setCurrent(true);
					current = query;
				} else
					query.setCurrent(false);
			}
		}
		session.setAttribute("queries", queries);
		return current;
	}
	
	public static Query getCurrentQuery(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		if (queries != null) {
			for (int i = 0; i < queries.size(); i++) {
				Query query = queries.get(i);
				if (query.isCurrent())
					return query;
			}
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
	
	public static boolean isStillCounting(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Query> queries = (List<Query>) session.getAttribute("queries");
		if (queries != null) {
			for (int i = 0; i < queries.size(); i++) {
				Query query = queries.get(i);
				if (query.getStatusString().equals("COUNTING"))
					return true;
			}
		}
		return false;
	}

	public static void deleteTourQuery(HttpSession session) {
		session.removeAttribute("tourQuery");
	}
	
	public static boolean isOnTour(HttpSession session) {
		if (session.getAttribute("onTour") != null)
			return (boolean) session.getAttribute("onTour");
		return false;
	}
	
	public static int getTourStep(HttpSession session) {
		if (SessionManager.isOnTour(session) && session.getAttribute("tourStep") != null)
			return (int) session.getAttribute("tourStep");
		return 0;
	}

	public static void setTour(HttpSession session, int tour) {
		if (SessionManager.isOnTour(session) && tour == 0) {
			System.out.println("*** Info, closing site tour");
			session.setAttribute("onTour", false);
			session.removeAttribute("tourStep");
			session.removeAttribute("tourQuery");
		} else if (!SessionManager.isOnTour(session) && tour > 0) {
			session.setAttribute("onTour", true);
			session.setAttribute("tourStep", 0);
		}
		if (SessionManager.isOnTour(session) && tour != (int) session.getAttribute("tourStep")) {
			session.setAttribute("tourStep", tour);
		}
	}

}
