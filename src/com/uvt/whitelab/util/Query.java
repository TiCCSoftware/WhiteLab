package com.uvt.whitelab.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;

import com.uvt.whitelab.BaseResponse;

public class Query {

	private String id;
	private String pattern = "";
	private String within = "";
	private int view = 1;
	private int from = 0;
	private String group = "";
	private String sort = "";
	private String filter = "";
	private int groups = 0;
	private int hits = 0;
	private int docs = 0;
	private int status = 0;
	private boolean current = false;
	private int wordsAroundHit = -1;
	private int first = 0;
	private int number = 50;
	private String docPid = "";
	private WhitelabDocument document = null;
	private int start = -1;
	private int end = -1;
	private Map<String,Map<String,List<String>>> filters;
	private JSONArray cloud;

	private String result = "";
	
	public Query(Query q, Map<String,Object> replace) {
		id = UUID.randomUUID().toString();
		if (replace.containsKey("pattern"))
			pattern = (String) replace.get("pattern");
		else
			pattern = q.getPattern();
		if (replace.containsKey("within"))
			within = (String) replace.get("within");
		else
			within = q.getWithin();
		if (replace.containsKey("group"))
			group = (String) replace.get("group");
		else
			group = q.getGroup();
		if (replace.containsKey("sort"))
			sort = (String) replace.get("sort");
		else
			sort = q.getSort();
		if (replace.containsKey("docpid"))
			docPid = (String) replace.get("docpid");
		else
			docPid = q.getDocPid();
		if (replace.containsKey("view"))
			view = (Integer) replace.get("view");
		else
			view = q.getView();
		if (replace.containsKey("from"))
			from = (Integer) replace.get("from");
		else
			from = q.getFrom();
		if (replace.containsKey("start"))
			start = (Integer) replace.get("start");
		else
			start = q.getStart();
		if (replace.containsKey("end"))
			end = (Integer) replace.get("end");
		else
			end = q.getEnd();
		if (replace.containsKey("first"))
			first = (Integer) replace.get("first");
		else
			first = q.getFirst();
		if (replace.containsKey("number"))
			number = (Integer) replace.get("number");
		else
			number = q.getNumber();
		if (replace.containsKey("filters")) {
			@SuppressWarnings("unchecked")
			Map<String,Map<String,List<String>>> map = (Map<String,Map<String,List<String>>>) replace.get("filters");
			filters = map;
		} else
			filters = q.getFilters();

		if (!docPid.equals(""))
			document = new WhitelabDocument(docPid);
	}
	
