package pt.feup.stockportfolio;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class QuoteDetailsFragment extends Fragment {
	View view;
	
	TextView tick_view;
	TextView quantity_view;
	TextView value_view;
	Button remove_1_button;
	Button remove_10_button;
	Button add_1_button;
	Button add_10_button;
	
	Quote quote;
	
	QuoteDetailsListener listener = dummyListener;
	public interface QuoteDetailsListener {
		public void onQuantityChange();
	}
	
	private static QuoteDetailsListener dummyListener = new QuoteDetailsListener() {
		@Override
		public void onQuantityChange() {}
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
		
		add_10_button = (Button) view.findViewById(R.id.quote_details_add_10);
		add_10_button.setOnClickListener(on_click_listener);
		add_1_button = (Button) view.findViewById(R.id.quote_details_add_1);
		add_1_button.setOnClickListener(on_click_listener);
		remove_1_button = (Button) view.findViewById(R.id.quote_details_remove_1);
		remove_1_button.setOnClickListener(on_click_listener);
		remove_10_button = (Button) view.findViewById(R.id.quote_details_remove_10);
		remove_10_button.setOnClickListener(on_click_listener);
		
		setDetails(quote);
	}
	
	public void setDetails(String tick, int quantity, double value) {
		tick_view.setText(tick);
		quantity_view.setText(String.valueOf(quantity));
		value_view.setText(String.valueOf(value));
	}
	
	public void setDetails(Quote quote) {
		this.quote = quote;
		setDetails(quote.getTick(), quote.getQuantity(), quote.getValue());
	}
	
	OnClickListener on_click_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int change;
			switch(v.getId()) {
				case R.id.quote_details_add_1:
					change = 1;
					break;
				case R.id.quote_details_add_10:
					change = 10;
					break;
				case R.id.quote_details_remove_1:
					change = -1;
					break;
				case R.id.quote_details_remove_10:
					change = -10;
					break;
				default:
					change = 0;
			}
			quote.changeQuantity(change);
			quantity_view.setText(String.valueOf(quote.getQuantity()));
			listener.onQuantityChange();
		}
	}; 
	
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
	}
}
