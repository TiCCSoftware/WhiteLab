package com.uvt.whitelab.response.explore;

import com.uvt.whitelab.BaseResponse;

public class ExploreDocumentResponse extends BaseResponse {
	
	public ExploreDocumentResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		this.getContext().put("doctab", this.getParameter("tab", ""));
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/document"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ExploreDocumentResponse");
	}

	@Override
	public ExploreDocumentResponse duplicate() {
		return new ExploreDocumentResponse("explore");
	}

}
