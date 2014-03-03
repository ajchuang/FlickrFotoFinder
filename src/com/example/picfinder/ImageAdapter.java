package com.example.picfinder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
	//private static final String InputStream = null;
	private static final String M_LOG_TAG = "@lfred_img";
	// data member
	GridListActivity m_context;

	// Constructor
	public ImageAdapter (GridListActivity c) {
		m_context = c;
	}

	@Override
	public int getCount() {
		
		return searchRepo.getRepo().getTotalCount ();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		
		ImageView imageView;
		Log.i (M_LOG_TAG, "getView @" + Integer.toString (position));
		
		// @lfred: handle out-of-range
		//if (position > )
		
		if (convertView == null) {
			imageView = new ImageView (m_context);
			imageView.setLayoutParams (new GridView.LayoutParams (150, 150));
			imageView.setScaleType (ImageView.ScaleType.CENTER_CROP);
			//imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
	
		Bitmap bmp = searchRepo.getRepo().getBitmapAt (position);
		
		if (bmp != null)
			imageView.setImageBitmap (bmp);
		
		return imageView;
	}
}
