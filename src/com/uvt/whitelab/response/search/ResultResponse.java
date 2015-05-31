package com.uvt.whitelab.response.search;

import com.uvt.whitelab.BaseResponse;

public class ResultResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		this.getContext().put("from", this.getParameter("from",0));
		this.displayHtmlTemplate(this.servlet.getTemplate("search/results"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ResultResponse");
	}

	@Override
	public ResultResponse duplicate() {
		return new ResultResponse();
	}

}
