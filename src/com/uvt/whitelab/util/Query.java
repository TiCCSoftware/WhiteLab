package com.uvt.whitelab.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
	
	public Query (String i, String p, int v, int f) {
		id = i;
		setPattern(p);
		view = v;
		from = f;
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
	
	public String getPattern() {
		return pattern;
	}
	
	public String getPatternWithin() {
		return pattern+" "+within;
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
	
	public void resetStatus() {
		setStatus(0);
		setHits(0);
		setDocs(0);
		setResult("");
	}

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
				url = url+"&filter="+URLEncoder.encode(filter, "UTF-8");
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
	
}
