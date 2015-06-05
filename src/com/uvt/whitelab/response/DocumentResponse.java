/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONException;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.WhitelabDocument;
import com.uvt.whitelab.util.XslTransformer;

public class DocumentResponse extends BaseResponse {
	private XslTransformer transformer = new XslTransformer();
	private String queryType = null;
	private String subType = null;
	private Integer max = -1;
	private String format = "json";
	private WhitelabDocument document = null;

	@Override
	protected void completeRequest() {
		
		if (queryType == null)
			this.setQueryType();
		
		if (this.document == null)
			this.document = this.loadDocumentById(this.getParameter("docpid", ""));
		
		if (this.document == null) {
			Map<String,Object> outparams = new HashMap<String,Object>();
			outparams.put("error", "Document not found.");
			sendResponse(outparams);
		} else {
			processDocument(this.document);
		}
	}
	
	private void processDocument(WhitelabDocument document) {
		Map<String,Object> outparams = new HashMap<String,Object>();
		
		switch(queryType) {
			case "content":
				outparams.put("data", document.getContent());
				break;
			case "stats":
				outparams.put("data", document.getDocStats(this.lang));
				if (this.lang.equals("en"))
					outparams.put("title", "Document statistics");
				else
					outparams.put("title", "Document statistieken");
				break;
			case "growth":
				if (subType.equals("bare"))
					try {
						outparams.put("data", new JSONArray(document.getGrowthData(this.lang, format, true)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				else
					outparams.put("data", document.getGrowthData(this.lang, format, false));
				if (this.lang.equals("en"))
					outparams.put("title", "Vocabulary Growth Curve");
				else
					outparams.put("title", "Vocabulaire Groei Curve");
				sendResponse(outparams);
				break;
			case "cloud":
				outparams.put("data", document.getCloudData());
				break;
			case "meta":
				outparams.put("data", document.getMetadata());
				break;
			case "freqlist":
				outparams.put("data", document.getPosFreqList(subType, max, format));
				break;
			case "histogram":
				outparams.put("data", document.getPosHistogram(subType, format));
				break;
			case "pospie":
				outparams.put("data", document.getPosTotals(subType,format));
				if (this.lang.equals("en")) {
					if (subType.equals("token")) {
						outparams.put("title", "Tokens / POS");
					} else {
						outparams.put("title", "Lemmas / POS");
					}
				} else {
					if (subType.equals("token")) {
						outparams.put("title", "Tokens / Woordsoort");
					} else {
						outparams.put("title", "Lemmas / Woordsoort");
					}
				}
				break;
			default:
				this.getContext().put("document", document);
				this.getContext().put("docId", document.getId());
				this.servlet.log("Set docId to: "+document.getId());
				this.getContext().put("article_content", document.getContent());
				String q = this.getParameter("query", "");
				this.servlet.log("QUERY: "+q);
				if (q.length() > 0) {
					this.getContext().put("query", q);
					this.getContext().put("whitelabPage", "search");
				} else
					this.getContext().put("whitelabPage", "explore");
				String html = this.applyHtmlTemplate(this.servlet.getTemplate("document"));
				outparams.put("data", html);
				break;

		}
		
		if (format.equals("json")) {
			sendResponse(outparams);
		} else {
			sendCsvResponse((String) outparams.get("data"));
		}
	}
	
	private void setQueryType() {
		
		if (this.request.getParameter("content") != null) {
			queryType = "content";
		} else if (this.request.getParameter("stats") != null) {
			queryType = "stats";
		} else if (this.request.getParameter("growth") != null) {
			queryType = "growth";
			subType = this.getParameter("growth","");
			format = this.getParameter("format","json");
		} else if (this.request.getParameter("cloud") != null) {
			queryType = "cloud";
		} else if (this.request.getParameter("meta") != null) {
			queryType = "meta";
		} else if (this.request.getParameter("freqlist") != null) {
			queryType = "freqlist";
			subType = this.request.getParameter("pos");
			max = Integer.parseInt(this.request.getParameter("max"));
			format = this.getParameter("format","json");
		} else if (this.request.getParameter("histogram") != null) {
			queryType = "histogram";
			subType = this.request.getParameter("pos");
			format = this.getParameter("format","json");
		} else if (this.request.getParameter("pospie") != null) {
			queryType = "pospie";
			subType = this.request.getParameter("pospie");
			format = this.getParameter("format","json");
		} else {
			queryType = "";
		}
		
	}

	private WhitelabDocument loadDocumentById(String docPid) {
		WhitelabDocument document = null;
		int start = this.getParameter("start", -1);
		int end = this.getParameter("end", -1);
		
		if (start > -1 && end == -1)
			end = start + 499;
		
		if (docPid.length() == 0) {
			this.servlet.log("No docPid given!");
		} else {
			if (this.servlet.hasDocument(docPid,this.lang)) {
				this.servlet.log("Document exists");
				document = this.servlet.getDocument(docPid,this.lang);
			} else {
				document = new WhitelabDocument();
				document.setId(docPid);
				this.servlet.addDocument(document,this.lang);
			}
			
			if (document.getContent().length() == 0 || start != document.start) {
				Map<String,Object> params = query.getParameters();
				
				if (this.getParameter("type", "").equals("explore") && params.containsKey("patt"))
					params.remove("patt");
			
				setTransformerDisplayParameters(docPid);

				String response = getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+docPid+"/contents", params);

				try {
					String documentStylesheet = loadStylesheet("article_folia.xsl");
					String htmlResult = transformer.transformArticle(response, documentStylesheet, start, end);
					document.setContent(htmlResult);
					document.setXml(response);
					document.start = start;
					document.end = end;
				} catch (IOException | TransformerException e) {
					e.printStackTrace();
				}
				
				if (document.getMetadata().length() == 0) {
					String meta = getBlackLabResponse(this.labels.getString("corpus"), "/docs/"+docPid, params);
					this.servlet.log("Meta response: \n"+meta);
					try {
						String metadataStylesheet = loadStylesheet("article_metadata.xsl");
						String metaResult = transformer.transform(meta, metadataStylesheet);
						this.servlet.log("Meta result: \n"+meta);
						document.setMetadata(metaResult);
						document.setMetaXml(meta);
					} catch (IOException | TransformerException e) {
						e.printStackTrace();
					}
					document.count();
				}
			
			}
		}
		
		return document;
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

	@Override
	protected void logRequest() {
		if (queryType == null)
			this.setQueryType();
		
		if (queryType.length() > 0)
			this.servlet.log("DocumentResponse "+queryType+"="+this.getParameter(queryType, ""));
		else
			this.servlet.log("DocumentResponse");
	}

	@Override
	public DocumentResponse duplicate() {
		return new DocumentResponse();
	}

}
