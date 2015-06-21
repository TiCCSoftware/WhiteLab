package com.uvt.whitelab.response.explore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.MetadataField;

public class CorpusResponse extends BaseResponse {
	
	public CorpusResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		loadTreemapOptions();

		this.getContext().put("field", "CollectionName");
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/corpus"));
	}

	@Override
	protected void logRequest() {
		this.servlet.log("CorpusResponse");
	}

	@Override
	public CorpusResponse duplicate() {
		return new CorpusResponse("explore");
	}
	
	private void loadTreemapOptions() {
		List<String> options = new ArrayList<String>();
		int i = 0;
		
		for (MetadataField dataField : this.servlet.getMetadataFields()) {
			String fieldName = dataField.getName();
			if (this.labels.containsKey("metadataFields."+fieldName))
				fieldName = this.labels.getString("metadataFields."+fieldName);
			int total = dataField.getTotal();
			int subtotal = dataField.getSubTotal();
			if (subtotal > total / 2) {
				i++;
				String checked = "";
				if (i == 1)
					checked = "checked";
				options.add("<label class=\"treemap-option\"><input type=\"radio\" name=\"field\" value=\""+dataField.getName()+"\" "+checked+"/> &nbsp;"+fieldName+"</label>");
			}
		}
		
		this.getContext().put("treemapOptions", StringUtils.join(options.toArray(),"\n"));
	}

}
