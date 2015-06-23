/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WhitelabDocument {

	public String id = "";
	private String content = "";
	private String metadata = "";
	private String xml = "";
	@SuppressWarnings("unused")
	private String metaXml = "";
	public int start = -1;
	public int end = -1;

	private Map<String,Map<String,Integer>> lemmasByPos = new HashMap<String,Map<String,Integer>>();
	private Map<String,Map<Integer,List<String>>> posBins = new HashMap<String,Map<Integer,List<String>>>();
	private List<String> lemmas = new ArrayList<String>();
	private List<String> types = new ArrayList<String>();
	private Map<String,Integer> posTokenTotals = new HashMap<String,Integer>();
	private Map<String,Integer> posLemmaTotals = new HashMap<String,Integer>();
	
	private Integer tokenCount = 0;
	private Integer lemmaCount = 0;
	private Integer typeCount = 0;
	
	public WhitelabDocument() {
	}
	
	public WhitelabDocument(String docPid) {
		id = docPid;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String pid) {
		this.id = pid;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getMetadata() {
		return metadata;
	}
	
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public Integer getTokenCount() {
		return tokenCount;
	}
	
	public void setLemmas(List<String> l) {
		lemmas = l;
	}
	
	public List<String> getLemmas() {
		return lemmas;
	}
	
	public void setTypes(List<String> t) {
		types = t;
	}
	
	public List<String> getTypes() {
		return types;
	}

	public JSONArray getGrowthData(String lang, String format, Boolean bare, int startIndex, int startValue1, int startValue2) {
		JSONArray data = new JSONArray();
		List<String> doneT = new ArrayList<String>();
		List<String> doneL = new ArrayList<String>();
		
		if (!bare) {
			try {
				JSONArray header = new JSONArray();
				header.put(0, "token");
				if (lang.equals("en")) {
					header.put(1, "Unique type count");
					header.put(3, "Unique lemma count");
				} else {
					header.put(1, "Unieke types");
					header.put(3, "Unieke lemmas");
				}
				header.put(2, "tooltip1");
				header.put(4, "tooltip2");
				data.put(header);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		int x = startIndex;
		int y1 = startValue1;
		int y2 = startValue2;
		
		try {
			JSONArray zero = new JSONArray();
			zero.put(0, x);
			zero.put(1, y1);
			zero.put(3, y2);
			if (bare) {
				zero.put(2, "");
				zero.put(4, "");
			} else {
				if (lang.equals("en")) {
					zero.put(2, "token: "+x+"\nunique types: "+y1);
					zero.put(4, "token: "+x+"\nunique lemmas: "+y2);
				} else {
					zero.put(2, "token: "+x+"\nunieke types: "+y1);
					zero.put(4, "token: "+x+"\nunieke lemmas: "+y2);
				}
			}
			data.put(zero);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < types.size(); i++) {
			x++;
			String type = types.get(i);
			String lemma = lemmas.get(i);
			if (!doneT.contains(type)) {
				doneT.add(type);
				y1++;
			}
			if (!doneL.contains(lemma)) {
				doneL.add(lemma);
				y2++;
			}
			
			try {
				JSONArray row = new JSONArray();
				row.put(0, x);
				row.put(1, y1);
				row.put(3, y2);
				if (bare) {
					row.put(2, type);
					row.put(4, lemma);
				} else {
					if (lang.equals("en")) {
						row.put(2, "token: "+x+"\ntype: "+type+"\nunique types: "+y1);
						row.put(4, "token: "+x+"\nlemma: "+lemma+"\nunique lemmas: "+y2);
					} else {
						row.put(2, "token: "+x+"\nwoordvorm: "+type+"\nunieke woordvormen: "+y1);
						row.put(4, "token: "+x+"\nlemma: "+lemma+"\nunieke lemmas: "+y2);
					}
				}
				data.put(row);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	public String getGrowthDataString(String lang, String format, Boolean bare, int startIndex, int startValue1, int startValue2) {
		JSONArray data = getGrowthData(lang, format, bare, startIndex, startValue1, startValue2);
		if (format.equals("csv")) {
			List<Integer> skip = new ArrayList<Integer>();
			skip.add(2);
			skip.add(4);
			return dataTableToCsv(data, skip);
		}
		return data.toString();
	}

	public String getCloudData() {
		JSONArray data = new JSONArray();
		
		for (String pos : posBins.keySet()) {
			List<Map<String,Integer>> top10 = getPosTop(pos,10);
			for (Map<String,Integer> l : top10) {
				String lemma = l.keySet().iterator().next();
				Integer freq = l.get(lemma);
				JSONObject ll = createLemma(lemma,pos,freq);
				data.put(ll);
			}
		}
		
		return data.toString();
	}
	
	public String getPosHistogram(String pos, String format) {
		JSONArray data = new JSONArray();
		
		try {
			JSONArray header = new JSONArray();
			header.put(0, "lemma");
			header.put(1, "frequency");
			data.put(header);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Map<String,Integer> lemmas = lemmasByPos.get(pos);
		
		if (lemmas != null) {
			for (String lemma : lemmas.keySet()) {
				try {
					JSONArray l = new JSONArray();
					l.put(0, lemma);
					l.put(1, lemmas.get(lemma));
					data.put(l);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		if (format.equals("csv")) {
			return dataTableToCsv(data,new ArrayList<Integer>());
		}
		return data.toString();
	}
	
	public String getPosFreqList(String pos, String format) {
		JSONArray data = new JSONArray();
		
		try {
			JSONArray header = new JSONArray();
			header.put(0, "lemma");
			header.put(1, "frequency");
			data.put(header);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<Map<String,Integer>> top10 = getPosTop(pos,10);
		
		for (Map<String,Integer> l : top10) {
			String lemma = l.keySet().iterator().next();
			Integer freq = l.get(lemma);
			try {
				JSONArray ll = new JSONArray();
				ll.put(0, lemma);
				ll.put(1, freq);
				data.put(ll);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (format.equals("csv")) {
			return dataTableToCsv(data,new ArrayList<Integer>());
		}
		return data.toString();
	}

	public void count() {
		countLemmasByPos();
		getPosBins();
	}
	
	public void countLemmasByPos() {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
		    Document xmlDoc = builder.parse(is);
		    
		    NodeList nList = xmlDoc.getElementsByTagName("w");
		    
		    for (int i = 0; i < nList.getLength(); i++) {
		    	Node node = nList.item(i);
		    	if (node.getNodeType() == Node.ELEMENT_NODE) {
		    		Element word = (Element) node;
		    		NodeList list2 = word.getChildNodes();
		    		String type = "";
		    		String lemma = "";
		    		String pos = "";
		    		
		    		for (int j = 0; j < list2.getLength(); j++) {
		    			Node child = list2.item(j);
		    			if (child.getNodeType() == Node.ELEMENT_NODE) {
		    				Element el = (Element) child;
		    				if (child.getNodeName().equals("t")) {
		    					type = el.getTextContent();
		    				} else if (child.getNodeName().equals("lemma")) {
		    					lemma = el.getAttribute("class");
		    				} else if (child.getNodeName().equals("pos")) {
		    					pos = el.getAttribute("head");
		    				}
		    			}
		    		}
		    		
		    		if (!posTokenTotals.containsKey(pos)) {
		    			posTokenTotals.put(pos, 0);
		    		}
		    		posTokenTotals.put(pos, posTokenTotals.get(pos) + 1);
		    		
		    		if (!posLemmaTotals.containsKey(pos)) {
		    			posLemmaTotals.put(pos, 0);
		    		}
		    		
		    		if (!lemmasByPos.containsKey(pos)) {
		    			Map<String,Integer> posCounts = new HashMap<String,Integer>();
		    			lemmasByPos.put(pos, posCounts);
		    		}
		    		
		    		Map<String,Integer> posCounts = lemmasByPos.get(pos);
		    		
		    		if (posCounts.containsKey(lemma)) {
		    			posCounts.put(lemma, posCounts.get(lemma) + 1);
		    		} else {
			    		posLemmaTotals.put(pos, posLemmaTotals.get(pos) + 1);
		    			posCounts.put(lemma, 1);
		    		}

		    		tokenCount++;
		    		if (!lemmas.contains(lemma)) {
		    			lemmaCount++;
		    		}
		    		if (!types.contains(type)) {
		    			typeCount++;
		    		}
		    		
		    		
		    		lemmas.add(lemma);
		    		types.add(type);
		    	}
		    }
		    
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getPosBins() {
		for (String pos : lemmasByPos.keySet()) {
			Map<String,Integer> posCounts = lemmasByPos.get(pos);
			Map<Integer,List<String>> bins = new HashMap<Integer,List<String>>();
			
			for (String lemma : posCounts.keySet()) {
				Integer freq = posCounts.get(lemma);
				if (!bins.containsKey(freq)) {
					List<String> bin = new ArrayList<String>();
					bins.put(freq, bin);
				}
				bins.get(freq).add(lemma);
			}
			
			posBins.put(pos, bins);
		}
	}
	
	private List<Map<String,Integer>> getPosTop(String pos, Integer max) {
		List<Map<String,Integer>> top = new ArrayList<Map<String,Integer>>();
		Map<Integer,List<String>> bins = posBins.get(pos);
		
		List<Integer> list = new ArrayList<Integer>();
		if (bins != null) {
			list.addAll(bins.keySet());
			Collections.sort(list, Collections.reverseOrder());
			for (Integer i : list) {
				List<String> lemmas = bins.get(i);
				for (String lemma : lemmas) {
					Map<String,Integer> l = new HashMap<String,Integer>();
					l.put(lemma, i);
					top.add(l);
					
					if (top.size() == max) {
						break;
					}
				}
				if (top.size() == max) {
					break;
				}
			}
		}
		
		return top;
	}
	
	private JSONObject createLemma(String lemma, String pos, Integer freq) {
		JSONObject l = new JSONObject();
		try {
			l.put("lemma", lemma);
			l.put("pos", pos);
			l.put("freq", freq);
			l.put("clean", StringCleaner.clean(lemma));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return l;
	}

	public Object getPosTotals(String type, String format) {
		JSONArray data = new JSONArray();
		
		try {
			JSONArray header = new JSONArray();
			header.put(0, "POS");
			header.put(1, "count");
			data.put(header);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Map<String,Integer> totals;
		if (type.equals("token"))
			totals = posTokenTotals;
		else
			totals = posLemmaTotals;
		
		String[] args = totals.keySet().toArray(new String[totals.keySet().size()]);
		Arrays.sort(args);
		
		for (int i = 0; i < args.length; i++) {
			String pos = args[i];
			Integer val = totals.get(pos);
			try {
				JSONArray row = new JSONArray();
				row.put(0,pos);
				row.put(1,val);
				data.put(row);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (format.equals("csv")) {
			return dataTableToCsv(data,new ArrayList<Integer>());
		}
		return data.toString();
	}
	
	private String dataTableToCsv(JSONArray data, List<Integer> skipCols) {
		String[] csv = new String[data.length()];
		for (int i = 0; i < data.length(); i++) {
			try {
				JSONArray row = data.getJSONArray(i);
				String[] line = new String[row.length() - skipCols.size()];
				int jj = -1;
				for (int j = 0; j < row.length(); j++) {
					if (!skipCols.contains(j)) {
						jj++;
						line[jj] = row.get(j).toString();
					}
				}
				csv[i] = StringUtils.join(line, ",");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return StringUtils.join(csv, ";\n");
	}

	public String getDocStats(String language) {
		JSONObject data = new JSONObject();
		
		try {
			JSONArray items = new JSONArray();
			
			JSONObject wc = new JSONObject();
			wc.put("label", "tokens");
			wc.put("value", tokenCount);
			items.put(wc);
			
			JSONObject lc = new JSONObject();
			lc.put("label", "lemmas");
			lc.put("value", lemmaCount);
			items.put(lc);
			
			JSONObject tc = new JSONObject();
			tc.put("label", "types");
			tc.put("value", typeCount);
			items.put(tc);
			
			float tt1 = (float) (tokenCount * 1.0) / typeCount;
			float tt2 = 1 / tt1;
			
			JSONObject ttr = new JSONObject();
			ttr.put("label", "type/token ratio");
			ttr.put("value", "1/"+String.format("%.2f", tt1)+" ("+String.format("%.2f", tt2)+")");
			items.put(ttr);
			
			data.put("items", items);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return data.toString();
	}

	public void setMetaXml(String meta) {
		metaXml = meta;
	}

}


