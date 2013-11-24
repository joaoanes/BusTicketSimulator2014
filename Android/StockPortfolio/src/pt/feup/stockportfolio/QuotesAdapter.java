package pt.feup.stockportfolio;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

public class QuotesAdapter extends ArrayAdapter<Quote> 
{

	public QuotesAdapter(Context _context, int resource,
			ArrayList<Quote> _objects) {
		super(_context, resource, _objects);
		context = _context;
		objects = _objects;
	}

	Context context;
	ArrayList<Quote> objects;
	int portfolioPosition;



	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		boolean skipListener = false;
		View view = convertView;
		if (view == null) 
		{
			LayoutInflater inflator = ((Activity) getContext()).getLayoutInflater();


			if(objects.get(position) instanceof QuoteUpdate)
			{
				if (position == 0)
					view = inflator.inflate(R.layout.quote_update_first, null);
				else
					view = inflator.inflate(R.layout.quote_update, null);
				((TextView) view.findViewById(R.id.quote)).setText(objects.get(position).tick);
				view.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {

						Toast.makeText(context, "Hello from " + position, Toast.LENGTH_SHORT).show();
					}

				});
			}
			else
			{
				if (objects.get(position) instanceof QuoteSeparator)
				{
					view = inflator.inflate(R.layout.my_portfolio, null);
					portfolioPosition = position;
					view.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							Toast.makeText(context, "Hello from the portfolio at " + portfolioPosition, Toast.LENGTH_SHORT).show();
						}

					});
				} 
				else
				{
					view = inflator.inflate(R.layout.quote_real, null);

					((TextView) view.findViewById(R.id.quote)).setText(objects.get(position).tick);
					view.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							Toast.makeText(context, "Hello from " + position, Toast.LENGTH_SHORT).show();
						}

					});
					}
			}
		}


		return view;

	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position > 1)
			return 0;
		else return 1;
	}


}
