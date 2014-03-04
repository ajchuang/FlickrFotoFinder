package com.example.picfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;

public class GridListActivity extends Activity {
	
	// UI members - used to manipulate things later
	ImageAdapter 	m_adapter;
	GridView		m_gridview;
	searchRepo		m_repo;
	ProgressDialog	m_progDialog;
	
	// Control the singleton attribute of the GridList
	ImgLoaderThread	m_slaveThread;
	
	public static void log (String str) {
		Log.i ("@lfred_list", str);
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_grid_list);
		
		// control components;
		m_repo = searchRepo.getRepo ();
		m_slaveThread = null;
	
		startImgLoaderTask (0);
		//m_gridview = (GridView) findViewById (R.id.gridview_main);
		//m_adapter = new ImageAdapter (this); 
	    //m_gridview.setAdapter (m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate (R.menu.grid_list, menu);
		return true;
	}
	
	// The parameter here is the global position
	public void startImgLoaderTask (int global_position) {
		
		if (m_slaveThread == null) {
			
			// Use UI to block user strange behaviors
			m_progDialog = 
				ProgressDialog.show (this, "Please wait ...", "Searching Data ...", true);					
			m_progDialog.setCancelable (false);
			
			// start to load image
			m_slaveThread = new ImgLoaderThread (this, m_adapter, global_position);
			m_slaveThread.execute ();
		} else {
			log ("startImgLoaderTask: You should wait");
		}
	}
	
	public void onImgLoadCmpl () {
		// clean members
		m_progDialog.dismiss ();
		m_slaveThread = null;
		
		
		if (m_adapter == null) {
			m_gridview = (GridView) findViewById (R.id.gridview_main);
			m_adapter = new ImageAdapter (this); 
		    m_gridview.setAdapter (m_adapter);
		} else {
		
			// update the list view
			m_adapter.notifyDataSetChanged ();
		}
	}
	
	// @lfred inner working class
	private static class ImgLoaderThread extends AsyncTask<Void, Void, Void> {

		private static final String M_LOG_TAG = "@lfred_ldr";
		GridListActivity m_act;
		ImageAdapter m_adpt;
		int m_globalPosition;
		int m_curPage;
		
		public ImgLoaderThread (GridListActivity act, ImageAdapter adpt, int global_position) {
			m_act = act;
			m_adpt = adpt;
			m_globalPosition = global_position;
			m_curPage = (global_position - (global_position%100))/100 + 1; 
		}
		
		@Override
		protected void onPostExecute (Void result) {
			Log.i (M_LOG_TAG, "onPostExecute done");
			m_act.onImgLoadCmpl ();
		}
		
		@Override
		protected Void doInBackground (Void... params) {
			
			try {
				// 1. construct uri
				String uri = construct_uri ();
				
				// 2. load xml from Flickr
				String tempFile = contactFlickr (uri);
				
				// 3. Parse XML
				parseXml (tempFile);
				
				// 4. load Bitmap from Flickr
				load_img ();
				
			} catch (Exception e) {
				Log.i (M_LOG_TAG, "Exception: " + e);
				e.printStackTrace ();
			}
			
			return null;
		}
		
		String construct_uri () {
			return new String (searchRepo.getRepo().generateSearchUri (m_globalPosition));
		}
		
		String contactFlickr (String url) throws Exception {
			
			// Step 1. get the XML file using REST
			String tempPath = null;
			File outFile = File.createTempFile ("result", ".xml");
			FileOutputStream f = new FileOutputStream (outFile);
			
			URL u = new URL (url);
		    HttpURLConnection c = (HttpURLConnection) u.openConnection ();
		    c.setRequestMethod ("GET");
		    c.setDoOutput (true);
		    c.connect ();
		    
		    InputStream in = c.getInputStream ();

		    byte[] buffer = new byte[2048];
		    int len = 0;
		    int total_len = 0;
		    
		    while ((len = in.read (buffer)) != -1) {
		         f.write (buffer, 0, len);
		         total_len += len;
		    }
		    
		    Log.i (M_LOG_TAG, "Total length = " + Integer.toString (total_len));

		    f.close ();
		    tempPath = outFile.getAbsolutePath ();
		    in.close ();
		    
		    Log.i (M_LOG_TAG, "XML file path: " + tempPath);
			
			return tempPath;
		}
		
		boolean parseXml (String tempPath) throws Exception {
			
			//parseXml (new File)
		    FileInputStream fin = new FileInputStream (tempPath);
		    XmlParser.parseXml (fin); 
		    fin.close ();
		    
		    return true;
		}
		
		boolean load_img () {
			
			for (int i=0; i<searchRepo.per_page; ++i) {
				
				int global_idx = searchRepo.per_page * (m_curPage - 1) + i;
				String u = searchRepo.getRepo().getUrlAt (global_idx);
				Log.i (M_LOG_TAG, "Loading img: " + u);
				
				try {
					URL url = new URL (u);
			        URLConnection conn = url.openConnection();
		
			        HttpURLConnection httpConn = (HttpURLConnection)conn;
			        httpConn.setRequestMethod ("GET");
			        httpConn.connect ();
		
			        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			        	InputStream inputStream = httpConn.getInputStream ();
		
			            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			            inputStream.close ();
			            searchRepo.getRepo ().addBitmap (bitmap, m_curPage, i);
			        } else {
			        	Log.i (M_LOG_TAG, "Flickr complains");
			        }
				} catch (Exception e) {
					Log.i (M_LOG_TAG, "load_img: " + e);
					e.printStackTrace ();
				}
			}
			
			return true;
		}
	}
}
