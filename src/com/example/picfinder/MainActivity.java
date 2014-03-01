package com.example.picfinder;

import android.os.Bundle;
import android.app.Activity;
import android.view.*;
import android.view.View.*;

import android.widget.Button;
import android.widget.EditText;

import android.util.*;
import android.content.Intent;
import android.provider.*;

import android.text.Editable;
import android.os.AsyncTask;
import java.net.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

// Sample code: show image from internet in the image viewer
// Note: Need user permission -> INTERNET
// try {
// Bitmap bt = BitmapFactory.decodeStream (InputStream new URL (url).getContent ());
// } catch (Exception e) {
// }

// Hint: For example, 
// http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key={api key 
// here}&tags=Emma&extras=date_taken,owner_name,description

public class MainActivity extends Activity implements OnClickListener {
	
	final static String m_searchUrl_1 = 
			"http://api.flickr.com/services/rest/?method=flickr.photos.search&" + 
			"api_key=278c507b03fa3a089af0a5972f83a8e4&tags=";
	
	final static String m_searchUrl_2 = 	
			"&extras=date_taken,owner_name,description";
	
	public static String flickrUrlBuilder (String tag) {
		return new String (m_searchUrl_1 + tag + m_searchUrl_2);
	}
	
	// @lfred inner working class
	private static class WorkThread extends AsyncTask<String, Void, String> {
		
		@SuppressWarnings("unused")
		protected void onPostExecute (String... results) {
			return;
		}

		@Override
		protected String doInBackground (String... params) {
			
			if (params.length == 0) 
				return null;
			
			String url = makeUrl (params);
			HttpClient client = new DefaultHttpClient (); 
			
			try {
				HttpGet get = new HttpGet (url);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String setServerString = client.execute (get, responseHandler);
                Log.i ("Mainactivity", "Server rsp: " + setServerString);
      
                // Show response on activity 

                //content.setText(SetServerString);
			} catch (Exception e) {
				e.printStackTrace ();
			}
			
			Log.i ("Mainactivity", "I am a worker:" + params[0]);
			return null;
		}
		
		String makeUrl (String... params) {
			
			String searchKeyWords = new String (params[0]);
			
			for (int i=1; i<params.length; ++i)
				searchKeyWords = searchKeyWords + "," + params[i];
			
			String url = m_searchUrl_1 + searchKeyWords + m_searchUrl_2;
			
			Log.i ("Mainactivity", "the query URL: " + url);
			return url;
		}
		
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button searchBtn = (Button) findViewById (R.id.searchBtn);
		searchBtn.setOnClickListener (this);
		
		Button contactBtn = (Button) findViewById (R.id.contactBtn);
		contactBtn.setOnClickListener (this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick (View v) {
		
		int id = v.getId();
		
		switch (id) {
		
			case R.id.searchBtn: {
				Log.i ("Mainactivity", "search button clicked");
				
				EditText text = (EditText) findViewById (R.id.keywordText);
				String ed = text.getText ().toString ();
				
				Log.i ("Mainactivity", ed);
				
				new WorkThread ().execute (ed);
				
			} 
			break;
			
			case R.id.contactBtn: {
				Log.i ("Mainactivity", "contact button clicked");
				
				// @lfred: create an intent to open the contact windows
				Intent it = new Intent (Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult (it, 1);
			} break;
			
			default:
				Log.i ("Mainactivity", "unknown button clicked");
			break;
		
		}
	}
	
	@Override
	public void onActivityResult (int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		Log.i ("Mainactivity", "return from activity");
	}
}
