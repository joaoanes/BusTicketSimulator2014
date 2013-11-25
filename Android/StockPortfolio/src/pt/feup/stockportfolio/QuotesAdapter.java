package pt.feup.stockportfolio;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
	ArrayList<View> swipable = new ArrayList<View>();
	private QuoteDetailsFragment fragment;

	public void hideSwipables()
	{

		int i = 0;
		for (i = 0; i < objects.size(); )
		{
			if (objects.get(i) instanceof QuoteUpdate)
			{
				this.remove(objects.get(i));
				continue;
			}
			++i;
		}
		Toast.makeText(getContext(), "TOAST! Also hidden", Toast.LENGTH_SHORT).show();
		super.notifyDataSetChanged();


	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final Quote quote = objects.get(position);
		if (quote == null)
			return new View(context);
		View view = convertView;
		if (view == null) 
		{
			Log.e("HELLO QUOTES", "Refreshing a view!");
			LayoutInflater inflator = ((Activity) getContext()).getLayoutInflater();


			if(quote instanceof QuoteUpdate)
			{
				if (position == 0)
					view = inflator.inflate(R.layout.quote_update_first, null);
				else
					view = inflator.inflate(R.layout.quote_update, null);
				((TextView) view.findViewById(R.id.quote)).setText(quote.tick);
				((RelativeLayout) view.findViewById(R.id.update_background)).setBackgroundColor(quote.color);
				view.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						hideSwipables();
					}


				});



				swipable.add(view);
			}
			else
			{
				if (quote instanceof QuoteSeparator)
				{
					view = inflator.inflate(R.layout.my_portfolio, null);
					view.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							Toast.makeText(context, "Hello from the portfolio at " + position, Toast.LENGTH_SHORT).show();
						}

					});
				} 
				else
				{
					if (quote instanceof QuoteAdd)
					{
						view = inflator.inflate(R.layout.add_quote, null);
						view.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {

								Toast.makeText(context, "Hello from add quote at " + position, Toast.LENGTH_SHORT).show();
							}

						});
					}
					else
					{
						final QuotesAdapter hello = this;
						class GraphAsync extends AsyncTask<Object, Void, Void>
						{
							View myView = null;
							Quote quote = null;
							@Override
							protected Void doInBackground(Object... arg0) {

								myView = (View) arg0[0];
								quote = (Quote) arg0[1];
								if (!quote.isUpdated)
									quote.update();
								return null;
							}
							@Override
							protected void onPostExecute(Void result) {
								((RelativeLayout) myView.findViewById(R.id.real_background)).setBackgroundColor(quote.color);
								((TextView) myView.findViewById(R.id.value)).setText("" + quote.history.get(quote.history.size()-1).close);
								GraphViewSmall mine = new GraphViewSmall(context, null, quote);
								((LinearLayout)myView.findViewById(R.id.graph)).addView(mine);
								Animation fadeInAnimation = AnimationUtils.loadAnimation(hello.getContext(), R.anim.fade_in);
								fadeInAnimation.setRepeatCount(0);
								fadeInAnimation.setFillAfter(true);

								mine.startAnimation(fadeInAnimation);
								((LinearLayout)myView.findViewById(R.id.graph)).findViewById(R.id.progressBar).setVisibility(View.GONE);

								hello.notifyDataSetChanged();
							}

						}
						view = inflator.inflate(R.layout.quote_real, null);

						new GraphAsync().execute(view, quote);
						((TextView) view.findViewById(R.id.quote)).setText(quote.tick);
						//TODO colors and stuff



						view.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								Toast.makeText(context, "Hello from " + position, Toast.LENGTH_SHORT).show();
								fragment.setDetails(quote);
							}

						});
					}}
			}
		}


		return view;

	}

	@Override
	public int getViewTypeCount()
	{
		return 4;
	}

	@Override
	public int getItemViewType(int position)
	{

		Quote quote = objects.get(position);
		
		
		if (quote instanceof QuoteSeparator)
			return 2;
		
		if (quote instanceof QuoteUpdate)
			if (position == 0)
			return 0;
			else
				return 1;
		
		
		return 3;
	}

	public void setFragment(QuoteDetailsFragment frg) {
		fragment = frg;
		
	}


}
