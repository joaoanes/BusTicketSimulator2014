package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.HashMap;
import pt.feup.stockportfolio.AddQuotesFragment.AddQuoteListener;
import pt.feup.stockportfolio.HttpHelper.QuoteResult;
import pt.feup.stockportfolio.QuotesFragment.QuotesListener;
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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class QuotesActivity extends Activity implements QuotesListener, AddQuoteListener {
	static HashMap<String, Quote> quotes_map = new HashMap<String, Quote>();
	static ArrayList<Quote> quotes = new ArrayList<Quote>();

	HttpHelper http_helper = new HttpHelper();	
	boolean landscape = false;
	QuotesFragment quotes_fragment;
	static Fragment extra_fragment;
	FragmentManager fragment_manager;
	FrameLayout details_layout;
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Utils.myQuotes.size() == 0)
		{
			Utils.myQuotes.add(new Quote("MSFT", 100));
			Utils.myQuotes.add(new Quote("AAPL", 100));
			Utils.myQuotes.add(new Quote("GE", 100));
			Utils.myQuotes.add(new Quote("FB", 100));
			Utils.myQuotes.add(new Quote("LNKD", 100));
			Utils.myQuotes.add(new Quote("WMT", 100));
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotes);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout = ((DrawerLayout) findViewById(R.id.main_drawer));
		if (quotes.size() == 0)
		{
			quotes.add(new QuoteUpdate(Utils.myQuotes.get(0)));
			quotes.add(new QuoteUpdate(Utils.myQuotes.get(1)));
			quotes.add(new QuoteUpdate(Utils.myQuotes.get(2)));
			quotes.add(new QuoteSeparator());
			
			for (Quote q : Utils.myQuotes)
				quotes.add(q);
			
			quotes.add(new QuoteAdd());

		}
		
		QuoteDetailsFragment frg = (QuoteDetailsFragment) getFragmentManager().findFragmentById(R.id.details);
		
		QuotesAdapter myAdapter = new QuotesAdapter(((Context) this), 0, quotes);
		myAdapter.setFragment(frg);
		mDrawerList.setAdapter(myAdapter);
		
		setUpDrawerToggle();
		
		
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
		getMenuInflater().inflate(R.menu.quote, menu);
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
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onBackPressed() {
		if(extra_fragment != null && !landscape) {
			returnToQuoteFragment();
			return;
		}

		super.onBackPressed();
	}

	@Override //QuotesListener
	public void onQuoteClick(Quote quote) {
	}

	@Override // AddQuoteListener
	public void addQuote(String tick, int quantity, double value) {
		Toast.makeText(this, tick+" "+String.valueOf(quantity) , Toast.LENGTH_SHORT).show();
		Quote quote = new Quote(tick, quantity);

		returnToQuoteFragment();

		quotes_fragment.adapter.add(quote);
	}



	public void startAddQuote() {
		showExtraFragment(new AddQuotesFragment());
	}

	public void showExtraFragment() {
		FragmentTransaction transaction = fragment_manager.beginTransaction();
		transaction.replace(R.id.details_layout, extra_fragment);

		if (!landscape) {
			details_layout.setVisibility(View.VISIBLE);
			transaction.hide(quotes_fragment);

			if(!landscape) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		transaction.commit();
	}

	public void showExtraFragment(Fragment fragment) {
		if(extra_fragment != null) {
			fragment_manager.beginTransaction()
			.remove(extra_fragment)
			.commit();
		}

		extra_fragment = fragment;
		showExtraFragment();
	}


	public void returnToQuoteFragment() {
		FragmentTransaction transaction = fragment_manager.beginTransaction();
		transaction.remove(extra_fragment);

		extra_fragment = null;

		if(!landscape) {
			transaction.show(quotes_fragment);

			details_layout.setVisibility(View.GONE);
		}


		getActionBar().setDisplayHomeAsUpEnabled(false);
		transaction.commit();
	}

	public static boolean hasQuote(String tick) {
		return quotes_map.containsKey(tick);
	}

	public static void addQuote(Quote quote) {
		quotes_map.put(quote.tick, quote);
	}

	public static void updateQuoteValue(QuoteResult quote_result) {
		String tick = quote_result.getTick();
		if(hasQuote(tick)) {
			quotes_map.get(tick).value = quote_result.getValue();
		}
	}

	public static void updateQuoteQuantity(String tick, int change) {
		if(hasQuote(tick)) {
			quotes_map.get(tick).changeQuantity(change);
		}
	}

	public static void removeQuote(String tick) {
		if(hasQuote(tick)) {
			quotes.remove(quotes_map.get(tick));
			quotes_map.remove(tick);
		}
	}

	@SuppressWarnings("deprecation")
	public boolean isLandscape() {
		Display display = getWindowManager().getDefaultDisplay();
		return display.getWidth() > display.getHeight();

	}

	void showQuoteDetails() {
		
	}



}
