package com.example.picfinder;

import android.graphics.Bitmap;
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
	GridListActivity m_parentAct;

	// Constructor
	public ImageAdapter (GridListActivity c) {
		m_parentAct = c;
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
		
		Log.i (M_LOG_TAG, "getView request @" + Integer.toString (position));
		ImageView imageView;
		
		if (convertView == null) {
			imageView = new ImageView (m_parentAct);
			imageView.setLayoutParams (new GridView.LayoutParams (150, 150));
			imageView.setScaleType (ImageView.ScaleType.CENTER_CROP);
			
			int height = parent.getHeight();
			if (height > 0) {
				android.view.ViewGroup.LayoutParams layoutParams = (android.view.ViewGroup.LayoutParams) imageView.getLayoutParams();
				layoutParams.height = 270; //(int) (height / rowsCount);
				layoutParams.width = 360;
			}        
			
		} else {
			imageView = (ImageView) convertView;
		}
	
		Bitmap bmp = searchRepo.getRepo().getBitmapAt (position);
		
		if (bmp != null)
			imageView.setImageBitmap (bmp);
		else {
			
			if (position != 0) {
				// load the new page
				Log.i (M_LOG_TAG, "getView miss @" + Integer.toString (position));
				m_parentAct.startImgLoaderTask (position);
			}
		}
		
		
		
		return imageView;
	}
}
