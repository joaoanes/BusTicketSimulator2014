package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PortfolioFragment extends Fragment {
	View view;
	GraphView graph;
	PieView pie;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("HELLO FRAGMENT", "Creating PORTFOLIO fragment " + this.hashCode());
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_portfolio, container, false);
		pie = new PieView(getActivity());
		Quote port = Utils.getPortfolioQuote();
		graph = new GraphView(getActivity(), null, port);
		pie.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pie.quantityMode = !pie.quantityMode;
				pie.invalidate();
			}
			
		});
		
		
		
		LinearLayout sharesLayout = ((LinearLayout) view.findViewById(R.id.quotes_list));
		View v;
		double worth = 0;
		ArrayList<Quote> withShares = new ArrayList<Quote>();
		for (Quote q : Utils.myQuotes)
		{
			if (q.quantity > 0)
				withShares.add(q);
		}
		
		if (withShares.size() == 0)
		{
			((TextView) view.findViewById(R.id.worth)).setText("no $");
			
			view.findViewById(R.id.worthless).setVisibility(View.GONE);
			view.findViewById(R.id.junk).setVisibility(View.GONE); //hah
			return view;
			
		}
		
		((RelativeLayout) view.findViewById(R.id.graphHolder)).addView(graph);
		
		((LinearLayout) view.findViewById(R.id.pieView1)).addView(pie);
		
		
		for (final Quote q : withShares)
		{
			worth += q.value*q.quantity;
			v = inflater.inflate(R.layout.quote_shares, null);
			((LinearLayout) v).setGravity(Gravity.CENTER_HORIZONTAL);
			((TextView) v.findViewById(R.id.quote)).setText(q.tick);
			v.findViewById(R.id.real_background).setBackgroundColor(q.color);

			((TextView) v.findViewById(R.id.shares)).setText("" + q.quantity);
			v.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					pie.setActivated(q);
					
				}
				
			});
			sharesLayout.addView(v);
		}
		
		((TextView) view.findViewById(R.id.worth)).setText("$" + String.valueOf(worth).substring(0, String.valueOf(worth).indexOf(".")+3));
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		}

	@Override
	public void onDetach() {
		super.onDetach();
		
	}
}
