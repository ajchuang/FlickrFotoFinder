package com.example.picfinder;

import java.util.Vector;

import android.util.Log;

public class searchRepo {

	static String M_LOG_TAG = "@lfred_repo";
	
	// The repo
	static searchRepo m_theRepo = null;
	
	// provided by main activity
	String m_keyword;
	
	// REST API result
	int	m_totalResult;
	int m_currentPage;		// count from 1
	int m_totalPage;
	int m_resultPerPage;
	
	// Result of current parsed XML data -> data in this page
	Vector<String> m_urls;	// start from 0
	
	public static searchRepo getRepo () {
		
		if (m_theRepo == null) {
			m_theRepo = new searchRepo ();
		}
		
		return m_theRepo;
	}
	
	// singleton trick!
	private searchRepo () {
		m_urls = new Vector<String> ();
	}
	
	public void setKeyword (String kw) {
		m_keyword = new String (kw);
	}
	
	public String getKeyword () {
		return m_keyword;
	}
	
	public void setXmlStat (int total, int tPage, int cPage, int perPage) {
		
		m_totalResult	= total;
		m_currentPage	= cPage;
		m_totalPage		= tPage;
		m_resultPerPage = perPage;
	}
	
	public void addNewUrl (String url, int local_idx) {
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
}