	public Query(BaseResponse br) {
		try {
			id = UUID.randomUUID().toString();
			pattern = URLDecoder.decode(br.getParameter("query", ""), "UTF-8");
			within = br.getParameter("within", "");
			view = br.getParameter("view", 1);
			from = br.getParameter("from", 0);
			group = URLDecoder.decode(br.getParameter("group", ""), "UTF-8");
			sort = URLDecoder.decode(br.getParameter("sort", ""), "UTF-8");
			start = br.getParameter("start", -1);
			end = br.getParameter("end", -1);
			first = br.getParameter("first", 0);
			number = br.getParameter("number", 50);
			docPid = br.getParameter("docpid", "");
			if (!docPid.equals(""))
				document = new WhitelabDocument(docPid);
			if (view == 12)
				wordsAroundHit = 0;
			else
				wordsAroundHit = -1;
//			setPattern(br.getParameter("query", "").replaceAll("&", "%26"));
			generateFilterStringFromInput(br,true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public Query updateQuery(BaseResponse br) {
		try {
			String p = URLDecoder.decode(br.getParameter("query", ""), "UTF-8");
			String w = br.getParameter("within", "");
			int v = br.getParameter("view", 1);
//			int f = br.getParameter("from", 0);
			String g = URLDecoder.decode(br.getParameter("group", ""), "UTF-8");
			String s = URLDecoder.decode(br.getParameter("sort", ""), "UTF-8");
			int st = br.getParameter("start", -1);
			int en = br.getParameter("end", -1);
			int fi = br.getParameter("first", 0);
			int n = br.getParameter("number", 50);
			String d = br.getParameter("docpid", "");
			
			if (!p.equals(pattern) || !w.equals(within) ||
					(v == 12 && wordsAroundHit != 0)) {
				br.getServlet().log("QUERY CHANGED - NEW QUERY");
				Query query = new Query(br);
				return query;
			} else if (v != view || !g.equals(group) || !s.equals(sort) || n != number ||
					fi != first) {
				br.getServlet().log("QUERY CHANGED - RESET");
				view = v;
				group = g;
				sort = s;
				number = n;
				first = fi;
				this.resetStatus();
			}
			
			if (st != start || en != end || !d.equals(docPid)) {
				br.getServlet().log("QUERY CHANGED - DOC UPDATED");
				if (d.equals("")) {
					docPid = d;
					document = null;
				} else if (!d.equals(docPid)) {
					docPid = d;
					document = new WhitelabDocument(docPid);
				}
				if (document != null) {
					start = st;
					end = en;
				}
			}
			
			String ff = generateFilterStringFromInput(br,false);
			if (!ff.equals(filter)) {
				br.getServlet().log("FILTER CHANGED");
				Query query = new Query(br);
				return query;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return this;
	}

	public String getId() {
		return id;
	}
	
//	public void setPattern(String p) {
//		if (p.contains("within")) {
//			pattern = p.split(" within")[0];
//			setWithin("within"+p.split(" within")[1]);
//		} else {
//			pattern = p;
//			within = "";
//		}
//	}
	
	public String getSimplePattern() {
		String patt = pattern.replaceAll("\"", "QT\"QT");
		String[] parts = patt.split("QT");
		if (parts.length == 1)
			return patt;
		else {
			List<String> keep = new ArrayList<String>();
			boolean inside = false;
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].equals("\"")) {
					if (inside)
						inside = false;
					else
						inside = true;
				} else if (inside) {
					String str = parts[i].replace("(?i)", "").replace("(?c)", "");
					keep.add(str);
				}
			}
			return StringUtils.join(keep.toArray()," ");
		}
	}
	
	public void setPattern(String p) {
		pattern = p;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String getPatternWithin() {
		if (within.length() > 0 && !within.equals("document"))
			if (within.equals("sentence"))
				return pattern+" within <s/>";
			else
				return pattern+" within (<p/>|<head/>|<event/>)";
		return pattern;
	}
	
	public void setWithin(String w) {
		within = w;
	}
	
	public String getWithin() {
		return within;
	}
	
	public String getWithinString() {
		if (within.length() > 0)
			return within;
		return "document";
	}
	
	public void setView(int v) {
		view = v;
	}
	
	public int getView() {
		return view;
	}
	
	public void setFrom(int f) {
		from = f;
	}
	
	public int getFrom() {
		return from;
	}
	
	public String getFromString(String namespace) {
		if (namespace.equals("search")) {
			if (from == 1)
				return "simple";
			else if (from == 2)
				return "extended";
			else if (from == 3)
				return "advanced";
			else
				return "expert";
		} else if (namespace.equals("explore")) {
			if (from == 5)
				return "corpus";
			else if (from == 6)
				return "statistics";
			else if (from == 7)
				return "ngrams";
			else if (from == 8)
				return "document";
		}
		return "";
	}
	
	public void setGroup(String g) {
		group = g;
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getGroupClean() {
		return group.replace("field:", "");
	}
	
	public void setSort(String s) {
		sort = s;
	}
	
	public String getSort() {
		return sort;
	}
	
	public void setFilter(String f) {
		filter = f;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public Map<String,Map<String,List<String>>> getFilters() {
		return filters;
	}
	
	public void setGroups(int g) {
		groups = g;
	}
	
	public int getGroups() {
		return groups;
	}
	
	public void setHits(int h) {
		hits = h;
	}
	
	public int getHits() {
		return hits;
	}
	
	public void setDocs(int d) {
		docs = d;
	}
	
	public int getDocs() {
		return docs;
	}
	
	public void setStatus(int s) {
		status = s;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getStatusString() {
		if (status == 0)
			return "WAITING";
		else if (status == 1)
			return "COUNTING";
		else if (status == 2 && hits > -1)
			return "FINISHED";
		return "ERROR";
	}
	
	public void setCurrent(boolean c) {
		current = c;
	}
	
	public boolean isCurrent() {
		return current;
	}

	public void setStart(Integer s) {
		start = s;
	}
	
	public int getStart() {
		return start;
	}

	public void setEnd(Integer e) {
		end = e;
	}
	
	public int getEnd() {
		return end;
	}

	public void setFirst(Integer f) {
		first = f;
	}
	
	public int getFirst() {
		return first;
	}

	public void setNumber(Integer n) {
		number = n;
	}

	public int getNumber() {
		return number;
	}

	public void setDocPid(String d) {
		docPid = d;
	}
	
	public String getDocPid() {
		return docPid;
	}

	public void setDocument(WhitelabDocument d) {
		document = d;
	}
	
	public WhitelabDocument getDocument() {
		return document;
	}

	public void setWordsAroundHit(int i) {
		wordsAroundHit = i;
	}
	
	public int getWordsAroundHit() {
		return wordsAroundHit;
	}
	
	public void setResult(String r) {
		result = r;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setCloud(JSONArray c) {
		cloud = c;
	}
	
	public JSONArray getCloud() {
		return cloud;
	}
	
	public void resetStatus() {
		setStatus(0);
//		setHits(0);
//		setDocs(0);
		setResult("");
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("patt",this.getPatternWithin());
		if (group.length() > 0)
			params.put("group",group);
		if (sort.length() > 0)
			params.put("sort", sort);
		if (first > 0)
			params.put("first", first);
		if (docPid.length() == 0)
			params.put("number", number);
		if (start > -1)
			params.put("start", start);
		if (end > -1)
			params.put("end", end);
		if (wordsAroundHit > -1)
			params.put("wordsaroundhit", wordsAroundHit);
		if (filter.length() > 0)
			params.put("filter", filter);
		
		return params;
	}
	
	public String getUrl(String page, String suffix, boolean onlyId, String[] exceptArray) {
		List<String> except = new ArrayList<String>();
		if (exceptArray != null)
			except = Arrays.asList(exceptArray);
		return getUrl(page,suffix,onlyId,except);
	}
		
	public String getUrl(String page, String suffix, boolean onlyId, List<String> exceptArray) {
		List<String> except = new ArrayList<String>();
		if (exceptArray != null)
			except = exceptArray;
		String url = "/whitelab/"+page+"?";
		url = url+"id="+getId();
		if (!onlyId) {
			try {
				if (!except.contains("query"))
					url = url+"&query="+URLEncoder.encode(pattern, "UTF-8");
				if (within.length() > 0 && !except.contains("within"))
					url = url+"&within="+URLEncoder.encode(within, "UTF-8");
				if (page.contains("/results") && view > 0 && !except.contains("view"))
					url = url+"&view="+view;
				if (from > 0 && !except.contains("from"))
					url = url+"&from="+from;
				if (filter.length() > 0 && !except.contains("filter"))
					url = url+"&filter="+URLEncoder.encode(getFilterUrlParameters(), "UTF-8");
				if (group.length() > 0 &&!except.contains("group"))
					url = url+"&group="+URLEncoder.encode(group, "UTF-8");
				if (sort.length() > 0 && !except.contains("sort"))
					url = url+"&sort="+URLEncoder.encode(sort, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (first > 0 && !except.contains("first"))
				url = url+"&first="+first;
			if (docPid.length() == 0 && number > 50 && !except.contains("number"))
				url = url+"&number="+number;
			if (docPid.length() > 0 && !except.contains("docpid"))
				url = url+"&docpid="+docPid;
			if (start > -1 && !except.contains("start"))
				url = url+"&start="+start;
			if (end > -1 && !except.contains("end"))
				url = url+"&end="+end;
		}
		if (suffix != null)
			url = url + suffix;
		return url;
	}
	
	public String getFilterUrlParameters() {
		List<String> params = new ArrayList<String>();
		for (String f : filters.keySet()) {
			for (String v : filters.get(f).get("is"))
				params.add(f+"="+v);
			for (String v : filters.get(f).get("isnot"))
				params.add(f+"=-"+v);
		}
		return StringUtils.join(params.toArray(),"&");
	}

	public String generateFilterStringFromInput(BaseResponse baseResponse,boolean update) {
		Map<String,Map<String,List<String>>> filters_ = new HashMap<String,Map<String,List<String>>>();
		
		for (MetadataField dataField : baseResponse.getServlet().getMetadataFields()) {
			String[] filterValues = baseResponse.getParameterValues(dataField.getName(), null);
			if (filterValues != null && filterValues.length > 0) {
				Map<String,List<String>> vals = new HashMap<String,List<String>>();
				List<String> is = new ArrayList<String>();
				List<String> isnot = new ArrayList<String>();
				vals.put("is", is);
				vals.put("isnot", isnot);
				
				for (int i = 0; i < filterValues.length; i++) {
					String filterValue = "";
					try {
						filterValue = URLDecoder.decode(filterValues[i], "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					if (filterValue.startsWith("-") || filterValue.startsWith("\"-")) {
						filterValue = filterValue.replaceFirst("-", "");
						vals.get("isnot").add(filterValue);
					} else {
						vals.get("is").add(filterValue);
					}
				}
				
				if (vals.get("isnot").size() > 0 && vals.get("is").size() == 0) {
					for (String filterValue : dataField.getValues()) {
						filterValue = "\""+filterValue+"\"";
						if (!vals.get("isnot").contains(filterValue) && !vals.get("is").contains(filterValue))
							vals.get("is").add(filterValue);
					}
				} else {
					vals.remove("isnot");
				}
				
				filters_.put(dataField.getName(), vals);
			}
		}

		List<String> filterStrings_ = new ArrayList<String>();
		String filter_ = "";
		if (filters_.keySet().size() > 0) {
			for (String field : filters_.keySet()) {
				filterStrings_.add(field+":("+StringUtils.join(filters_.get(field).get("is").toArray(), " OR ").replaceAll("&", "%26")+")");
			}
			filter_ = "("+StringUtils.join(filterStrings_.toArray()," AND ")+")";
		}
		
		if (update) {
			filters = filters_;
//			filterStrings = filterStrings_;
			filter = filter_;
		}
		
		return filter_;
	}
	
	public boolean equalPattern(String p, String w) {
		return p.equals(pattern) && w.equals(within);
	}
	
}
