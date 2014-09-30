/**
 * Copyright (c) 2013, 2014 Tilburg University.
 * All rights reserved.
 *
 * @author MvdCamp
 */
package com.uvt.whitelab.response;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.xml.transform.TransformerException;

import com.uvt.whitelab.BaseResponse;
import com.uvt.whitelab.util.XslTransformer;

public class ExportResponse extends BaseResponse {

	private static final int BUFSIZE = 4096;
	private String corpus;
	private String trail = "/hits";
	private Integer view = 1;
	private XslTransformer transformer = new XslTransformer();

	@Override
	protected void completeRequest() {
		
		corpus = this.labels.getString("corpus");
		
		if (this.params.keySet().size() > 0 && params.containsKey("patt")) {
			
			view = this.getParameter("view", 1);
			int id = this.getParameter("id", 1);
			int number = this.getParameter("number", 1);
			this.servlet.log("number: "+number);

			if (view == 2 || view == 4 || view == 16)
				trail = "/docs";
			
			String filePath = this.servlet.getRealPath()+"WEB-INF/tmp/"+id+"_view"+view+".tsv";
			File file = new File(filePath);
			if (!file.exists()) {
				String result = this.jobToTSV();
				this.saveDataToFile(result,filePath);
			}
			
			sendFileResponse(filePath);
			
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
		try {
			String resp = getBlackLabResponse(corpus, trail, this.params);
			String stylesheet = this.getExportStylesheet(view);
			this.setTransformerDisplayParameters();
			String result = transformer.transform(resp, stylesheet);
			return result;
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private void setTransformerDisplayParameters() throws UnsupportedEncodingException {
		this.servlet.log("setTransformerDisplayParameters");
		transformer.clearParameters();
		transformer.addParameter("urlparamwithoutstart", this.lastUrl + "?" + getParameterStringExcept(new String[] {"first"}));
		transformer.addParameter("urlparamwithoutvieworgroup", this.lastUrl + "?" + getParameterStringExcept(new String[] {"view", "groupBy"}));
		transformer.addParameter("urlparamwithoutsort", this.lastUrl + "?" + getParameterStringExcept(new String[] {"sortBy"}));
		if (this.params.containsKey("patt")) {
			transformer.addParameter("urlparamquery", URLEncoder.encode((String) this.params.get("patt"), "UTF-8"));
			transformer.addParameter("query", (String) this.params.get("patt"));
		}
		transformer.addParameter("baseurl", this.labels.getString("baseUrl"));
		transformer.addParameter("webserviceurl", this.labels.getString("baseUrl") + "/" + this.labels.getString("corpus"));
		transformer.addParameter("backendRequestUrl", this.lastUrl + "?" + getParameterStringExcept(new String[] {}));

		transformer.addParameter("query_id",this.getParameter("id", "1"));
		transformer.addParameter("per_hit",this.labels.getString("result.per_hit"));
		transformer.addParameter("per_doc",this.labels.getString("result.per_doc"));
		transformer.addParameter("grouped_per_hit",this.labels.getString("result.grouped_per_hit"));
		transformer.addParameter("grouped_per_doc",this.labels.getString("result.grouped_per_doc"));
		transformer.addParameter("context_left",this.labels.getString("context") + " " + this.labels.getString("left"));
		transformer.addParameter("context_right",this.labels.getString("context") + " " + this.labels.getString("right"));
		transformer.addParameter("word",this.labels.getString("word"));
		transformer.addParameter("lemma",this.labels.getString("lemma"));
		transformer.addParameter("pos",this.labels.getString("pos"));
		transformer.addParameter("result_export",this.labels.getString("result.export"));
		transformer.addParameter("result_pagination_show",this.labels.getString("result.pagination.show"));
		transformer.addParameter("by",this.labels.getString("result.by"));
		transformer.addParameter("max", this.getParameter("number", "50"));
		
		String groupBy_name = this.getParameter("groupBy", "");
		transformer.addParameter("groupBy_name", groupBy_name);
		groupBy_name = groupBy_name.replaceAll("field:", "");
		transformer.addParameter("groupBy_name_clean", groupBy_name);

		transformer.addParameter("collection_name", this.labels.getString("result.collection_name"));
		transformer.addParameter("title_name", this.labels.getString("result.title_name"));
		transformer.addParameter("author_name", this.labels.getString("result.author_name"));
		transformer.addParameter("date_name", this.labels.getString("result.date_name"));
		transformer.addParameter("lemma_name", this.labels.getString("result.lemma_name"));
		transformer.addParameter("pos_name", this.labels.getString("result.pos_name"));
		
	}
	
	private String getExportStylesheet(int view) throws IOException {
		if (view == 1)
			return loadStylesheet("tsvexport_perhitresults.xsl");
		else if (view == 2)
			return loadStylesheet("tsvexport_perdocresults.xsl");
		else if (view == 4)
			return loadStylesheet("tsvexport_perdocstatsresults.xsl");
		else if (view == 8)
			return loadStylesheet("tsvexport_groupperhitresults.xsl");
		else if (view == 10)
			return loadStylesheet("tsvexport_groupperhitngramresults.xsl");
		else if (view == 12)
			return loadStylesheet("tsvexport_groupperhitstatsresults.xsl");
		else if (view == 16)
			return loadStylesheet("tsvexport_groupperdocresults.xsl");
		
		return null;
	}

	private String saveDataToFile(String data, String filePath) {
		try {
			PrintWriter out = new PrintWriter(filePath);
			out.println(data);
			out.close();
			return filePath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendFileResponse(String filePath) {
		File file = new File(filePath);
        int length   = 0;
        ServletOutputStream outStream = null;
		try {
			outStream = response.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		response.setContentType("application/octet-stream");
        response.setContentLength((int)file.length());
        String fileName = (new File(filePath)).getName();
        
        // sets HTTP header
        response.setHeader("Content-Disposition", "attachment; filename=\"whitelab_" + fileName + "\"");
        
		try {
	        byte[] byteBuffer = new byte[BUFSIZE];
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			// reads the file's bytes and writes them to the response stream
	        while ((in != null) && ((length = in.read(byteBuffer)) != -1))
	        {
	            outStream.write(byteBuffer,0,length);
	        }
	        
	        in.close();
	        outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
