package com.uvt.whitelab.util;

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
		} else
			pattern = p;
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
	
	public void resetStatus() {
		setStatus(0);
		setHits(0);
		setDocs(0);
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
	
}
