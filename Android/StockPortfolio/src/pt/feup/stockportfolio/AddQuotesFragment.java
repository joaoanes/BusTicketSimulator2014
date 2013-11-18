package pt.feup.stockportfolio;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddQuotesFragment extends Fragment {
	View view;
	
	EditText tick_view;
	EditText value_view;
	EditText quantity_view;
	Button add_button;
	
	AddQuoteListener listener = dummyListener;
	public interface AddQuoteListener {
		public void addQuote(String tick, int quantity, double value);
	}
	
	private static AddQuoteListener dummyListener = new AddQuoteListener() {
		@Override
		public void addQuote(String tick, int quantity, double value) {
			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_add_quote, container, false);
		setViews();
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof AddQuoteListener)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		listener = (AddQuoteListener) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// Reset the active callbacks interface to the dummy implementation.
		listener = dummyListener;
	}
	
	public void setViews() {
		tick_view = (EditText) view.findViewById(R.id.add_quote_tick);
		value_view = (EditText) view.findViewById(R.id.add_quote_value);
		quantity_view = (EditText) view.findViewById(R.id.add_quote_quantity);
		add_button = (Button) view.findViewById(R.id.add_quote_button);
		
		add_button.setOnClickListener(addQuote);
	}
	
	OnClickListener addQuote = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String tick = tick_view.getText().toString();
			int quantity = 0;
			double value = 0.0;
			
			try {
				quantity = Integer.parseInt(quantity_view.getText().toString());
				value = Double.parseDouble(value_view.getText().toString());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			listener.addQuote(tick, quantity, value);
		}
	};

}
