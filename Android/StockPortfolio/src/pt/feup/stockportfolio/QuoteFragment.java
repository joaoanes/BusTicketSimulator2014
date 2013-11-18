package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pt.feup.stockportfolio.HttpHelper.HistoricResult;
import pt.feup.stockportfolio.HttpHelper.QuoteResult;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QuoteFragment extends Fragment {
	HttpHelper http_helper = new HttpHelper();
	
	
	ListView quotes_list;
	QuoteAdapter adapter;
	View v;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//(new GetTickHistoricTask()).execute(new Void[]{});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v =  inflater.inflate(R.layout.fragment_quote, container, false);
		setListView();
		return v;
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
			
			Utils.createAlertDialog(getActivity(), "Cenas", print);
		}
		
	}
	
	private class GetTickHistoricTask extends AsyncTask<Void, Void, ArrayList<HistoricResult>> {

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
			
			Utils.createAlertDialog(getActivity(), "Cenas", print);
		}
		
	}

	
	void setListView() {
		quotes_list = (ListView) v.findViewById(R.id.quotes_list);
		adapter = new QuoteAdapter();
		quotes_list.setAdapter(adapter);
		quotes_list.setEmptyView(v.findViewById(R.id.quotes_list_empty));
	}
	
	class QuoteAdapter extends ArrayAdapter<Quote> {
		int layout_id;
		public QuoteAdapter() {
			super(QuoteFragment.this.getActivity(), R.layout.row_quote, QuoteActivity.quotes);
			layout_id = R.layout.row_quote;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater =  ((Activity) getContext()).getLayoutInflater();
				row = inflater.inflate(layout_id, null);
			}

			Quote quote = QuoteActivity.quotes.get(position);
			((TextView) row.findViewById(R.id.row_quote_tick)).setText(quote.getTick());
			((TextView) row.findViewById(R.id.row_quote_quantiy)).setText(String.valueOf(quote.getQuantity()));
			((TextView) row.findViewById(R.id.row_quote_value)).setText(String.valueOf(quote.getValue()));
			
			return row;
		}
		
	}

}
