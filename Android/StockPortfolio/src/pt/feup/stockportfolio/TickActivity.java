package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.Arrays;

import pt.feup.stockportfolio.HttpHelper.HistoricResult;
import pt.feup.stockportfolio.HttpHelper.QuoteResult;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

public class TickActivity extends Activity {
	HttpHelper http_helper = new HttpHelper();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tick);
		
		(new GetTickHistoricTask()).execute(new Void[]{});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tick, menu);
		return true;
	}
	
	private class GetQuotesValuesTask extends AsyncTask<Void, Void, ArrayList<QuoteResult>> {

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
			
			Utils.createAlertDialog(TickActivity.this, "Cenas", print);
		}
		
	}
	
	private class GetTickHistoricTask extends AsyncTask<Void, Void, ArrayList<HistoricResult>> {

		@Override
		protected ArrayList<HistoricResult> doInBackground(Void... params) {
			return http_helper.getHistoric("GOOG");//, 9,5,2013,9,19,2013);
		}

		@Override
		protected void onPostExecute(ArrayList<HistoricResult> result) {
			String print = "";
			for(HistoricResult historic : result) {
				print += historic.toString() + "\n";
			}
			
			Utils.createAlertDialog(TickActivity.this, "Cenas", print);
		}
		
	}

}
