package com.example.picfinder;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedViewActivity extends Activity implements OnClickListener {

	int m_global_idx;
	singleData m_myData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView (R.layout.activity_detailed_view);
		getActionBar().setTitle("Details");
		
		Intent intent = getIntent();
		m_global_idx = intent.getIntExtra ("TheSelectedItem", 0);
		m_myData = searchRepo.getRepo().getDataObject (m_global_idx);
		
		// image view
		ImageView iv = (ImageView) findViewById (R.id.imageView1);
		Bitmap bmp = searchRepo.getRepo().getBitmapAt (m_global_idx);
		if (bmp != null)
			iv.setImageBitmap (bmp);
		
		// text view
		String display = 
				"Owner: " + m_myData.getOwner () + "\n" +
				"Date: "  + m_myData.getDate () + "\n" +
				"Description: " + m_myData.getDes ();		
		
		TextView tv = (TextView) findViewById (R.id.textView1);
		tv.setText (display);
		
		// button
		Button b = (Button) findViewById (R.id.button1);
		b.setOnClickListener (this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate (R.menu.detailed_view, menu);
		return true;
	}
	
	@Override
	public void onClick (View v) {
		
		String url = m_myData.getMediumImgUrl ();
		Intent browserIntent = new Intent (Intent.ACTION_VIEW, Uri.parse (url));
		startActivity (browserIntent);
	}
}
