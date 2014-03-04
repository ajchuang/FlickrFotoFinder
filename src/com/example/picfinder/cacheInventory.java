package com.example.picfinder;

import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

public class cacheInventory {

	final static int m_cacheCount = 5;
	
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
	
	public Bitmap getBitmapAt (int pageNum, int localIdx) {
		
		for (cacheData c : m_cache) {
			
			if (c.isActivated() == true) {
				if (c.getPage() == pageNum)
					return c.getBitmapAt (pageNum, localIdx); 
			}
		}
		
		return null;
	}
	
	public void addBitmapAt (int pageNum, int localIdx, Bitmap bmp) {
		
		boolean isNext = false;
		
		// 1. found the page
		for (cacheData c : m_cache) {
			
			// @lfred: check which one we are going to replace. (the previous one or the next one)
			if (c.getPage () < pageNum)
				isNext = true;
			
			if (c.isActivated () && c.getPage() == pageNum) {
				c.setBitmapAt (pageNum, localIdx, bmp);
				return;
			}
		}
		
		// 2. try to find an empty page
		for (cacheData c : m_cache) {	
			if (c.isActivated () == false) {
				c.setBitmapAt (pageNum, localIdx, bmp);
				return;
			}
		}
		
		// 0. debug
		for (cacheData c : m_cache)
			Log.i ("@lfred_cache", "current pages: " + c.getPage());
		
		// 3. age a cache
		if (isNext) {
			
			// replace the oldest one
			int currentPage = 1000000;
			cacheData minCache = null;
			
			// find the smallest
			for (cacheData c : m_cache) {
				if (c.getPage () < currentPage) {
					currentPage = c.getPage ();
					minCache = c;
				}
			}
			
			if (minCache != null) {
				Log.i ("@lfred_cache", "Replace page: " + Integer.toString (minCache.getPage ()));
				minCache.setBitmapAt (pageNum, localIdx, bmp);
			} else
				Log.i ("@lfred_cache", "!!! MinCache Unexpected NULL !!!");
		} else {
			
			// replace the newest one
			int currentPage = -1;
			cacheData maxCache = null;
			
			// find the smallest
			for (cacheData c : m_cache) {
				if (c.getPage () > currentPage) {
					currentPage = c.getPage ();
					maxCache = c;
				}
			}
			
			if (maxCache != null) {
				Log.i ("@lfred_cache", "Replace page: " + Integer.toString (maxCache.getPage ()));
				maxCache.setBitmapAt (pageNum, localIdx, bmp);
			} else
				Log.i ("@lfred_cache", "!!! MaxCache Unexpected NULL !!!");
		}
		
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
	
	public void addUrlAt (int pageNum, int localIdx, String url) {
		
		boolean isNext = false;
		
		// 1. found the page
		for (cacheData c : m_cache) {
			
			if (c.getPage () < pageNum)
				isNext = true;
			
			if (c.isActivated () && c.getPage() == pageNum) {
				c.setUrlAt (pageNum, localIdx, url);
				return;
			}
		}
		
		// 2. try to find an empty page
		for (cacheData c : m_cache) {	
			if (c.isActivated () == false) {
				c.setUrlAt (pageNum, localIdx, url);
				return;
			}
		}
		
		// 3. age a cache
		
		if (isNext) {
			
			int currentPage = 10000;
			cacheData minCache = null;
			
			// find the smallest
			for (cacheData c : m_cache) {
				if (c.getPage () < currentPage) {
					currentPage = c.getPage ();
					minCache = c;
				}
			}
			
			if (minCache != null)
				minCache.setUrlAt (pageNum, localIdx, url);
			else
				Log.i ("@lfred_cache", "!!! MinCache Unexpected NULL !!!");
		} else {
			int currentPage = -1;
			cacheData maxCache = null;
			
			// find the smallest
			for (cacheData c : m_cache) {
				if (c.getPage () > currentPage) {
					currentPage = c.getPage ();
					maxCache = c;
				}
			}
			
			if (maxCache != null)
				maxCache.setUrlAt (pageNum, localIdx, url);
			else
				Log.i ("@lfred_cache", "!!! MaxCache Unexpected NULL !!!");
		}
		
	}
	
	
}
