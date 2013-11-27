package pt.feup.stockportfolio;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

		Animation fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
		fadeOutAnimation.setRepeatCount(0);
		fadeOutAnimation.setFillAfter(true);

		for (View v : swipable)
		{
			v.startAnimation(fadeOutAnimation);
		}
		fadeOutAnimation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				int i = 0;
				for (i = 0; i < objects.size(); )
				{
					if (objects.get(i) instanceof QuoteUpdate)
					{
						remove(objects.get(i));
						continue;
					}
					++i;
				}
				notifyDataSetChanged();

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

		});



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
			final LayoutInflater inflator = ((Activity) getContext()).getLayoutInflater();


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
							
							class QuoteAsync extends AsyncTask<Object, Void, Void>
							{
								Quote quote = null;
								View view = null;
								
								@Override
								protected Void doInBackground(Object... arg0) {
									quote = (Quote) arg0[0];
									view = (View) arg0[1];
									if (!quote.isUpdated)
										quote.update();
									return null;
								}
								@Override
								protected void onPostExecute(Void result) {
									view.findViewById(R.id.updating).setVisibility(View.GONE);
									if (quote.isUpdated)
									((TextView) view.findViewById(R.id.close)).setText("" + quote.getLast().close);
									
								}

							}
							
							@Override
							public void onClick(View v) {

								final AlertDialog alertDialog;
								AlertDialog.Builder builder = new AlertDialog.Builder(context);
								final View myView = inflator.inflate(R.layout.fragment_add_quote_2, null);
								final int[] share_number = {0};
								final EditText ticker = (EditText) myView.findViewById(R.id.ticker);
								final TextView shares = (TextView) myView.findViewById(R.id.shares);


								builder.setView(myView);


								alertDialog = builder.create();

								myView.findViewById(R.id.increaseshares).setOnClickListener(new OnClickListener(){

									@Override
									public void onClick(View v) {

										share_number[0]++;
										shares.setText("" + share_number[0]);
									}

								});
								
								myView.findViewById(R.id.increaseshares).setOnLongClickListener(new OnLongClickListener(){

									@Override
									public boolean onLongClick(View v) {
										// TODO Auto-generated method stub
										return false;
									}
									
								});
								
								myView.findViewById(R.id.decreaseshares).setOnClickListener(new OnClickListener(){

									@Override
									public void onClick(View v) {
										if (share_number[0] == 0)
											return;
										share_number[0]--;
										shares.setText("" + share_number[0]);
									}

								});


								myView.findViewById(R.id.accept).setOnClickListener(new OnClickListener(){

									
									Quote q = new Quote("NULL", 0);
									@Override
									public void onClick(View v) {
										if (q.isUpdated)
										{
										Utils.myQuotes.add(q);

										Quote a = objects.get(objects.size()-1);
										objects.remove(objects.size()-1);
										objects.add(q);
										objects.add(a);

										notifyDataSetChanged();
										alertDialog.dismiss();
										}
										else
										{
											myView.findViewById(R.id.updating).setVisibility(View.VISIBLE);
											q = new Quote(ticker.getText().toString(), share_number[0]);
											
											Object[] quot = { q , myView };
											new QuoteAsync().execute(quot);
										}
									}

								});

								alertDialog.show();
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
								((TextView) myView.findViewById(R.id.value)).setText("" + quote.getLast().close);
								if (quote.getLast().close < quote.getFromLast(1).close)
								{
									((RelativeLayout) myView.findViewById(R.id.tick_box)).setBackgroundResource(R.drawable.border_red);
									((TextView) myView.findViewById(R.id.value)).setTextColor(Color.parseColor("#ed1c24"));
								}
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
						

						final boolean landscape = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

						view.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								if (!landscape)	
									new Handler().postDelayed(new Runnable() {


										@Override
										public void run() {
											((QuotesActivity) context).mDrawerLayout.closeDrawer(Gravity.LEFT);
										}
									}, 500);
								if (quote.isUpdated)
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
		return 5;
	}

	@Override
	public int getItemViewType(int position)
	{

		Quote quote = objects.get(position);


		if (quote instanceof QuoteSeparator)
			return 2;

		if (quote instanceof QuoteUpdate)

		{
			if (position == 0)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
		if (quote instanceof QuoteAdd)
			return 4;

		return 3;
	}

	public void setFragment(QuoteDetailsFragment frg) {
		fragment = frg;

	}


}