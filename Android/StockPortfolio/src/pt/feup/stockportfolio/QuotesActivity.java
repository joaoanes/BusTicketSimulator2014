package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pt.feup.stockportfolio.AddQuotesFragment.AddQuoteListener;
import pt.feup.stockportfolio.HttpHelper.HistoricResult;
import pt.feup.stockportfolio.HttpHelper.QuoteResult;
import pt.feup.stockportfolio.QuoteDetailsFragment.QuoteDetailsListener;
import pt.feup.stockportfolio.QuotesFragment.QuotesListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class QuotesActivity extends Activity implements QuotesListener, AddQuoteListener, QuoteDetailsListener {
	static HashMap<String, Quote> quotes_map = new HashMap<String, Quote>();
	static ArrayList<Quote> quotes = new ArrayList<Quote>();

	HttpHelper http_helper = new HttpHelper();	
	
	boolean landscape = false;
	QuotesFragment quotes_fragment;
	static Fragment extra_fragment;
	FragmentManager fragment_manager;
	FrameLayout details_layout;
	
	static Quote selected_quote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotes);

		details_layout = (FrameLayout) findViewById(R.id.details_layout);
		fragment_manager = getFragmentManager();
		landscape = isLandscape();

		quotes_fragment = (QuotesFragment) fragment_manager.findFragmentById(R.id.quote_fragment);
		
		if(extra_fragment != null) {
			showExtraFragment();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_quote:
				startAddQuote();
				return true;
			case android.R.id.home:
				if(extra_fragment != null) {
					returnToQuoteFragment();
					return true;
				}
				break;
			default:
				break;
		};
		return false;
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
		selected_quote = quote;
		showQuoteDetails();
	}

	@Override // AddQuoteListener
	public void addQuote(String tick, int quantity, double value) {
		Toast.makeText(this, tick+" "+String.valueOf(quantity) , Toast.LENGTH_SHORT).show();
		Quote quote = new Quote(tick, quantity);
		
		returnToQuoteFragment();
		
		quotes_fragment.adapter.add(quote);
	}
	
	@Override
	public void onQuantityChange() {
		quotes_fragment.adapter.notifyDataSetChanged();
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
		quotes_map.put(quote.getTick(), quote);
	}

	public static void updateQuoteValue(QuoteResult quote_result) {
		String tick = quote_result.getTick();
		if(hasQuote(tick)) {
			quotes_map.get(tick).setValue(quote_result.getValue());
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
		if(extra_fragment instanceof QuoteDetailsFragment) {
			((QuoteDetailsFragment) extra_fragment).setDetails(selected_quote);
		} else {
			showExtraFragment(new QuoteDetailsFragment());
		}
	}
	
	public class GetQuotesValuesTask extends AsyncTask<Void, Void, ArrayList<QuoteResult>> {

		@Override
		protected ArrayList<QuoteResult> doInBackground(Void... params) {
			String[] quotes = {"GOOG", "AAPL", "DELL"};
			return http_helper.getTickValues(new ArrayList<String>(Arrays.asList(quotes)));
		}

		@Override
		protected void onPostExecute(ArrayList<QuoteResult> result) {
			String print = "";
			for(QuoteResult quote : result) {
				print += quote.toString() + "\n";
			}
			
			Utils.createAlertDialog(QuotesActivity.this, "Cenas", print);
		}
		
	}
	
	public class GetTickHistoricTask extends AsyncTask<Void, Void, ArrayList<HistoricResult>> {

		@Override
		protected ArrayList<HistoricResult> doInBackground(Void... params) {
			return http_helper.getHistoric("GOOG");
		}

		@Override
		protected void onPostExecute(ArrayList<HistoricResult> result) {
			String print = "";
			for(HistoricResult historic : result) {
				print += historic.toString() + "\n";
			}
			
			Utils.createAlertDialog(QuotesActivity.this, "Cenas", print);
		}
		
	}

}
