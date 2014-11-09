package com.uvt.whitelab.util;

public class StringCleaner {

	public final static String clean(String str) {
		String clean = str.replaceAll(",","COMMA");
		clean = clean.replaceAll("\\.","PERIOD");
		clean = clean.replaceAll("\\$","DOLLAR");
		clean = clean.replaceAll("'","APOS");
		clean = clean.replaceAll("\"","QUOTE");
		clean = clean.replaceAll("\\(","LRB");
		clean = clean.replaceAll("\\)","RRB");
		clean = clean.replaceAll("\\[","LSB");
		clean = clean.replaceAll("\\]","RSB");
		clean = clean.replaceAll("\\:","COL");
		clean = clean.replaceAll("\\;","SEM");
		clean = clean.replaceAll("\\/","SLASH");
		clean = clean.replaceAll("\\\\","BSLASH");
		clean = clean.replaceAll("\\&","AMPERSAND");
		clean = clean.replaceAll("\\%","PERCENT");
		clean = clean.replaceAll("\\?","QUESTION");
		clean = clean.replaceAll("\\!","EXCLAMATION");
		clean = clean.replaceAll("\\@","ATSIGN");
		clean = clean.replaceAll("\\#","HASHTAG");
		clean = clean.replaceAll("\\=","EQUAL");
		clean = clean.replaceAll("\\+","PLUS");
		return clean;
	}
	
}
