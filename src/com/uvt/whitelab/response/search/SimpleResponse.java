package com.uvt.whitelab.response.search;

import com.uvt.whitelab.BaseResponse;

public class SimpleResponse extends BaseResponse {
	
	public SimpleResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		if (query != null)
			this.getContext().put("query", query);
		this.displayHtmlTemplate(this.servlet.getTemplate("search/simple"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("SimpleResponse");
	}

	@Override
	public SimpleResponse duplicate() {
		return new SimpleResponse("search");
	}

}
