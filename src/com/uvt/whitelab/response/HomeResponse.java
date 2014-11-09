/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import com.uvt.whitelab.BaseResponse;

public class HomeResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		this.getContext().put("maintab", "home");
		this.displayHtmlTemplate(this.servlet.getTemplate("home"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("HomeResponse");
	}

	@Override
	public HomeResponse duplicate() {
		return new HomeResponse();
	}

}
