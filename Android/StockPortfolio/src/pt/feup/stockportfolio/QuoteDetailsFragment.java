package pt.feup.stockportfolio;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuoteDetailsFragment extends Fragment {
	View view;
	
	TextView tick_view;
	TextView quantity_view;
	TextView value_view;
	LinearLayout remove_1_button;
	LinearLayout add_1_button;
	GraphView graph = null;
	
	Quote quote = null;
	
	
	class QuoteUpdater extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) {
			if(!quote.isUpdated) {
				try {
					quote.update();
				} catch (NoInternetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			addGraph();
	     }
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (quote == null)
			quote = Utils.myQuotes.get(0);
		Log.i("HELLO FRAGMENT", "Creating DETAIL fragment " + this.hashCode());
	}

	public void addGraph() {
		view.findViewById(R.id.graphProgress).setVisibility(View.GONE);
		graph = new GraphView(getActivity(), null, quote);
		((LinearLayout) view.findViewById(R.id.insertGraphHere)).addView(graph);
		Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		fadeInAnimation.setRepeatCount(0);
		fadeInAnimation.setFillAfter(true);

		setDetails(quote);
		graph.startAnimation(fadeInAnimation);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_quote_details, container, false);
		setViews();
		return view;
	}
	
	public void setViews() {
		quantity_view = (TextView) view.findViewById(R.id.shares);
		value_view = (TextView) view.findViewById(R.id.personalworth);
		
		add_1_button = (LinearLayout) view.findViewById(R.id.increaseshares);
		add_1_button.setOnClickListener(on_click_listener);
		remove_1_button = (LinearLayout) view.findViewById(R.id.decreaseshares);
		remove_1_button.setOnClickListener(on_click_listener);;
		
		if(!quote.isUpdated)
		{
			view.setBackgroundColor(ColorFactory.serve);
			
			new QuoteUpdater().execute(new Void[1]);
		}
		else
		{
			addGraph();
		}
		
		
	}
	
	public void setDetails(String tick, int quantity, double value) {
		
		quantity_view.setText(String.valueOf(quantity));
		String val = String.valueOf(value);
		val = val.substring(0 ,val.indexOf(".")+3 > val.length() ? val.indexOf(".") + 2 : val.indexOf(".") + 2 );
		
		value_view.setText(val);
		
		if (graph != null)
		graph.changeQuote(quote);
		BigDecimal bd = new BigDecimal(quote.getLast().close * quote.quantity);
		bd.setScale(2, BigDecimal.ROUND_FLOOR);
		value_view.setText("$" + bd.doubleValue());
		view.setBackgroundColor(quote.color);
		
		double percent =  quote.getPercentage();
		String percentage_string = String.valueOf(percent);
		int dotPosition = percentage_string.indexOf(".");
		int offset = 3;
		if ((dotPosition + 3) >= percentage_string.length() )
			offset = 2;
		percentage_string = percentage_string.substring(0, percentage_string.indexOf(".") +  offset);
		((TextView) view.findViewById(R.id.percent)).setText("" + percentage_string + "%");
		//((TextView) view.findViewById(R.id.close)).setText("$" + quote.getLast().close);
		((TextView) view.findViewById(R.id.close)).setText("$" + quote.value);
		((TextView) view.findViewById(R.id.name)).setText(quote.name.substring(0, (quote.name.length() < 14) ? quote.name.length() : 14));
		if (percent < 0)
		{
			((TextView) view.findViewById(R.id.percent)).setTextColor(Color.parseColor("#ed1c24"));
			((ImageView) view.findViewById(R.id.arrow)).setImageResource(R.drawable.downarrow_03);
		}	
		else
		{
			((TextView) view.findViewById(R.id.percent)).setTextColor(Color.parseColor("#8cc63e"));
			((ImageView) view.findViewById(R.id.arrow)).setImageResource(R.drawable.greenarrow_03);	
		}
		((TextView) view.findViewById(R.id.close)).setTextColor(quote.color);
		((LinearLayout) view.findViewById(R.id.increaseshares)).setBackgroundColor(quote.color);

		((LinearLayout) view.findViewById(R.id.decreaseshares)).setBackgroundColor(quote.color);
		
	}
	
	public void setDetails(Quote quote) {

		Log.i("HELLO FRAGMENT", "DETAIL fragment details for quote " + quote.tick + " " + this.hashCode());
		this.quote = quote;
		if (quote != null)
		setDetails(quote.tick, quote.quantity, quote.value);
	}
	
	OnClickListener on_click_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int change;
			switch(v.getId()) {
				case R.id.increaseshares:
					change = 1;
					break;
				case R.id.decreaseshares:
					change = -1;
					break;
				default:
					change = 0;
			}
			quote.changeQuantity(change);
			quantity_view.setText(String.valueOf(quote.quantity));
			value_view.setText("$" + quote.getLast().close * quote.quantity);
			
		}
	}; 
	
}
