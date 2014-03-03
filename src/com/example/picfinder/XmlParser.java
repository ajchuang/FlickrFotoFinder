package com.example.picfinder;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class XmlParser {
	
	static final String M_LOG_TAG = "@lfred_xml";

	static public boolean parseXml (InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
        	parser.setFeature (XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        	parser.setInput (in, null);
        	parser.nextTag ();
        	return readRsp (parser); 
		} catch (Exception exp) {
			Log.i (M_LOG_TAG, "parseXML: " + exp);
			return false;
		}
	}
	
	static public boolean readRsp (XmlPullParser parser) throws Exception {
		
		parser.require (XmlPullParser.START_TAG, null, "rsp");
		
		String tag = parser.getName ();
	    if (tag.equals ("rsp")) {
	    	
	    	String relType = parser.getAttributeValue (null, "stat");
	    	Log.i (M_LOG_TAG, "Flickr says : " + relType);
	    	
	        if (relType.equals ("ok") == false) 
	        	return false;
	        
	        parser.nextTag (); 
	        return readHeader (parser);
	    }
		
		return false;
	}
	
	static public boolean readHeader (XmlPullParser parser) throws Exception {
		
		parser.require (XmlPullParser.START_TAG, null, "photos");
		
		String tag = parser.getName ();
		if (tag.equals ("photos")) {
			String page    = parser.getAttributeValue (null, "page");
			String pages   = parser.getAttributeValue (null, "pages");
			String perPage = parser.getAttributeValue (null, "perpage");
			String total   = parser.getAttributeValue (null, "total");
			
			Log.i (M_LOG_TAG, "Flickr says - photos : " + page + ":" + pages + ":" + perPage + ":" + total);
			searchRepo repo = searchRepo.getRepo ();
			repo.setXmlStat (
				Integer.parseInt (total), 
				Integer.parseInt (pages), 
				Integer.parseInt (page), 
				Integer.parseInt (perPage));
		}
		
	    while (parser.next () != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName ();
	        
	        // Starts by looking for the entry tag
	        if (name.equals("photo")) {
	            readPhoto (parser);
	        } else {
	            skip (parser);
	        }
	    }
	    
		return true;
	}
	
	static public boolean readPhoto (XmlPullParser parser) throws Exception {
		
		parser.require (XmlPullParser.START_TAG, null, "photo");
		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        	        
	        String tag = parser.getName ();
	        
			if (tag.equals ("photo")) {
				String id    	= parser.getAttributeValue (null, "id");
				String secret   = parser.getAttributeValue (null, "secret");
				String server 	= parser.getAttributeValue (null, "server");
				String farm   	= parser.getAttributeValue (null, "farm");
				
				Log.i (M_LOG_TAG, "Flickr says - photos : " + id + ":" + secret + ":" + server + ":" + farm);
				
				//searchRepo repo = searchRepo.getRepo ();
				//repo.setXmlStat (
				//	Integer.parseInt (total), 
				//	Integer.parseInt (pages), 
				//	Integer.parseInt (page), 
				//	Integer.parseInt (perPage));
			}
	    }
		
		return true;
	}
	
	static public boolean skip (XmlPullParser parser) throws Exception {
		
		if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
		
	    int depth = 1;
	    
	    while (depth != 0) {
	        switch (parser.next ()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
		return true;
	}
	
}
