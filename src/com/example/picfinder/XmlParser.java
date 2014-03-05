package com.example.picfinder;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class XmlParser {
	
	static final String M_LOG_TAG = "@lfred_xml";
	static int m_photoCounter = 0;
	static int m_curPage;

	static public boolean parseXml (InputStream in) {
		
		Log.i (M_LOG_TAG, "Begin to parse XML");
		
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
	        
	        m_photoCounter = 0;
	        parser.nextTag (); 
	        return readHeader (parser);
	    }
		
		return false;
	}
	
	static public boolean readHeader (XmlPullParser parser) {
		
		try {
			parser.require (XmlPullParser.START_TAG, null, "photos");
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.i (M_LOG_TAG, "Flickr gets drunk.1");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.i (M_LOG_TAG, "Flickr gets drunk.2");
		}
		
		String tag = parser.getName ();
		if (tag.equals ("photos")) {
			String page    = parser.getAttributeValue (null, "page");
			String pages   = parser.getAttributeValue (null, "pages");
			String perPage = parser.getAttributeValue (null, "perpage");
			String total   = parser.getAttributeValue (null, "total");
			
			m_curPage = Integer.parseInt (page);
			Log.i (M_LOG_TAG, "Flickr says - photos : " + page + ":" + pages + ":" + perPage + ":" + total);
			searchRepo repo = searchRepo.getRepo ();
			
			try {
				repo.allocCache (Integer.parseInt (page));
				repo.setXmlStat (
						Integer.parseInt (total), 
						Integer.parseInt (pages), 
						Integer.parseInt (page), 
						Integer.parseInt (perPage));
			} catch (Exception e) {
				Log.i (M_LOG_TAG, "Flickr gets drunk.");
				e.printStackTrace ();
			}
		}
		
		try {
		    while (parser.next () != XmlPullParser.END_TAG) {
		    	
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName ();
		        
		        // Starts by looking for the entry tag
		        if (name.equals ("photo")) {	
		        	readPhoto (parser);
		        	Log.i (M_LOG_TAG, "Flickr found photo");
		        	
		        	// consume myself?
		        	parser.require (XmlPullParser.END_TAG, null, "photo");
		        } else {
		            skip (parser);
		        }
		    }
		} catch (Exception eee) {
			Log.i (M_LOG_TAG, "Flickr gets drunk.3");
			eee.printStackTrace ();
		}
	    
		return true;
	}
	
	static public boolean readPhoto (XmlPullParser parser) {
	
		try {
			parser.require (XmlPullParser.START_TAG, null, "photo");
		
	    	String id    	 = parser.getAttributeValue (null, "id");
			String secret    = parser.getAttributeValue (null, "secret");
			String server 	 = parser.getAttributeValue (null, "server");
			String farm   	 = parser.getAttributeValue (null, "farm");	
			String date		 = parser.getAttributeValue (null, "datetaken");
			String ownername = parser.getAttributeValue (null, "ownername");
			
			Log.i (M_LOG_TAG, "Flickr photo : " + id + ":" + secret + ":" + server + ":" + farm);
			
			String des = "description"; //readDescription (parser);
			
			while (parser.next() != XmlPullParser.END_TAG) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        if (name.equals ("description")) {
		            des = readDescription (parser);
		        } else {
		            skip (parser);
		        }
		    } 
			
			// @lfred: create the data object, and add to the repo.
			singleData d = 
				new singleData (
					m_curPage,
					m_photoCounter,
					id,
					secret,
					server,
					farm,
					des,
					date,
					ownername);
			
			searchRepo.getRepo().insertNewData (d, m_curPage); 
			m_photoCounter++;
			
		} catch (Exception e) {
			Log.i (M_LOG_TAG, "Flickr gets drunk.4");
			e.printStackTrace ();
		}
		return true;
	}
	
	static String readDescription (XmlPullParser parser) {
		
		String des = null;
		try {
			parser.require (XmlPullParser.START_TAG, null, "description");
			des = readText(parser);
			parser.require (XmlPullParser.END_TAG, null, "description");
		} catch (Exception e) {
			return null;
		}
		
	    return des;
	}
	
	// For the tags title and summary, extracts their text values.
	static String readText (XmlPullParser parser) throws Exception {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    
	    return result;
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
