/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.xml.transform.TransformerException;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.XslTransformer;

public class ExportResponse extends BaseResponse {

	private String corpus;
	private String trail = "/hits";
	private Integer view = 1;
	private XslTransformer transformer = new XslTransformer();
	private SecureRandom random = new SecureRandom();

	@Override
	protected void completeRequest() {
		
		corpus = this.labels.getString("corpus");
		
		if (this.params.keySet().size() > 0 && params.containsKey("patt")) {
			
			view = this.getParameter("view", 1);
			int number = this.getParameter("number", 1);
			this.servlet.log("number: "+number);

			if (view == 2 || view == 4 || view == 16)
				trail = "/docs";
			
			String fileName = corpus + "-" + new BigInteger(130, random).toString(32) + ".tsv";
			
			String result = this.jobToTSV();
			
			sendFileResponse(result, fileName);
			
		} else {
			Map<String,Object> output = new HashMap<String,Object>();
			output.put("html", "<p>ERROR: Insufficient parameters.</p>");
			output.put("hits", 0);
			output.put("docs", 0);
			output.put("counting", "false");
			sendResponse(output);
		}
	}

	public String jobToTSV() {
		StringBuilder result = new StringBuilder();
		if (view == 1 || view == 2 || view == 4 || view == 10 || view == 12) {
			int n = (int) this.params.get("number");
			if (n > 50000)
				n = 50000;
			this.params.put("number", 2500);
			
			for (int f = 0; f < n; f = f + 2500) {
				this.params.put("first", f);
				try {
					String resp = getBlackLabResponse(corpus, trail, this.params);
					String stylesheet = this.getExportStylesheet(view);
					this.setTransformerDisplayParameters(f == 0,n);
					result.append(transformer.transform(resp, stylesheet));
				} catch (IOException | TransformerException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				String resp = getBlackLabResponse(corpus, trail, this.params);
				String stylesheet = this.getExportStylesheet(view);
				this.setTransformerDisplayParameters(true,(int) this.params.get("number"));
				result.append(transformer.transform(resp, stylesheet));
			} catch (IOException | TransformerException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	private void setTransformerDisplayParameters(boolean includeHeader, int n) {
		this.servlet.log("setTransformerDisplayParameters");
		transformer.clearParameters();
		transformer.addParameter("include_header", String.valueOf(includeHeader));
		transformer.addParameter("n", String.valueOf(n));
		transformer.addParameter("query", this.labels.getString("result.query"));
		transformer.addParameter("filter", this.labels.getString("result.filters"));
		transformer.addParameter("total_hits", this.labels.getString("result.export.totalhits"));
		transformer.addParameter("total_docs", this.labels.getString("result.export.totaldocs"));
		transformer.addParameter("total_groups", this.labels.getString("result.export.totalgroups"));
		transformer.addParameter("total_exported", this.labels.getString("result.export.totalexported"));
		transformer.addParameter("layout", this.labels.getString("result.export.layout"));
		transformer.addParameter("per_hit", this.labels.getString("result.per_hit"));
		transformer.addParameter("per_doc", this.labels.getString("result.per_doc"));
		transformer.addParameter("grouped_per_hit", this.labels.getString("result.grouped_per_hit"));
		transformer.addParameter("grouped_per_doc", this.labels.getString("result.grouped_per_doc"));
		transformer.addParameter("grouped_header",this.labels.getString("result.grouped_header"));
		transformer.addParameter("context_left", this.labels.getString("context")+" "+this.labels.getString("left"));
		transformer.addParameter("context_right", this.labels.getString("context")+" "+this.labels.getString("right"));
		transformer.addParameter("lemma", this.labels.getString("lemma"));
		transformer.addParameter("pos", this.labels.getString("pos"));
		transformer.addParameter("document_id", this.labels.getString("document.meta.docid"));
		transformer.addParameter("document_title", this.labels.getString("document.meta.doctitle"));
		transformer.addParameter("collection", this.labels.getString("document.meta.collection"));
		transformer.addParameter("hits", this.labels.getString("result.hits"));
		transformer.addParameter("docs", this.labels.getString("result.documents"));
		transformer.addParameter("pos_name", this.labels.getString("result.pos_name"));
		transformer.addParameter("title_name", this.labels.getString("document.meta.field.title"));
		transformer.addParameter("collection_name", this.labels.getString("document.meta.field.collection"));
		
	}
	
	private String getExportStylesheet(int view) throws IOException {
		if (view == 1)
			return loadStylesheet("tsvexport_perhitresults.xsl");
		else if (view == 2 || view == 4)
			return loadStylesheet("tsvexport_perdocresults.xsl");
		else if (view == 8 || view == 10 || view == 12)
			return loadStylesheet("tsvexport_groupperhitresults.xsl");
		else if (view == 16)
			return loadStylesheet("tsvexport_groupperdocresults.xsl");
		
		return null;
	}

	private void sendFileResponse(String contents, String fileName) {
        // Set HTTP headers
		response.setContentType("application/octet-stream");
        response.setContentLength(contents.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"whitelab_" + fileName + "\"");
        
        ServletOutputStream outStream = null;
		try {
			outStream = response.getOutputStream();
			try {
				outStream.write(contents.getBytes("utf-8"));
			} finally {
		        outStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void logRequest() {
		this.servlet.log("ExportResponse");
	}

	@Override
	public ExportResponse duplicate() {
		return new ExportResponse();
	}

}
