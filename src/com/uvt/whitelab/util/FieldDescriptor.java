/**
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology.
 * All rights reserved.
 *
 * @author VGeirnaert
 */
package com.uvt.whitelab.util;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class FieldDescriptor {

	public String name;
	final public String searchField;
	final public String displayField;
	final public boolean isSensitive;

	private String tabGroup = "";
	private String type = "";
	private Boolean batch = false;
	private List<ValuePair> validValues = new LinkedList<ValuePair>();

	public class ValuePair {
		public final String value;
		public String description;

		public ValuePair(String value, String description) {
			this.value = value;
			this.description = description;
		}

		public String getValue() {
			return value;
		}

		public String getDescription() {
			return description;
		}
	}

	public FieldDescriptor(String argName, boolean argSensitive, String argSearchField, String argDisplayField) {
		name = argName;
		isSensitive = argSensitive;
		searchField = argSearchField;
		displayField = argDisplayField;
	}

	public String getName() {
		return name;
	}

	public String getSearchField() {
		return searchField;
	}

	public String getDisplayField() {
		return displayField;
	}

	public boolean isSensitive() {
		return isSensitive;
	}

	public void addValidValue(String value, String description) {
		if(value == null)
			value = "";

		if(value.length() == 0)
			value = description;

		validValues.add(new ValuePair(value, description));
	}

	public boolean restrictedInput() {
		return (validValues.size() > 0);
	}

	public List<ValuePair> getValidValues() {
		return validValues;
	}

	public void setValidValues(List<ValuePair> vals) {
		validValues = vals;
	}

	public void setType(String type) {
		if(type == null)
			type = "";

		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public String getTabGroup() {
		return this.tabGroup;
	}

	public void setTabGroup(String group) {
		if(group == null)
			group = "";

		this.tabGroup = group;
	}

	public void setBatch(boolean b) {
		this.batch = b;
	}
	
	public Boolean getBatch() {
		return this.batch;
	}
}
