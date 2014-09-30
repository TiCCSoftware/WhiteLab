package com.uvt.whitelab.response;

import com.uvt.whitelab.BaseResponse;

public class CollectionResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void logRequest() {
		this.servlet.log("CollectionResponse");
	}

	@Override
	public CollectionResponse duplicate() {
		return new CollectionResponse();
	}

}
