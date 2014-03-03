package com.example.picfinder;

import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

public class searchRepo {

	public final static int per_page = 30;
	final static String M_LOG_TAG = "@lfred_repo";
	final static String m_searchUrl_1 = 
			"http://api.flickr.com/services/rest/?method=flickr.photos.search&" + 
			"api_key=278c507b03fa3a089af0a5972f83a8e4&tags=";
	
	final static String m_searchUrl_2 = "&per_page=" + Integer.toString(per_page) + "&page=";	
			//"&extras=date_taken,owner_name,description";
	
	// The repo
	static searchRepo m_theRepo = null;
	
	// provided by main activity
	Vector<String> m_keyword;
	
	// REST API result
	int	m_totalResult;
	int m_currentPage;		// count from 1
	int m_totalPage;
	int m_resultPerPage;
	
	// Result of current parsed XML data -> data in this page
	Vector<String> m_urls;	// start from 0
	Vector<Bitmap> m_bitmap;
	
	public static searchRepo getRepo () {
		
		if (m_theRepo == null) {
			m_theRepo = new searchRepo ();
		}
		
		return m_theRepo;
	}
	
	// singleton trick!
	private searchRepo () {
		m_urls = new Vector<String> (per_page);
		m_bitmap = new Vector<Bitmap> (per_page);
		m_keyword = new Vector<String> ();
		
		// REST API result
		m_totalResult = 0;
		m_currentPage = 1;		// count from 1
		m_totalPage = 0;
		m_resultPerPage = 0;
	}
	
	public void setKeyword (String... kw) {
		
		if (kw.length == 0)
			return;
		else {
			for (int i=0; i<kw.length; ++i)
				m_keyword.add (new String (kw[i]));
		}
	}
	
	public Vector<String> getKeyword () {
		return m_keyword;
	}
	
	public String generateSearchUri (String... params) {
		
		if (params.length == 0)
			return null;
		
		setKeyword (params);
		String searchKeyWords = new String (params[0]);
		
		for (int i=1; i<params.length; ++i)
			searchKeyWords = searchKeyWords + "," + params[i];
		
		String url = m_searchUrl_1 + searchKeyWords + m_searchUrl_2 + Integer.toString (m_currentPage);
		
		Log.i (M_LOG_TAG, "the query URL: " + url);
		return url;
	}
	
	public void setXmlStat (int total, int tPage, int cPage, int perPage) {
		
		m_totalResult	= total;
		m_currentPage	= cPage;
		m_totalPage		= tPage;
		m_resultPerPage = perPage;
	}
	
	public int getTotalCount () {
		return m_totalResult;
	}
	
	public void addNewUrl (String url, int local_idx) {
		Log.i (M_LOG_TAG, "addNewUrl: " + url);
		m_urls.add (local_idx, url);
	}
	
	public String getUrlAt (int total_idx) {
		
		// check if the data in the range
		int min = (m_currentPage - 1) * m_resultPerPage;
		int max = (m_currentPage == m_totalPage) ? (m_totalResult - 1): (min + m_resultPerPage - 1);
		
		if (total_idx < min || total_idx > max) {
			Log.i (M_LOG_TAG, "getUrlAt - out of bound");
			return null;
		} else {
			return m_urls.elementAt (total_idx - min);
		}
	}
	
	public void addBitmap (Bitmap bitmap, int local_idx) {
		m_bitmap.add (local_idx, bitmap);
	}
	
	public Bitmap getBitmapAt (int total_idx) {
		
		// check if the data in the range
		int min = (m_currentPage - 1) * m_resultPerPage;
		int max = (m_currentPage == m_totalPage) ? (m_totalResult - 1) : (min + m_resultPerPage - 1);
		
		if (total_idx < min || total_idx > max) {
			Log.i (M_LOG_TAG, "getUrlAt - out of bound");
			return null;
		} else {
			int local_idx = total_idx - min;
			if (local_idx >= m_bitmap.size())
				return null;
			else
				return m_bitmap.elementAt (total_idx - min);
		}
	}
}
