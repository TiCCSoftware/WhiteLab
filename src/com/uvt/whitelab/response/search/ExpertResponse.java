package com.uvt.whitelab.response.search;

import com.uvt.whitelab.BaseResponse;

public class ExpertResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		loadMetaDataComponents();
		loadCQLInfoBox();
		
		this.getContext().put("showMetaOptions", "yes");
		this.displayHtmlTemplate(this.servlet.getTemplate("search/expert"));
	}

	private void loadCQLInfoBox() {
		String box = "<div id=\"cql_info\">"
				+ "<label>"+this.labels.getString("cql.info.header")+"</label>"
				+ "<ul class=\"examples\">";
		
		int i = 1;
		while (this.labels.containsKey("cql.info.query."+i+".text")) {
			box = box+"<li><label>"+this.labels.getString("cql.info.query."+i+".text")+"</label>";
			int j = 1;
			while (this.labels.containsKey("cql.info.query."+i+".code."+j)) {
				box = box+"<span class=\"cql\">"+this.labels.getString("cql.info.query."+i+".code."+j)+"</span>";
				j++;
			}
			box = box+"</li>";
			i++;
		}
		
		box = box+"</ul></div>";
		this.getContext().put("cqlinfo", box);
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ExpertResponse");
	}

	@Override
	public ExpertResponse duplicate() {
		return new ExpertResponse();
	}

}
