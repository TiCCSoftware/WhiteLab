package com.uvt.whitelab.response.explore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.FieldDescriptor;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.SessionManager;

public class NgramsResponse extends BaseResponse {
	
	public NgramsResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		loadPosValues();
		int nsize = this.getParameter("size", 5);
		if (query == null)
			loadMetaDataComponents(false);
		else {
			ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
			
			query.resetStatus();
			query = resultHandler.executeQuery(query,"/hits");
			
			loadMetaDataComponents(true);
			this.getContext().put("query", query);
			this.getContext().put("isStillCounting", SessionManager.isStillCounting(session));
		}

		this.getContext().put("size", nsize);
		this.getContext().put("showMetaOptions", "no");
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/ngrams"));
	}

	private void loadPosValues() {
		LinkedList<FieldDescriptor> fields = this.servlet.getSearchFields();
		
		List<String> options = new ArrayList<String>();
		options.add("<option value=\"\">&lt;any&gt;</option>");
		
		for (FieldDescriptor field : fields) {
			if (field.getSearchField().equals("pos")) {
				for (int i = 1; i <= 12; i++) {
					options.add("<option value=\""+labels.getString("pos."+i+".value")+"\">"+labels.getString("pos."+i+".name")+"</option>");
				}
			}
		}
		
		this.getContext().put("posValues", "<select class=\"input pos\">"+StringUtils.join(options.toArray(),"")+"</select>");
	}

	@Override
	protected void logRequest() {
		this.servlet.log("NgramsResponse");
	}

	@Override
	public NgramsResponse duplicate() {
		return new NgramsResponse("explore");
	}

}
