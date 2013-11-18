package pt.feup.stockportfolio;

import pt.feup.stockportfolio.AddQuotesFragment.AddQuoteListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class AddQuotesActivity extends Activity implements AddQuoteListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_layout_details);
		// Show the Up button in the action bar.
		setupActionBar();
		
		if (savedInstanceState == null) {
			AddQuotesFragment fragment = new AddQuotesFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.details_layout, fragment).commit();
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_quotes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void addQuote(String tick, int quantity, double value) {
		Toast.makeText(this, tick+" "+String.valueOf(quantity) , Toast.LENGTH_SHORT).show();
		
	}

}
