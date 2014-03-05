package com.example.picfinder;

import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

public class cacheData {

	boolean m_isInuse;
	int	m_page;
	int m_pageSize;
	
	Vector<singleData> m_data;
	
	public cacheData (int pageSize) {
		
		m_isInuse = false;
		m_page = -1;
		m_pageSize = pageSize;
		
		m_data = new Vector<singleData> ();
		m_data.ensureCapacity (pageSize);
	}
	
	public void activate (int pageNum, int count) {
		m_isInuse = true;
		m_page = pageNum;
		m_data.clear ();
		m_data.ensureCapacity (pageNum);
	}
	
	public int getPage () {
		return m_page;
	}
	
	public boolean isActivated () {
		return m_isInuse;
	}
	
	public void insertData (int pageCount, int local_idx, singleData d) {
		m_isInuse = true;
		m_page = pageCount;
		m_data.add (local_idx, d);
	}
	
	public singleData getDataObject (int local_idx) {
		return m_data.elementAt (local_idx);
	}
	
	public void clear () {
		m_isInuse = false;
		m_page = -1;
		m_data.clear ();
	}
	
	public void setBitmapAt (int pageNum, int localIdx, Bitmap bmp) {		
		if (m_isInuse == true && pageNum == m_page) {
			m_data.elementAt (localIdx).setBitmap (bmp);
		} else
			return;
		
		return;
	}
	
	public Bitmap getBitmapAt (int pageNum, int localIdx) {
		if (m_isInuse == true && pageNum == m_page) {
			return m_data.elementAt (localIdx).getBitmap ();
		} else {
			Log.i ("@lfred_cData", "Failed to get Bitmap " + Integer.toString(pageNum) + ":" + Integer.toString(localIdx));
			return null;
		}
	}
	
	public String getUrlAt (int pageNum, int localIdx) {
		if (m_isInuse == true && pageNum == m_page) {
			return m_data.elementAt (localIdx).getThumbnailUrl ();
		} else {
			Log.i ("@lfred_cData", "Failed to get url " + Integer.toString(pageNum) + ":" + Integer.toString(localIdx));
			return null;
		}
	}
}
