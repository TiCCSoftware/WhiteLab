package com.uvt.whitelab.response.explore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.ResultHandler;
import com.uvt.whitelab.util.WhitelabDocument;

public class ExploreDocumentResponse extends BaseResponse {
	private SecureRandom random = new SecureRandom();
	
	public ExploreDocumentResponse(String ns) {
		super(ns);
	}

	@Override
	protected void completeRequest() {
		
		String corpus = this.labels.getString("corpus");
		String tab = this.getParameter("tab", "text");
		String export = this.getParameter("export", "false");
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
						if (export.equals("true")) {
							String fileName = corpus + "-" + new BigInteger(130, random).toString(32) + ".tsv";
							sendFileResponse(document.getGrowthDataString(this.lang, "csv", false, 0,0,0), fileName);
						} else {
							this.getContext().put("growthExportUrl", query.getUrl("explore/document", "&tab=statistics&growth&export=true", false, new String[] {}));
							this.getContext().put("data", document.getGrowthDataString(this.lang, "json", false, 0,0,0));
							this.getContext().put("statType", "growth");
						}
					} else if (this.request.getParameterMap().containsKey("posdata")) {
						if (export.equals("true")) {
							String charttype = this.getParameter("chart", "freqlist");
							String fileName = corpus + "-" + new BigInteger(130, random).toString(32) + ".tsv";
							if (charttype.equals("freqlist")) {
								sendFileResponse(document.getPosFreqList(this.getParameter("pos", "ADJ"), "csv"), fileName);
							} else if (charttype.equals("histogram")) {
								sendFileResponse(document.getPosHistogram(this.getParameter("pos", "ADJ"), "csv"), fileName);
							}
						} else {
							this.getContext().put("freqlistExportUrl", query.getUrl("explore/document", "&tab=statistics&posdata&chart=freqlist&export=true", false, new String[] {}));
							this.getContext().put("histogramExportUrl", query.getUrl("explore/document", "&tab=statistics&posdata&chart=histogram&export=true", false, new String[] {}));
							this.getContext().put("freqlist", document.getPosFreqList(this.getParameter("pos", "ADJ"), "json"));
							this.getContext().put("histogram", document.getPosHistogram(this.getParameter("pos", "ADJ"), "json"));
							this.getContext().put("statType", "posdata");
							this.getContext().put("posSelected", this.getParameter("pos", "ADJ"));
							this.getContext().put("posLabels", loadPosLabels());
							this.getContext().put("posColors", loadPosColors());
						}
					} else if (this.request.getParameterMap().containsKey("pospie")) {
						if (export.equals("true")) {
							String charttype = this.getParameter("chart", "token");
							String fileName = corpus + "-" + new BigInteger(130, random).toString(32) + ".tsv";
							if (charttype.equals("token")) {
								sendFileResponse((String) document.getPosTotals("token", "csv"), fileName);
							} else if (charttype.equals("lemma")) {
								sendFileResponse((String) document.getPosTotals("lemma", "csv"), fileName);
							}
						} else {
							this.getContext().put("tokenExportUrl", query.getUrl("explore/document", "&tab=statistics&pospie&chart=token&export=true", false, new String[] {}));
							this.getContext().put("lemmaExportUrl", query.getUrl("explore/document", "&tab=statistics&pospie&chart=lemma&export=true", false, new String[] {}));
							this.getContext().put("tokenpie", document.getPosTotals("token", "json"));
							this.getContext().put("lemmapie", document.getPosTotals("lemma", "json"));
						}
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
