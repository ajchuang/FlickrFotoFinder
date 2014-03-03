package com.example.picfinder;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.*;
import android.view.View.*;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.util.*;
import android.content.Intent;
import android.database.Cursor;
import android.provider.*;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.*;

import org.xmlpull.v1.XmlPullParser;

// Sample code: show image from internet in the image viewer
// Note: Need user permission -> INTERNET
// try {
// Bitmap bt = BitmapFactory.decodeStream (InputStream new URL (url).getContent ());
// } catch (Exception e) {
// }

public class MainActivity extends Activity implements OnClickListener {
	
	final static int 	M_REQCODE_CONTACT = 1;
	final static String M_LOG_TAG = "@lfred_main";
	
	final static String m_searchUrl_1 = 
			"http://api.flickr.com/services/rest/?method=flickr.photos.search&" + 
			"api_key=278c507b03fa3a089af0a5972f83a8e4&tags=";
	
	final static String m_searchUrl_2 = 	
			"&extras=date_taken,owner_name,description";
	
	static ProgressDialog	m_progDialog;
	static searchRepo		m_repo;
	static MainActivity		m_myself;
	
	public static String flickrUrlBuilder (String tag) {
		return new String (m_searchUrl_1 + tag + m_searchUrl_2);
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		Button searchBtn = (Button) findViewById (R.id.searchBtn);
		searchBtn.setOnClickListener (this);
		
		Button contactBtn = (Button) findViewById (R.id.contactBtn);
		contactBtn.setOnClickListener (this);
		
		// @lfred: get the repo
		m_repo = searchRepo.getRepo ();
		m_myself = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick (View v) {
		
		int id = v.getId ();
		
		switch (id) {
		
			case R.id.searchBtn: {
				
				Log.i (M_LOG_TAG, "search button clicked");
				
				EditText text = (EditText) findViewById (R.id.keywordText);
				String keyword = text.getText ().toString ();
				
				Log.i (M_LOG_TAG, keyword);
				
				if (keyword.length () == 0) {
					Toast t  = Toast.makeText (MainActivity.this, "Empty Keyword", Toast.LENGTH_LONG);
					t.show ();
				} else {
					startSearchTask (keyword);
				}
			} 
			break;
			
			case R.id.contactBtn: {
				Log.i (M_LOG_TAG, "contact button clicked");
				
				// @lfred: create an intent to open the contact windows
				Intent it = new Intent (Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult (it, M_REQCODE_CONTACT);
			} break;
			
			default:
				Log.i (M_LOG_TAG, "unknown button is pressed");
			break;
		
		}
	}
	
	@Override
	public void onActivityResult (int reqCode, int resultCode, Intent data) {
		
		super.onActivityResult (reqCode, resultCode, data);
		
		if (reqCode == M_REQCODE_CONTACT) {
		
			if (resultCode != RESULT_OK) {
			
				Toast t = Toast.makeText (MainActivity.this, "No contact selected", Toast.LENGTH_LONG);
				t.show ();
				return;
			
			} else {
				
				if (data != null) {
		            Uri u = data.getData ();
		            Log.i (M_LOG_TAG, "URI: " + u.toString ());
		            
		            int idx;
		            String id;
		            String name;
		            String hasPhone;
		            
		            Cursor cursor = getContentResolver().query (u, null, null, null, null);
		            
		            if (cursor.moveToFirst ()) {
		                idx = cursor.getColumnIndex (ContactsContract.Contacts._ID);
		                id = cursor.getString (idx);

		                idx = cursor.getColumnIndex (ContactsContract.Contacts.DISPLAY_NAME);
		                name = cursor.getString (idx);

		                idx = cursor.getColumnIndex (ContactsContract.Contacts.HAS_PHONE_NUMBER);
		                hasPhone = cursor.getString (idx);
		                
		                Log.i (M_LOG_TAG, "Contact id  : " + id);
		                Log.i (M_LOG_TAG, "Contact Name: " + name);
		                Log.i (M_LOG_TAG, "Contact hasPhone: " + hasPhone);
		                startSearchTask (name);
		            }
		            
				} else {
					Toast t  = Toast.makeText (MainActivity.this, "Empty data", Toast.LENGTH_LONG);
					t.show ();
					return;
				}
			
				// get the contact info and use the async task to run the task
			}
		} else {
			Log.i (M_LOG_TAG, "Unknown activity");
		}
		
		Log.i (M_LOG_TAG, "return from activity");
	}
	
	void startSearchTask (String key) {
		m_progDialog = 
			ProgressDialog.show (MainActivity.this, "Please wait ...", "Searching Data ...", true);					
		m_progDialog.setCancelable (false);
			
		// start the worker thread
		new WorkThread ().execute (key);
	}
	
	void openGridList () {
		
		Log.i (M_LOG_TAG, "Start gridview");
		
		Intent it = new Intent ();
		it.setClass (MainActivity.this, GridListActivity.class);
		startActivity (it);
	}
	
	// @lfred inner working class
	private static class WorkThread extends AsyncTask<String, Void, Integer> {
		
		// @lfred: this will be called in the UI thread.
		@Override
		protected void onPostExecute (Integer result) {
			
				
			Log.i (M_LOG_TAG, "WorkThread: onPostExecute");
			MainActivity.m_progDialog.dismiss ();
			
			if (result.intValue() != 0) {
				Log.i (M_LOG_TAG, "onPostExecute - error");
			} else {
				//openGridList ();
			}
				
			return;
		}

		@Override
		protected Integer doInBackground (String... params) {
			
			if (params.length == 0) 
				return -1;
			
			String url = makeUrl (params);
			int returnCode = 0;
			
			// @lfred: new
			try {
				
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

			    byte[] buffer = new byte[1024];
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
			    
			    //parseXml (new File)
			    FileInputStream fin = new FileInputStream (tempPath);
			    XmlParser.parseXml (fin); 
			    fin.close ();
			    
			    // Step 2: parsing the XML file
			    // Step 2.1: read <photos ... >
			    // Step 2.2: read <photo id>, <farm id>, <server_id>, <secret>
			    
				
			} catch (Exception e) {
				Log.i (M_LOG_TAG, "Ooops, IO exception");
				returnCode = -1;
				
			}
			
			Log.i (M_LOG_TAG, "I am a worker:" + params[0]);
			return returnCode;
		}
		
		String makeUrl (String... params) {
			
			String searchKeyWords = new String (params[0]);
			
			for (int i=1; i<params.length; ++i)
				searchKeyWords = searchKeyWords + "," + params[i];
			
			String url = m_searchUrl_1 + searchKeyWords + m_searchUrl_2;
			
			Log.i (M_LOG_TAG, "the query URL: " + url);
			return url;
		}			
	}
}
