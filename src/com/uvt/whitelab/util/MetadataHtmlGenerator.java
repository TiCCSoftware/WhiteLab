package com.uvt.whitelab.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uvt.whitelab.WhiteLab;

public class MetadataHtmlGenerator {
	private final WhiteLab servlet;
	private List<MetadataField> filterFields = null;
	private LinkedList<FieldDescriptor> searchFields = null;

	public MetadataHtmlGenerator(WhiteLab s) {
		servlet = s;
	}

	public void init(ResourceBundle labels, Document xml) {
		filterFields = new ArrayList<MetadataField>();
		searchFields = new LinkedList<FieldDescriptor>();

		try {
			XPath xPath =  XPathFactory.newInstance().newXPath();
			String expr = "//metadataField/fieldName";
			NodeList nodeList = (NodeList) xPath.compile(expr).evaluate(xml, XPathConstants.NODESET);
			for (int n = 0; n < nodeList.getLength(); n++) {
				Node node = nodeList.item(n);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) node;
					String field = el.getTextContent();
					if (labels.containsKey("metadataFields."+field)) {
						MetadataField dataField = new MetadataField(field,labels);
						servlet.log("Loading field "+field);
						dataField.load(true);
						filterFields.add(dataField);
					}
				}
			}
			
			String[] fields = labels.getString("searchfields").split(",");
			for (String fieldName : fields) {
				expr = "//basicProperties/property[@name=\""+fieldName+"\"]";
				Node node = (Node) xPath.compile(expr).evaluate(xml, XPathConstants.NODE);
				if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) node;
					boolean isSensitive = false;
					if (el.getElementsByTagName("sensitivity").item(0).getTextContent().equals("SENSITIVE_AND_INSENSITIVE"))
						isSensitive = true;
					FieldDescriptor searchField = new FieldDescriptor(fieldName, isSensitive, fieldName, fieldName);
					
					int l = 1;
					
					while (labels.containsKey(fieldName+"."+l+".value")) {
						searchField.addValidValue(labels.getString(fieldName+"."+l+".value"), labels.getString(fieldName+"."+l+".name"));
						l++;
					}
					
