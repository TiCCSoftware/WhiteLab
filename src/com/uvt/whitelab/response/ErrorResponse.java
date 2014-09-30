/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import com.uvt.whitelab.BaseResponse;

public class ErrorResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		this.displayHtmlTemplate(this.servlet.getTemplate("error"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ErrorResponse");
	}

	@Override
	public ErrorResponse duplicate() {
		return new ErrorResponse();
	}

}
