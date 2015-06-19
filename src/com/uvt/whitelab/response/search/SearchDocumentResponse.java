package com.uvt.whitelab.response.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.WhitelabDocument;
import com.uvt.whitelab.util.XslTransformer;

public class SearchDocumentResponse extends BaseResponse {
	private XslTransformer transformer = new XslTransformer();

	@Override
	protected void completeRequest() {

		String tab = this.getParameter("tab", "text");
		if (query != null) {
			WhitelabDocument document = query.getDocument();
			if (document != null) {
				if (document.getXml().length() == 0 || query.getStart() != document.start) {
					loadDocument(document);
				}
				if (tab.equals("text")) {
					this.getContext().put("content", document.getContent());
				} else if (tab.equals("metadata")) {
					this.getContext().put("content", document.getMetadata());
				} else if (tab.equals("statistics")) {
					if (this.request.getParameterMap().containsKey("growth")) {
						this.getContext().put("data", document.getGrowthData(this.lang, "json", false));
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
		this.getContext().put("query", query);
		this.displayHtmlTemplate(this.servlet.getTemplate("search/document"));
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

	private void loadDocument(WhitelabDocument document) {
		Map<String,Object> params = query.getParameters();

		String response = getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+document.getId()+"/contents", params);

		try {
			setTransformerDisplayParameters(document.getId());
			String documentStylesheet = loadStylesheet("article_folia.xsl");
			String htmlResult = transformer.transformArticle(response, documentStylesheet, query.getStart(), query.getEnd());
			document.setContent(htmlResult);
			document.setXml(response);
			document.start = query.getStart();
			document.end = query.getEnd();
			document.count();
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
		
		String meta = getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+document.getId(), params);
		
		try {
			setTransformerDisplayParameters(document.getId());
			String metadataStylesheet = loadStylesheet("article_metadata.xsl");
			String metaResult = transformer.transform(meta, metadataStylesheet);
			document.setMetadata(metaResult);
			document.setMetaXml(meta);
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void logRequest() {
		this.servlet.log("SearchDocumentResponse");
	}

	@Override
	public SearchDocumentResponse duplicate() {
		return new SearchDocumentResponse();
	}
	
	private void setTransformerDisplayParameters(String docPid) {
		transformer.clearParameters();
		String query = this.getParameter("query", "");
		transformer.addParameter("query", query);
		if (query.length() == 0)
			transformer.addParameter("whitelab_page", "explore");
		else
			transformer.addParameter("whitelab_page", "search");
		transformer.addParameter("doc_id", docPid);
		transformer.addParameter("lang", this.lang);
		
		transformer.addParameter("title_name", this.labels.getString("document.meta.field.title"));
		transformer.addParameter("author_name", this.labels.getString("document.meta.field.author"));
		transformer.addParameter("description_name", this.labels.getString("document.meta.field.description"));
		transformer.addParameter("document_id_name", this.labels.getString("document.meta.field.docid"));
		transformer.addParameter("texttype_name", this.labels.getString("document.meta.field.texttype"));
		transformer.addParameter("collection_name", this.labels.getString("document.meta.field.collection"));
		transformer.addParameter("license_code_name", this.labels.getString("document.meta.field.licensecode"));
		transformer.addParameter("license_date_name", this.labels.getString("document.meta.field.licensedate"));
		transformer.addParameter("country_name", this.labels.getString("document.meta.field.country"));
		transformer.addParameter("continent_name", this.labels.getString("document.meta.field.continent"));
		transformer.addParameter("language_name", this.labels.getString("document.meta.field.language"));
		
		transformer.addParameter("by",this.labels.getString("result.by"));
		transformer.addParameter("document_id", this.labels.getString("document.meta.docid"));
		transformer.addParameter("texttype", this.labels.getString("document.meta.texttype"));
		transformer.addParameter("collection", this.labels.getString("document.meta.collection"));
		transformer.addParameter("license_code", this.labels.getString("document.meta.licensecode"));
		transformer.addParameter("license_date", this.labels.getString("document.meta.licensedate"));
		transformer.addParameter("country", this.labels.getString("document.meta.country"));
		transformer.addParameter("continent", this.labels.getString("document.meta.continent"));
		transformer.addParameter("language", this.labels.getString("document.meta.language"));
		
	}

}
