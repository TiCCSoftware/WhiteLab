package com.uvt.whitelab.response.explore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.WhitelabDocument;

public class ExploreDocumentResponse extends BaseResponse {
	
	public ExploreDocumentResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		String tab = this.getParameter("tab", "text");
		String docPid = this.getParameter("docpid", "");
		if (docPid.length() > 0) {
			WhitelabDocument document = new WhitelabDocument(docPid);
			if (document != null) {
				if (document.getXml().length() == 0) {
					ResultHandler resultHandler = new ResultHandler(this.servlet, labels);
					resultHandler.loadDocument(document, this.lang, this.getParameter("start", -1), this.getParameter("end", -1));
				}
				if (tab.equals("text")) {
					this.getContext().put("content", document.getContent());
				} else if (tab.equals("metadata")) {
					this.getContext().put("content", document.getMetadata());
				} else if (tab.equals("statistics")) {
					if (this.request.getParameterMap().containsKey("growth")) {
						this.getContext().put("data", document.getGrowthDataString(this.lang, "json", false, 0,0,0));
						this.getContext().put("statType", "growth");
					} else if (this.request.getParameterMap().containsKey("posdata")) {
						this.getContext().put("freqlist", document.getPosFreqList(this.getParameter("pos", "ADJ"), "json"));
						this.getContext().put("histogram", document.getPosHistogram(this.getParameter("pos", "ADJ"), "json"));
						this.getContext().put("statType", "posdata");
						this.getContext().put("posSelected", this.getParameter("pos", "ADJ"));
						this.getContext().put("posLabels", loadPosLabels());
						this.getContext().put("posColors", loadPosColors());
					} else if (this.request.getParameterMap().containsKey("pospie")) {
						this.getContext().put("tokenpie", document.getPosTotals("token", "json"));
						this.getContext().put("lemmapie", document.getPosTotals("lemma", "json"));
						this.getContext().put("statType", "pospie");
					} else {
						this.getContext().put("data", document.getDocStats(this.lang));
					}
				} else if (tab.equals("wordcloud")) {
					this.getContext().put("data", document.getCloudData());
				}
				this.getContext().put("document", document);
			}
		}
		
		this.getContext().put("doctab", tab);
		this.displayHtmlTemplate(this.servlet.getTemplate("explore/document"));
	}
	
	private List<String> loadPosLabels() {
		List<String> posLabels = new ArrayList<String>();
		posLabels.add("ADJ");
		posLabels.add("BW");
		posLabels.add("LET");
		posLabels.add("LID");
		posLabels.add("N");
		posLabels.add("SPEC");
		posLabels.add("TSW");
		posLabels.add("TW");
		posLabels.add("VG");
		posLabels.add("VNW");
		posLabels.add("VZ");
		posLabels.add("WW");
		return posLabels;
	}
	
	private Map<String,String> loadPosColors() {
		Map<String,String> posColors = new HashMap<String,String>();
		posColors.put("ADJ","#272727");
		posColors.put("BW","#ab0000");
		posColors.put("LET","#0056e1");
		posColors.put("LID","#00abab");
		posColors.put("N","#353593");
		posColors.put("SPEC","#faae0f");
		posColors.put("TSW","#555555");
		posColors.put("TW","#e06666");
		posColors.put("VG","#0088ee");
		posColors.put("VNW","#66e0e0");
		posColors.put("VZ","#6666bb");
		posColors.put("WW","#fde281");
		return posColors;
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ExploreDocumentResponse");
	}

	@Override
	public ExploreDocumentResponse duplicate() {
		return new ExploreDocumentResponse("explore");
	}

}
