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
import android.widget.LinearLayout;
import android.widget.TextView;

public class PortfolioFragment extends Fragment {
	View view;
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
		
		((LinearLayout) view.findViewById(R.id.pieView1)).addView(pie);
		LinearLayout sharesLayout = ((LinearLayout) view.findViewById(R.id.quotes_list));
		View v;
		double worth = 0;
		for (final Quote q : Utils.myQuotes)
		{
			worth += q.value*q.quantity;
			if (q.quantity == 0)
				continue;
			v = inflater.inflate(R.layout.quote_shares, null);
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
		
		((TextView) view.findViewById(R.id.worth)).setText("$" + worth);
		
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
