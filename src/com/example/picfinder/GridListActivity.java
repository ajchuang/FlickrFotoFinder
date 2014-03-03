package com.example.picfinder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;

public class GridListActivity extends Activity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_grid_list);
	
		GridView gridview = (GridView) findViewById (R.id.gridview_main);
		ImageAdapter imgAdpt = new ImageAdapter (this); 
	    gridview.setAdapter (imgAdpt);
	    
	    // start to load image
	    new ImgLoaderThread (this, imgAdpt).execute ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid_list, menu);
		return true;
	}
	
	public void startImgLoaderTask () {
		
	}
	
	// @lfred inner working class
	private static class ImgLoaderThread extends AsyncTask<Void, Void, Void> {

		private static final String M_LOG_TAG = "@lfred_ldr";
		GridListActivity m_act;
		ImageAdapter m_adpt;
		
		public ImgLoaderThread (GridListActivity act, ImageAdapter adpt) {
			m_act = act;
			m_adpt = adpt;
		}
		
		@Override
		protected void onPostExecute (Void result) {
			Log.i (M_LOG_TAG, "onPostExecute done");
			m_adpt.notifyDataSetChanged ();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			
			for (int i=0; i<searchRepo.per_page; ++i) {
				Log.i (M_LOG_TAG, "Loading img: " + Integer.toString (i));
				String u = searchRepo.getRepo().getUrlAt (i);
				
				try {
					URL url = new URL (u);
			        URLConnection conn = url.openConnection();
		
			        HttpURLConnection httpConn = (HttpURLConnection)conn;
			        httpConn.setRequestMethod("GET");
			        httpConn.connect();
		
			        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			        	InputStream inputStream = httpConn.getInputStream();
		
			            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			            inputStream.close ();
			            searchRepo.getRepo ().addBitmap (bitmap, i);
			        }
				} catch (Exception e) {
					Log.i (M_LOG_TAG, "doInBackground: " + e);
				}
			}
			
			
			return null;
		}
	}
}
