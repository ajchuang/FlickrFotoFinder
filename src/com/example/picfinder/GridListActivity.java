package com.example.picfinder;

import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.widget.GridView;

public class GridListActivity extends Activity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_grid_list);
	
		GridView gridview = (GridView) findViewById (R.id.gridview_main);
	    gridview.setAdapter (new ImageAdapter (this));
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid_list, menu);
		return true;
	}

}
