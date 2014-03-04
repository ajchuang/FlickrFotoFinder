package com.example.picfinder;

import java.util.Vector;

import android.graphics.Bitmap;

public class cacheData {

	boolean m_isInuse;
	int	m_page;
	int m_pageSize;
	Vector<String> m_urls;
	Vector<Bitmap> m_bitmap;
	
	public cacheData (int pageSize) {
		
		m_isInuse = false;
		m_page = -1;
		m_pageSize = pageSize;
		
		m_urls = new Vector<String> (pageSize);
		m_bitmap = new Vector<Bitmap> (pageSize);
	}
	
	public int getPage () {
		return m_page;
	}
	
	public boolean isActivated () {
		return m_isInuse;
	}
	
	public void setUrlAt (int pageNum, int localIdxNum, String url) {
		
		m_isInuse = true;
		m_page = pageNum;
		m_urls.add (localIdxNum, url);
	}
	
	public String getUrlAt (int pageNum, int localIdxNum) {
		
		if (pageNum == m_page && localIdxNum < m_pageSize) {
			return m_urls.elementAt (localIdxNum);
		} else
			return null;
	}
	
	public void setBitmapAt (int pageNum, int localIdxNum, Bitmap bmp) {
		
		m_isInuse = true;
		m_page = pageNum;
		m_bitmap.add (localIdxNum, bmp);
	}
	
	public Bitmap getBitmapAt (int pageNum, int localIdxNum) {
		if (pageNum == m_page && localIdxNum < m_pageSize) {
			return m_bitmap.elementAt (localIdxNum);
		} else
			return null;
	}
	
	public void clear () {
		m_isInuse = false;
		m_page = -1;
		m_urls.clear ();
		m_bitmap.clear ();
	}
}
