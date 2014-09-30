/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.FieldDescriptor;
import com.uvt.whitelab.util.MetadataField;

public class ExploreResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		
		loadTreemapOptions();
		loadPosValues();
		loadMetaDataComponents();

		String tab = this.getParameter("tab", "treemap");
		this.getContext().put("maintab", "explore");
		this.getContext().put("field", "CollectionName");
		this.getContext().put("showMetaOptions", "no");
		this.getContext().put("tab", tab);
		this.displayHtmlTemplate(this.servlet.getTemplate("explore"));
	}

	private void loadTreemapOptions() {
		List<String> options = new ArrayList<String>();
		int i = 0;
		
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			String fieldName = dataField.getName();
			if (this.labels.containsKey("metadataFields."+fieldName))
				fieldName = this.labels.getString("metadataFields."+fieldName);
			int total = dataField.getTotal();
			int subtotal = dataField.getSubTotal();
			if (subtotal > total / 2) {
				i++;
				String checked = "";
				if (i == 1)
					checked = "checked";
				options.add("<label class=\"treemap-option\"><input type=\"radio\" name=\"field\" value=\""+dataField.getName()+"\" "+checked+"/> &nbsp;"+fieldName+"</label>");
			}
		}
		
		this.getContext().put("treemapOptions", StringUtils.join(options.toArray(),"\n"));
	}

	private void loadPosValues() {
		LinkedList<FieldDescriptor> fields = this.servlet.getSearchFields();
		
		List<String> options = new ArrayList<String>();
		options.add("<option value=\"\">&lt;any&gt;</option>");
		
		for (FieldDescriptor field : fields) {
			if (field.getSearchField().equals("pos")) {
				for (int i = 1; i <= 12; i++) {
					options.add("<option value=\""+labels.getString("pos."+i+".value")+"\">"+labels.getString("pos."+i+".name")+"</option>");
				}
			}
		}
		
		this.getContext().put("posValues", "<select class=\"input pos\">"+StringUtils.join(options.toArray(),"")+"</select>");
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ExploreResponse");
	}

	@Override
	public ExploreResponse duplicate() {
		return new ExploreResponse();
	}

}
