package com.example.picfinder;

import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

public class searchRepo {

	public final static int per_page = 100;
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
	//int m_currentPage;		// count from 1
	int m_totalPage;
	int m_resultPerPage;
	
	cacheInventory m_cache;
	
	public static searchRepo getRepo () {
		
		if (m_theRepo == null) {
			m_theRepo = new searchRepo ();
		}
		
		return m_theRepo;
	}
	
	// singleton trick!
	private searchRepo () {
		//m_urls 		= new Vector<String> (per_page);
		//m_bitmap 	= new Vector<Bitmap> (per_page);
		m_keyword 	= new Vector<String> ();
		m_cache     = new cacheInventory (per_page); 
		
		// REST API result
		m_totalResult = 0;
		//m_currentPage = 1;		// count from 1
		m_totalPage = 0;
		m_resultPerPage = per_page;
	}
	
	public void setKeyword (String kw) {
		
		if (kw.length () == 0)
			return;
		else {
			m_keyword.add (new String (kw));
		}
	}
	
	public Vector<String> getKeyword () {
		return m_keyword;
	}
	
	public String generateSearchUri (int global_idx) {
		
		// generate keyword
		String searchKeyWords = new String (m_keyword.elementAt (0));
		
		for (int i=1; i<m_keyword.size(); ++i)
			searchKeyWords = searchKeyWords + "," + m_keyword.elementAt (i);
		
		// calculate page
		int local_idx = global_idx % m_resultPerPage;
		int currentPage = (global_idx - local_idx) / m_resultPerPage + 1;
		String url = m_searchUrl_1 + searchKeyWords + m_searchUrl_2 + Integer.toString (currentPage);
		Log.i (M_LOG_TAG, "the query URL: " + url);
		return url;
	}
	
	public void setXmlStat (int total, int tPage, int cPage, int perPage) {
		
		m_totalResult	= total;
		m_totalPage		= tPage;
		m_resultPerPage = perPage;
	}
	
	public int getTotalCount () {
		return m_totalResult;
	}
	
	public void addNewUrl (String url, int page_num, int local_idx) {
		Log.i (M_LOG_TAG, "addNewUrl:" + Integer.toString (page_num) + ":" + Integer.toString (local_idx));
		m_cache.addUrlAt (page_num, local_idx, url);
	}
	
	public String getUrlAt (int total_idx) {
		
		int local_idx = total_idx % per_page;
		int page_idx = (total_idx - local_idx) / per_page + 1;
		Log.i (M_LOG_TAG, "getUrlAt:" + Integer.toString (page_idx) + ":" + Integer.toString (local_idx));	
		return m_cache.getUrlAt (page_idx, local_idx);
	}
	
	public void addBitmap (Bitmap bitmap, int page_num, int local_idx) {
		Log.i (M_LOG_TAG, "addBitmap:" + Integer.toString (page_num) + ":" + Integer.toString (local_idx));
		m_cache.addBitmapAt (page_num, local_idx, bitmap);
	}
	
	public Bitmap getBitmapAt (int total_idx) {
		
		int local_idx = total_idx % per_page;
		int page_idx = (total_idx - local_idx) / per_page + 1;
		Log.i (M_LOG_TAG, "getBitmapAt:" + Integer.toString (page_idx) + ":" + Integer.toString (local_idx));	
		return m_cache.getBitmapAt (page_idx, local_idx);
	}
	
	public void resetRepo () {
		
		Log.i (M_LOG_TAG, "resetRepo");
	
		// provided by main activity
		m_keyword.clear ();
		
		// REST API result
		m_totalResult = 0;
		//m_currentPage = 1;		// count from 1
		m_totalPage = 0;
		m_resultPerPage = per_page;
		
		// Result of current parsed XML data -> data in this page
		m_cache.clear ();
	}
}
