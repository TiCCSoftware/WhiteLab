/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.MetadataField;

public class TreemapResponse extends BaseResponse {
	
	public TreemapResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		String field = this.getParameter("field", "CollectionName");
		this.servlet.log("Loading treemap for field: "+field);
		MetadataField dataField = new MetadataField(field,this.labels);
		dataField.load(true);
		
		Map<String,Object> output = new HashMap<String,Object>();
		output.put("field", field);
		String fieldName = dataField.getName();
		if (this.labels.containsKey(fieldName))
			fieldName = this.labels.getString(fieldName);
		output.put("name", fieldName);
		JSONObject data = dataField.getData();
		output.put("data", data);
		sendResponse(output);
	}

	@Override
	protected void logRequest() {
		this.servlet.log("TreemapResponse");
	}

	@Override
	public TreemapResponse duplicate() {
		return new TreemapResponse("explore");
	}

}
