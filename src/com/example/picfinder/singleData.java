package com.example.picfinder;

import android.graphics.Bitmap;
import android.util.Log;

public class singleData {

	int m_pageNum;	
	int m_local;	
	
	String m_id;
	String m_secret;
	String m_server;
	String m_farm;
	String m_description;
	String m_date;
	String m_ownerNames;
	
	Bitmap m_bmp;
	
	public singleData (int page, int local_idx, String id, String secret, String server, String farm, String des, String date, String owner) {
		
		m_pageNum = page;
		m_local = local_idx;
		m_id = id;
		m_secret = secret;
		m_server = server;
		m_farm = farm;
		
		m_description = new String (des);
		m_date = new String (date);
		m_ownerNames = new String (owner);
	}
	
	public int getPageIdx () {
		return m_pageNum;
	}
	
	public int getLocalIdx () {
		return m_local;
	}
	
	public int getGlobalIdx () {
		return (m_pageNum - 1) * SysParam.M_PER_PAGE + m_local;
	}
	
	public String getId () {
		return m_id;
	}
	
	public String getSecret () {
		return m_secret;
	}
	
	public String getServer () {
		return m_server;
	}
	
	public String getFarm () {
		return m_farm;
	}
	
	public String getDes () {
		return m_description;
	}
	
	public String getDate () {
		return m_date;
	}
	
	public String getOwner () {
		return m_ownerNames;
	}
	
	public void setBitmap (Bitmap bmp) {
		m_bmp = bmp;
	}
	
	public Bitmap getBitmap () {
		return m_bmp;
	}
	
	public String getThumbnailUrl () {
		String url = "http://farm" + m_farm + ".staticflickr.com/" + m_server + "/" + m_id + "_" + m_secret + "_m.jpg";
		Log.i ("@lfred_sd", url);
		return url;
	}
	
	public String getMediumImgUrl () {
		String url = "http://farm" + m_farm + ".staticflickr.com/" + m_server + "/" + m_id + "_" + m_secret + "_c.jpg";
		return url;
	}
}
