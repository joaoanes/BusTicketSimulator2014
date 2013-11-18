package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.HashMap;

import pt.feup.stockportfolio.AddQuotesFragment.AddQuoteListener;
import pt.feup.stockportfolio.HttpHelper.QuoteResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class QuoteActivity extends Activity implements AddQuoteListener {
	static HashMap<String, Quote> quotes_map = new HashMap<String, Quote>();
	static ArrayList<Quote> quotes = new ArrayList<Quote>();
	
	private boolean landscape = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quote);
		
		if(findViewById(R.id.details_layout) != null) {
			landscape = true;
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

		default:
			break;
		};
		return false;
	}
	
	@Override
	public void addQuote(String tick, int quantity, double value) {
		Toast.makeText(this, tick+" "+String.valueOf(quantity) , Toast.LENGTH_SHORT).show();
		Quote quote = new Quote(tick, quantity);
		QuoteFragment frag = (QuoteFragment) getFragmentManager().findFragmentById(R.id.quote_fragment);
		frag.adapter.add(quote);
	}

	public void startAddQuote() {
		if (landscape) {
			AddQuotesFragment fragment = new AddQuotesFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.details_layout, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, AddQuotesActivity.class);
			startActivity(detailIntent);
		}
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

}
