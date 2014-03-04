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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.*;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.*;
import java.util.StringTokenizer;

// Sample code: show image from internet in the image viewer
// Note: Need user permission -> INTERNET
// try {
// Bitmap bt = BitmapFactory.decodeStream (InputStream new URL (url).getContent ());
// } catch (Exception e) {
// }

public class MainActivity extends Activity implements OnClickListener {
	
	final static int 	M_REQCODE_CONTACT = 1;
	final static String M_LOG_TAG = "@lfred_main";
	
	static ProgressDialog	m_progDialog;
	static searchRepo		m_repo;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		Button searchBtn = (Button) findViewById (R.id.searchBtn);
		searchBtn.setOnClickListener (this);
		
		Button contactBtn = (Button) findViewById (R.id.contactBtn);
		contactBtn.setOnClickListener (this);
		
		// @lfred: get the repo
		m_repo = searchRepo.getRepo ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
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
				searchRepo.getRepo ().resetRepo ();
				
				EditText text = (EditText) findViewById (R.id.keywordText);
				String keyword = text.getText ().toString ();
				Log.i (M_LOG_TAG, keyword);
				
				
				if (keyword.length () == 0) {
					Toast t  = Toast.makeText (MainActivity.this, "Empty Keyword", Toast.LENGTH_LONG);
					t.show ();
				} else {
					
					// tokenizer
					StringTokenizer st = new StringTokenizer (keyword); 
					while (st.hasMoreTokens ()) {
				         searchRepo.getRepo ().setKeyword (st.nextToken ().trim());
				    }
					
					openGridList ();
				}
			} 
			break;
			
			case R.id.contactBtn: {
				Log.i (M_LOG_TAG, "contact button clicked");
				
				// @lfred: create an intent to open the contact windows
				Intent it = new Intent (Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult (it, M_REQCODE_CONTACT);
			} 
			break;
			
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
					// @lfred: clear the repo
					searchRepo.getRepo ().resetRepo ();
					
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
		                //startSearchTask (name);
		                searchRepo.getRepo ().setKeyword (name);
		                openGridList ();
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
	
	/*
	void startSearchTask (String key) {
		m_progDialog = 
			ProgressDialog.show (MainActivity.this, "Please wait ...", "Searching Data ...", true);					
		m_progDialog.setCancelable (false);
			
		// start the worker thread
		new WorkThread (this).execute (key);
	}
	*/
	
	public void openGridList () {
		
		Log.i (M_LOG_TAG, "Start gridview");
		
		Intent it = new Intent ();
		it.setClass (MainActivity.this, GridListActivity.class);
		startActivity (it);
	}	
}
