package pt.feup.stockportfolio;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuoteDetailsFragment extends Fragment {
	View view;
	
	TextView tick_view;
	TextView quantity_view;
	TextView value_view;
	
	Quote quote;
	
	QuoteDetailsListener listener = dummyListener;
	public interface QuoteDetailsListener {
		
	}
	
	private static QuoteDetailsListener dummyListener = new QuoteDetailsListener() {
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		quote = QuotesActivity.selected_quote;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_quote_details, container, false);
		setViews();
		return view;
	}
	
	public void setViews() {
		tick_view = (TextView) view.findViewById(R.id.quote_details_tick);
		quantity_view = (TextView) view.findViewById(R.id.quote_details_quantity);
		value_view = (TextView) view.findViewById(R.id.quote_details_value);
		
		tick_view.setText(quote.getTick());
		quantity_view.setText(String.valueOf(quote.getQuantity()));
		value_view.setText(String.valueOf(quote.getValue()));
	}
	
	public void setDetails(String tick, int quantity, double value) {
		tick_view.setText(tick);
		quantity_view.setText(String.valueOf(quantity));
		value_view.setText(String.valueOf(value));
	}
	
	public void setDetails(Quote quote) {
		setDetails(quote.getTick(), quote.getQuantity(), quote.getValue());
	}
	
	/*
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof QuoteDetailsListener)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		listener = (QuoteDetailsListener) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = dummyListener;
	}*/
}
