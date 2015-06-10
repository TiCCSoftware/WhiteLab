package com.uvt.whitelab.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.uvt.whitelab.BaseResponse;

public class Query {

	private String id;
	private String pattern = "";
	private String within = "";
	private int view = 0;
	private int from = 0;
	private String group = "";
	private String sort = "";
	private String filter = "";
	private int hits = 0;
	private int docs = 0;
	private int status = 0;
	private boolean current = false;
	private int wordsAroundHit = -1;
	private int first = 0;
	private int number = 50;
	private String docPid = "";
	private int start = -1;
	private int end = -1;
	private String result = "";
	private Map<String,Map<String,List<String>>> filters;
//	private List<String> filterStrings;
	
	public Query (String i, String p, int v, int f) {
		id = i;
		setPattern(p);
		view = v;
		from = f;
	}
	
	public Query(BaseResponse baseResponse) {
		try {
			id = UUID.randomUUID().toString();
			view = baseResponse.getParameter("view", 0);
			from = baseResponse.getParameter("from", 0);
			group = URLDecoder.decode(baseResponse.getParameter("group", ""), "UTF-8");
			sort = URLDecoder.decode(baseResponse.getParameter("sort", ""), "UTF-8");
			start = baseResponse.getParameter("start", -1);
			end = baseResponse.getParameter("end", -1);
			first = baseResponse.getParameter("first", 0);
			number = baseResponse.getParameter("number", 50);
			if (view == 12)
				wordsAroundHit = 0;
			else
				wordsAroundHit = -1;
			setPattern(baseResponse.getParameter("query", "").replaceAll("&", "%26"));
			generateFilterStringFromInput(baseResponse,true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public Query updateQuery(BaseResponse br) {
		try {
			int v = br.getParameter("view", 0);
			int f = br.getParameter("from", 0);
			String g = URLDecoder.decode(br.getParameter("group", ""), "UTF-8");
			String s = URLDecoder.decode(br.getParameter("sort", ""), "UTF-8");
			int st = br.getParameter("start", -1);
			int en = br.getParameter("end", -1);
			int fi = br.getParameter("first", 0);
			int n = br.getParameter("number", 0);
			String d = br.getParameter("docpid", "");
			
			if (v != view || f != from || !g.equals(group) || !s.equals(sort) || st != start || en != end || fi != first || n != number || !d.equals(docPid) ||
					(v == 12 && wordsAroundHit != 0)) {
				Query query = new Query(br);
				return query;
			}
			
			String ff = generateFilterStringFromInput(br,false);
			if (!ff.equals(filter)) {
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
	
	public void setPattern(String p) {
		if (p.contains("within")) {
			pattern = p.split(" within")[0];
			setWithin("within"+p.split(" within")[1]);
		} else {
			pattern = p;
			within = "";
		}
	}
	
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
					String str = parts[i].replace("(?i)", "");
					keep.add(str);
				}
			}
			return StringUtils.join(keep.toArray()," ");
		}
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String getPatternWithin() {
		if (within.length() > 0)
			return pattern+" "+within;
		return pattern;
	}
	
	public void setWithin(String w) {
		within = w;
	}
	
	public String getWithin() {
		return within;
	}
	
	public String getWithinString() {
		if (within.contains("<s>")) {
			return "sentence";
		} else if (within.contains("<p>")) {
			return "paragraph";
		}
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
			return "NEW";
		else if (status == 1)
			return "COUNTING";
		else if (status == 2)
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
	
//	public void resetStatus(String msg) {
//		System.out.println("resetStatus('"+msg+"')");
//		setStatus(0);
//		setHits(0);
//		setDocs(0);
//		setResult("");
//	}

	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("patt",pattern);
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
	
	public String getUrl(String page, String suffix) {
		String url = "/whitelab/"+page+"?";
		url = url+"id="+getId();
		try {
			url = url+"&query="+URLEncoder.encode(getPatternWithin(), "UTF-8");
			if (page.contains("/results") && view > 0)
				url = url+"&view="+view;
//			if (from > 0)
//				url = url+"&from="+from;
			if (filter.length() > 0)
				url = url+"&filter="+URLEncoder.encode(getFilterUrlParameters(), "UTF-8");
			if (group.length() > 0)
				url = url+"&group="+URLEncoder.encode(group, "UTF-8");
			if (sort.length() > 0)
				url = url+"&sort="+URLEncoder.encode(sort, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (first > 0)
			url = url+"&first="+first;
		if (docPid.length() == 0 && number > 50)
			url = url+"&number="+number;
		if (start > -1)
			url = url+"&start="+start;
		if (end > -1)
			url = url+"&end="+end;
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
	
}
