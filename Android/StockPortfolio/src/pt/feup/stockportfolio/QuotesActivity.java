package pt.feup.stockportfolio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import pt.feup.stockportfolio.HttpHelper.QuoteResult;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class QuotesActivity extends Activity {
	HttpHelper http_helper = new HttpHelper();	
	boolean landscape = false;

	static Fragment extra_fragment;
	FragmentManager fragment_manager;
	FrameLayout details_layout;
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	QuoteDetailsFragment d_frag;
	PortfolioFragment p_frag;

	boolean inspectingQuotes = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		if (Utils.myQuotes.size() == 0)
		{
			Utils.myQuotes.add(new Quote("GOOG", 0));
			Utils.myQuotes.add(new Quote("MSFT", 0));
			Utils.myQuotes.add(new Quote("AAPL", 0));

		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotes);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout = ((DrawerLayout) findViewById(R.id.main_drawer));
		if (quotes.size() == 0)
		{
			for (QuoteUpdate q : Utils.myUpdates)
			{
				quotes.add(q);
			}
			Utils.myUpdates.clear();
			quotes.add(new QuoteSeparator());
			
			for (Quote q : Utils.myQuotes)
				quotes.add(q);
			
			quotes.add(new QuoteAdd());

		}
		
		p_frag = new PortfolioFragment();
		d_frag = new QuoteDetailsFragment();
		showExtraFragment(d_frag);
		
	
		QuotesAdapter myAdapter = new QuotesAdapter(((Context) this), 0, quotes);
		
		mDrawerList.setAdapter(myAdapter);
		
		setUpDrawerToggle();
		
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		Log.i("Save", "SAVING");
		save();
		super.onDestroy();
	}

	private void save() {
		try {
			FileOutputStream fos = openFileOutput(Utils.FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(Utils.myQuotes);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void setUpDrawerToggle(){
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			return;
	    ActionBar actionBar = getActionBar();

	    // ActionBarDrawerToggle ties together the the proper interactions
	    // between the navigation drawer and the action bar app icon.
	    mDrawerToggle = new ActionBarDrawerToggle(
	            this,                             /* host Activity */
	            mDrawerLayout,                    /* DrawerLayout object */
	            R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
	            R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
	            R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
	    ) {
	        @Override
	        public void onDrawerClosed(View drawerView) {
	            invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
	        }

	        @Override
	        public void onDrawerOpened(View drawerView) {
	            invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
	        }
	        
	    };
	    
	    

	    // Defer code dependent on restoration of previous instance state.
	    // NB: required for the drawer indicator to show up!
	    mDrawerLayout.post(new Runnable() {
	        @Override
	        public void run() {
	            mDrawerToggle.syncState();
	            
	        }
	    });

	    mDrawerLayout.setDrawerListener(mDrawerToggle);

	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.quote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		/*
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		*/
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onBackPressed() {
	
		save();
		super.onBackPressed();
	}


	public void showExtraFragment() {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.content_frame, extra_fragment);

		
		transaction.commit();
	}

	public void showExtraFragment(Fragment fragment) {


		extra_fragment = fragment;
		showExtraFragment();
	}




	void showQuoteDetails() {
		
	}



}
