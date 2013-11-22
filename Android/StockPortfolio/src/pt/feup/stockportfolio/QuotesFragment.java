package pt.feup.stockportfolio;

import pt.feup.stockportfolio.QuoteDetailsFragment.QuoteDetailsListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QuotesFragment extends Fragment {

	ListView quotes_list;
	QuotesAdapter adapter;
	View v;
	
	QuotesListener listener = dummyListener;
	public interface QuotesListener {
		public void onQuoteClick(Quote quote);
	}
	
	private static QuotesListener dummyListener = new QuotesListener() {
		@Override
		public void onQuoteClick(Quote quote) {}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v =  inflater.inflate(R.layout.fragment_quotes, container, false);
		setListView();
		return v;
	}

	void setListView() {
		quotes_list = (ListView) v.findViewById(R.id.quotes_list);
		adapter = new QuotesAdapter();
		quotes_list.setAdapter(adapter);
		quotes_list.setEmptyView(v.findViewById(R.id.quotes_list_empty));
		quotes_list.setOnItemClickListener(click_quote);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof QuotesListener)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		listener = (QuotesListener) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = dummyListener;
	}
	
	class QuotesAdapter extends ArrayAdapter<Quote> {
		int layout_id;
		public QuotesAdapter() {
			super(QuotesFragment.this.getActivity(), R.layout.row_quote, QuotesActivity.quotes);
			layout_id = R.layout.row_quote;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater =  ((Activity) getContext()).getLayoutInflater();
				row = inflater.inflate(layout_id, null);
			}

			Quote quote = QuotesActivity.quotes.get(position);
			((TextView) row.findViewById(R.id.row_quote_tick)).setText(quote.getTick());
			((TextView) row.findViewById(R.id.row_quote_quantiy)).setText(String.valueOf(quote.getQuantity()));
			((TextView) row.findViewById(R.id.row_quote_value)).setText(String.valueOf(quote.getValue()));
			
			return row;
		}
		
	}
	
	OnItemClickListener click_quote = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			view.setSelected(true);
			listener.onQuoteClick(adapter.getItem(position));
		}
		
	};

}