					searchFields.add(searchField);
				}
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public List<MetadataField> getFilterFields() {
		return filterFields;
	}

	public LinkedList<FieldDescriptor> getSearchFields() {
		return searchFields;
	}
	
	public SortedSet<String> loadFilters(ResourceBundle labels) {
		SortedSet<String> filters = new TreeSet<String>();
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			String fieldName = dataField.getName();
			filters.add(fieldName);
		}
		return filters;
	}
	
	public Map<String,String> loadSelectFields(ResourceBundle labels) {
		Map<String,String> selectFields = new HashMap<String,String>();
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			if (dataField.numberOfValues() > 0) {
				List<String> vals = new ArrayList<String>();
				Map<String,String> fdOptions = new HashMap<String,String>();
				for (String value : dataField.getValues()) {
					vals.add(value);
					fdOptions.put(value, "<option value=\""+value+"\">"+value+"</option>");
				}

				String select = "<select class=\"metaInput\"><option value=\"\" selected></option>";
				SortedSet<String> keys = new TreeSet<String>(vals);
				for (String fieldValue : keys) {
					select = select+fdOptions.get(fieldValue);
				}
				if (!dataField.isComplete())
					select = select+"<option value=\"other\">"+labels.getString("other")+"</option>";
				
				select = select+"</select>";
				selectFields.put(dataField.getName(), select);
			} else {
				String select = "<input class=\"metaInput\" type=\"text\" />";
				selectFields.put(dataField.getName(), select);
			}
		}
		return selectFields;
	}
	
	public String loadSelectField(ResourceBundle labels, String metaLabel, String metaValue) {
		this.servlet.log("METAVALUE: '"+metaValue+"'");
		String select = "";
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			if (dataField.getName().equals(metaLabel)) {
				if (dataField.numberOfValues() > 0) {
					List<String> vals = new ArrayList<String>();
					Map<String,String> fdOptions = new HashMap<String,String>();
					for (String value : dataField.getValues()) {
						vals.add(value);
						if (value.equals(metaValue))
							fdOptions.put(value, "<option value=\""+value+"\" selected>"+value+"</option>");
						else
							fdOptions.put(value, "<option value=\""+value+"\">"+value+"</option>");
					}
	
					if (metaValue.length() == 0)
						select = "<select class=\"metaInput\"><option value=\"\" selected></option>";
					else
						select = "<select class=\"metaInput\"><option value=\"\"></option>";
					
					SortedSet<String> keys = new TreeSet<String>(vals);
					for (String fieldValue : keys) {
						select = select+fdOptions.get(fieldValue);
					}
					if (!dataField.isComplete()) {
						if (metaValue.equals("other"))
							select = select+"<option value=\"other\" selected>"+labels.getString("other")+"</option>";
						else
							select = select+"<option value=\"other\">"+labels.getString("other")+"</option>";
					}
					
					select = select+"</select>";
				} else {
					select = "<input class=\"metaInput\" type=\"text\" value=\""+metaValue+"\" />";
				}
			}
		}
		return select;
	}
	
	public Map<String,String> generateOptions(ResourceBundle labels) {
		Map<String,String> options = new HashMap<String,String>();
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			String fieldName = dataField.getName();
			String fieldLabel = fieldName;
			if (labels.containsKey("metadataFields."+fieldName))
				fieldLabel = labels.getString("metadataFields."+fieldName);
			
			options.put(fieldName, "<option value=\"field:"+dataField.getName()+"\">"+fieldLabel+"</option>");
		}
		return options;
	}
	
	public String generateOption(String fieldName, String fieldLabel, boolean selected, String classLabel) {
		String option = "<option value=\""+fieldName+"\" class=\""+classLabel+"\"";
		if (selected)
			option = option+" selected=\"true\">"+fieldLabel+"</option>";
		else
			option = option+">"+fieldLabel+"</option>";
		return option;
	}
	
	public String generateQueryRules(ResourceBundle labels, SortedSet<String> filters, Map<String, String> options, Query query) {
		if (query == null)
			return "";
		List<String> queryRules = new ArrayList<String>();
		Map<String,Map<String,List<String>>> queryFilters = query.getFilters();
		for (String filter : queryFilters.keySet()) {
			if (queryFilters.get(filter).containsKey("is")) {
				for (String value : queryFilters.get(filter).get("is"))
					queryRules.add(generateRule(labels,filters,options,filter,"is",value));
			}
			if (queryFilters.get(filter).containsKey("isnot")) {
				for (String value : queryFilters.get(filter).get("isnot"))
					queryRules.add(generateRule(labels,filters,options,filter,"isnot",value));
			}
		}
		return StringUtils.join(queryRules.toArray(),"");
	}

	public String generateRule(ResourceBundle labels, SortedSet<String> filters, Map<String, String> options, String metaLabel, String operator, String metaValue) {
		metaValue = metaValue.replaceAll("\"", "");
		String rule = "<div class=\"rule row large-16 medium-16 small-16\">"
				+ "<div class=\"large-4 medium-4 small-4 columns\">"
				+ "<select class=\"metaLabel switchable\">"
				+ "<option value=\"\" disabled=\"true\"></option>";
			
			Iterator<String> it = filters.iterator();
			while (it.hasNext()) {
				String option = options.get(it.next());
				if (option.contains("value=\"field:"+metaLabel)) {
					option = option.substring(0, option.indexOf(">")) + " selected=\"true\"" + option.substring(option.indexOf(">"));
				}
				rule = rule + option;
			}
				
			rule = rule + "</select>"
				+ "</div>"
				+ "<div class=\"large-3 medium-3 small-3 columns\">"
				+ "<select class=\"metaOperator\">";
			
			if (operator.equals("is"))
				rule = rule + "<option value=\"is\" selected=\"true\">"+labels.getString("meta.is")+"</option><option value=\"not\">"+labels.getString("meta.not")+"</option>";
			else
				rule = rule + "<option value=\"is\">"+labels.getString("meta.is")+"</option><option value=\"not\" selected=\"true\">"+labels.getString("meta.not")+"</option>";
			
			rule = rule + "</select>"
				+ "</div>"
				+ "<div class=\"large-7 medium-7 small-7 columns\">";
			
			rule = rule + loadSelectField(labels, metaLabel, metaValue);
			
			rule = rule + "</div>"
				+ "<div class=\"large-2 medium-2 small-2 columns\">"
				+ "<a class=\"meta-min\" onclick=\"Whitelab.meta.removeRule(this)\">"
				+ "<img src=\"../web/img/minus.png\">"
				+ "</a>"
				+ "<a class=\"meta-plus\" onclick=\"Whitelab.meta.addRule()\">"
				+ "<img src=\"../web/img/plus.png\">"
				+ "</a>"
				+ "</div>"
				+ "</div>";
		return rule;
	}
	
	public String generateEmptyRule(ResourceBundle labels, SortedSet<String> filters, Map<String, String> options) {
		String rule = "<div class=\"rule row large-16 medium-16 small-16\">"
				+ "<div class=\"large-4 medium-4 small-4 columns\">"
				+ "<select class=\"metaLabel switchable\">"
				+ "<option value=\"\" disabled=\"true\" selected=\"true\"></option>";
			
			Iterator<String> it = filters.iterator();
			while (it.hasNext()) {
				rule = rule + options.get(it.next());
			}
				
			rule = rule + "</select>"
				+ "</div>"
				+ "<div class=\"large-3 medium-3 small-3 columns\">"
				+ "<select class=\"metaOperator\">"
				+ "<option value=\"is\" selected=\"true\">"+labels.getString("meta.is")+"</option>"
				+ "<option value=\"not\">"+labels.getString("meta.not")+"</option>"
				+ "</select>"
				+ "</div>"
				+ "<div class=\"large-7 medium-7 small-7 columns\">"
				+ "<input class=\"metaInput\" type=\"text\">"
				+ "</div>"
				+ "<div class=\"large-2 medium-2 small-2 columns\">"
				+ "<a class=\"meta-min\" onclick=\"Whitelab.meta.removeRule(this)\">"
				+ "<img src=\"../web/img/minus.png\">"
				+ "</a>"
				+ "<a class=\"meta-plus\" onclick=\"Whitelab.meta.addRule()\">"
				+ "<img src=\"../web/img/plus.png\">"
				+ "</a>"
				+ "</div>"
				+ "</div>";
		return rule;
	}
	
}
