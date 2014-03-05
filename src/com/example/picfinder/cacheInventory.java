package com.example.picfinder;

import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

public class cacheInventory {

	final static int m_cacheCount = SysParam.M_CACHE_SIZE;
	
	Vector<cacheData> m_cache;
	int m_pageSize;
	
	public cacheInventory (int pageSize) {
		m_pageSize = pageSize;
		m_cache = new Vector<cacheData> (m_cacheCount);
		
		for (int i=0; i<m_cacheCount; ++i)
			m_cache.add (new cacheData (pageSize));
	}
	
	public void clear () {
		
		for (cacheData c: m_cache)
			c.clear ();
	}
	
	void printCurrentCachePage () {
		String x = "";
		for (cacheData c: m_cache) {
			if (c.isActivated ()) {
				x = x + Integer.toString(c.getPage()) + ",";
			}
		}
		
		Log.i ("@lfred_cache", "current page:" + x);
	}
	
	public void allocCache (int pageNum) {
		
		// 1. check if the page exists
		for (cacheData c : m_cache) {
			
			if (c.isActivated () && c.getPage() == pageNum) {
				// no need to alloc, there is one already.
				printCurrentCachePage ();
				return;
			}
		}
		
		// 2. alloc an empty cache	
		for (cacheData c : m_cache) {	
			if (c.isActivated () == false) {
				c.activate (pageNum, SysParam.M_PER_PAGE);
				printCurrentCachePage ();
				return;
			}
		}
		
		// 3. swap a cache
		cacheData minC = m_cache.elementAt(0);
		cacheData maxC = m_cache.elementAt(0);
		
		for (cacheData c : m_cache) {
			if (c.getPage() > maxC.getPage())
				maxC = c;
			
			if (c.getPage() < minC.getPage())
				minC = c ;
		}
		
		if (pageNum > maxC.getPage ()) {
			minC.clear ();
			minC.activate (pageNum, SysParam.M_PER_PAGE);
		} else if (pageNum < minC.getPage ()) {
			maxC.clear ();
			maxC.activate (pageNum, SysParam.M_PER_PAGE);
		} else {
			Log.i ("@lfred_cache", "WTF - what are you trying ?");
		}
		
		printCurrentCachePage ();
	}
	
	public void insertSingleData (int pageNum, singleData newData) {
		
		for (cacheData c : m_cache) {
			if (c.getPage () == pageNum) {
				c.insertData (pageNum, newData.getLocalIdx(), newData);
				return;
			}
		}
		
		Log.i ("@lfred_cache", "WTF - what are you trying.2 ?");
	}
	
	public Bitmap getBitmapAt (int pageNum, int localIdx) {
		
		for (cacheData c : m_cache) {
			
			if (c.isActivated() == true) {
				if (c.getPage() == pageNum)
					return c.getBitmapAt (pageNum, localIdx); 
			}
		}
		Log.i ("@lfred_cache", "WTF - what are you trying.3 ?");
		return null;
	}
	
	public void addBitmapAt (int pageNum, int localIdx, Bitmap bmp) {
		
		for (cacheData c : m_cache) {
			if (c.getPage() == pageNum) {
				c.setBitmapAt (pageNum, localIdx, bmp);
				return;
			}
		}
		Log.i ("@lfred_cache", "WTF - what are you trying.4 ?");
		return;
	}
	
	public String getUrlAt (int pageNum, int localIdx) {
		
		for (cacheData c : m_cache) {
			
			if (c.isActivated() == true) {
				if (c.getPage() == pageNum)
					return c.getUrlAt (pageNum, localIdx); 
			}
		}
		
		return null;
	}
	
	public singleData getDataObject (int pageNum, int local_Idx) {
		
		for (cacheData c : m_cache) {
			
			if (c.isActivated() && c.getPage() == pageNum)
				return c.getDataObject (local_Idx);
		}
		
		return null;
	}
}
