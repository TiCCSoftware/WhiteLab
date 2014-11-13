/**
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology.
 * All rights reserved.
 *
 * @author VGeirnaert
 */
package com.uvt.whitelab.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;

import com.ximpleware.AutoPilot;
import com.ximpleware.ModifyException;
import com.ximpleware.NavException;
import com.ximpleware.TranscodeException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import com.ximpleware.ParseException;

/**
 *
 */
public class XslTransformer {

	private TransformerFactory tFactory;
	private Map<String, String> params = new HashMap<String, String>();

	public XslTransformer() {
		tFactory = TransformerFactory.newInstance();
	}

	public String transform(String source, String stylesheet) throws TransformerException {
		StreamSource ssSource = new StreamSource(new StringReader(source));
		StreamSource ssStylesheet = new StreamSource(new StringReader(stylesheet));
		StringWriter result = new StringWriter();
		StreamResult streamResult = new StreamResult(result);

		Transformer optimusPrime = tFactory.newTransformer(ssStylesheet);

		for(String key : params.keySet())
			optimusPrime.setParameter(key, params.get(key));

		optimusPrime.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		optimusPrime.transform(ssSource, streamResult);

		return result.toString();
	}

	public String transformArticle(String source, String stylesheet, Integer startPar, Integer endPar) throws TransformerException {
		int windowSize = getWindowSize(source);
		if (startPar == -1) {
			startPar = 1;
		}
		if (endPar == -1) {
			endPar = windowSize;
		}
		if (startPar > endPar) {
			startPar = 1;
			endPar = windowSize;
		}
		String subSource = splitArticle(source,startPar,endPar,windowSize);
		//System.out.println(subSource);
		//return subSource;
		StreamSource ssSource = new StreamSource(new StringReader(subSource));
		StreamSource ssStylesheet = new StreamSource(new StringReader(stylesheet));
		StringWriter result = new StringWriter();
		StreamResult streamResult = new StreamResult(result);

		Transformer optimusPrime = tFactory.newTransformer(ssStylesheet);
		optimusPrime.setParameter("window",Integer.toString(windowSize));
		for(String key : params.keySet())
			optimusPrime.setParameter(key, params.get(key));

		optimusPrime.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		optimusPrime.transform(ssSource, streamResult);
		
		return result.toString();

	}
	
	public Integer getParCount(String source) {
		int parCount = StringUtils.countMatches(source, "<p ");
		if (parCount == 0) {
			parCount = StringUtils.countMatches(source, "<event ");
		}
		int headCount = StringUtils.countMatches(source, "<head");
		parCount = parCount + headCount; // plus one for the head paragraph
		return parCount;
	}
	
	public Integer getWordCount(String source) {
		int wordCount = StringUtils.countMatches(source, "<w ");
		return wordCount;
	}
	
	public Integer getAvgParagraphSize(String source) {
		Integer parCount = getParCount(source);
		int avgParagraphSize = 0;
		if (parCount > 0)
			avgParagraphSize = (int)(Math.ceil(getWordCount(source) / parCount));
		return avgParagraphSize;
	}
	
	public Integer getWindowSize(String source) {
		int avgParagraphSize = getAvgParagraphSize(source);
		int windowSize;
		if (avgParagraphSize > 100) {
			windowSize = 50;
		} else if (avgParagraphSize > 50) {
			windowSize = 100;
		} else {
			windowSize = 500;
		}
		return windowSize;
	}
	
	public String splitArticle(String source, Integer startPar, Integer endPar, Integer windowSize) {
		if (windowSize == null) {
			windowSize = getWindowSize(source);
		}
		int parCount = getParCount(source);
		if (endPar > parCount) {
			endPar = parCount;
		}
		int pages;
		int currentPage;
		if (parCount <= windowSize) {
			pages = 1;
			currentPage = 1;
		} else {
			pages = (int)(Math.ceil(parCount / windowSize));
			currentPage = (int)(Math.ceil(endPar / windowSize));
		}
		
		int pCount = 0;
		
		byte[] bytes = null;
		try {
			bytes = source.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		if (bytes != null) {
			VTDGen vg = new VTDGen();
			vg.setDoc(bytes);
	        XMLModifier xm;
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			try {
		        vg.parse(true);
				VTDNav vn = vg.getNav();
	            AutoPilot ap = new AutoPilot(vn);
				xm = new XMLModifier(vn);
				
				vn.toElement(VTDNav.FIRST_CHILD);
                xm.insertAfterElement("\n<DocumentFields><StartPar>"+Integer.toString(startPar)+"</StartPar>\n<EndPar>"+Integer.toString(endPar)+"</EndPar>\n<ParCount>"+Integer.toString(parCount)+"</ParCount>\n<TotalPages>"+Integer.toString(pages)+"</TotalPages>\n<CurrentPage>"+Integer.toString(currentPage)+"</CurrentPage><WindowSize>500</WindowSize></DocumentFields>");
                
                ap.declareXPathNameSpace("folia","http://ilk.uvt.nl/folia");
	            ap.selectXPath("//folia:head | //folia:p | //folia:event");
	            @SuppressWarnings("unused")
				int i;
	            while ((i = ap.evalXPath()) != -1) {
		        	pCount = pCount + 1;
	
		        	if (pCount < startPar || pCount > endPar) {
		        		xm.remove();
		        	}
		        }
	            ap.resetXPath();

		        xm.output(os);
		        String aString = new String(os.toByteArray(), "UTF-8");
//		        System.out.println("Article XML: "+aString);
		        return aString;
			} catch (ModifyException | XPathParseException | XPathEvalException | NavException | IOException | TranscodeException | ParseException e) {
				e.printStackTrace();
	        }
		}
        
		return "";
	}

	public void addParameter(String key, String value) {
		params.put(key, value);
	}

	public void clearParameters() {
		params.clear();
	}

}