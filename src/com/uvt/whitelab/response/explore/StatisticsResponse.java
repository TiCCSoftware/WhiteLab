package com.uvt.whitelab.response.explore;

import com.uvt.whitelab.BaseResponse;

public class StatisticsResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		loadMetaDataComponents();

		this.getContext().put("showMetaOptions", "no");
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/statistics"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("StatisticsResponse");
	}

	@Override
	public StatisticsResponse duplicate() {
		return new StatisticsResponse();
	}

}
