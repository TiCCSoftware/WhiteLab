package com.uvt.whitelab.response.search;

import java.util.ArrayList;
import java.util.LinkedList;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.FieldDescriptor;
import com.uvt.whitelab.util.FieldDescriptor.ValuePair;

public class ExtendedResponse extends BaseResponse {

	@Override
	protected void completeRequest() {
		loadProperties();
		loadMetaDataComponents();
		
		this.getContext().put("showMetaOptions", "yes");
		this.displayHtmlTemplate(this.servlet.getTemplate("search/extended"));
	}

	private void loadProperties() {
		LinkedList<FieldDescriptor> fields = this.servlet.getSearchFields();
		LinkedList<FieldDescriptor> props = new LinkedList<FieldDescriptor>();
		
		for (FieldDescriptor field : fields) {
			if (field.getSearchField().equals("pos")) {
				FieldDescriptor newField = field;
				newField.setValidValues(new ArrayList<ValuePair>());
				for (int i = 1; i <= 12; i++) {
					newField.addValidValue(labels.getString("pos."+i+".value"),labels.getString("pos."+i+".name"));
				}
				props.add(newField);
			} else {
				props.add(field);
			}
		}
		
		this.getContext().put("properties", props);
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ExtendedResponse");
	}

	@Override
	public ExtendedResponse duplicate() {
		return new ExtendedResponse();
	}

}
