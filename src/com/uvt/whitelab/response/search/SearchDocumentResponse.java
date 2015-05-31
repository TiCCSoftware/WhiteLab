package com.uvt.whitelab.response.search;

import com.uvt.whitelab.BaseResponse;

public class SearchDocumentResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		this.getContext().put("doctab", this.getParameter("tab", "text"));
		this.displayHtmlTemplate(this.servlet.getTemplate("search/document"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("SearchDocumentResponse");
	}

	@Override
	public SearchDocumentResponse duplicate() {
		return new SearchDocumentResponse();
	}

}
