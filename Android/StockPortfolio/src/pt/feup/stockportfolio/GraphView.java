package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import pt.feup.stockportfolio.HttpHelper.HistoricResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GraphView extends View
{

	Paint mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint bluerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint blueDottedPaint = new Paint();
	Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint smallTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint bigTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint defaultPaint = new Paint();
	Paint pointsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint objectiveTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint whitePaint = new Paint();

	Bitmap hp_icon;
	Bitmap objectives_icon;

	float slidex;
	float defaultSlide;
	float diffX = 0;
	float lastPosX = -1;
	Context ctxt;
	Path pointsGraph = null;
	int touch_events = 0;

	Quote quote = Utils.myQuotes.get(0);
	
	ArrayList<Integer> levelUps = new ArrayList<Integer>();
	static Bitmap bufferedGraph;
	Canvas bufferedGraphCanvas;

	boolean swipeDecay = false;
	private boolean resetted = false;
	
	class TestUpdater extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			if (!quote.isUpdated)
				quote.update();
			return null;
		}
		
	}
	
	public GraphView(Context context, AttributeSet attrs)
	{
		
		super(context, attrs);
		Options opts = new Options();
		opts.inDither = false; 
		opts.inPurgeable = true; 
		opts.inInputShareable = true; 
		opts.inTempStorage = new byte[32 * 1024];

		ExtraUtils.setMetrics(getResources().getDisplayMetrics());
		ctxt = context;
		
		this.setBackgroundColor(Color.parseColor("#efefef"));
		bluerPaint.setColor(Color.parseColor("#587C9B"));
		
		blueDottedPaint.setStyle(Style.STROKE);
		blueDottedPaint.setColor(Color.parseColor("#efefef"));
		textPaint.setColor(Color.parseColor("#eae9f2"));
		textPaint.setTextSize(ExtraUtils.dp2px(14));
		textPaint.setTextAlign(Align.CENTER);

		smallTextPaint.setColor(Color.parseColor("#587C9B"));

		smallTextPaint.setTypeface(Typeface.createFromAsset(
				context.getAssets(), "fonts/Roboto-Light.ttf"));
		smallTextPaint.setTextSize(ExtraUtils.dp2px(8));
		bigTextPaint.setColor(Color.parseColor("#587C9B"));
		bigTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Regular.ttf"));
		bigTextPaint.setTextSize(ExtraUtils.dp2px(11));

		pointsPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Regular.ttf"));
		pointsPaint.setTextSize(ExtraUtils.dp2px(60));
		pointsPaint.setColor(Color.parseColor("#000000"));
		pointsPaint.setTextAlign(Align.LEFT);

		objectiveTextPaint.setTypeface(Typeface.createFromAsset(
				context.getAssets(), "fonts/Roboto-Light.ttf"));
		objectiveTextPaint.setTextSize(ExtraUtils.dp2px(20));
		objectiveTextPaint.setColor(Color.WHITE);

		whitePaint.setColor(Color.WHITE);

		blueDottedPaint.setPathEffect(new DashPathEffect(new float[]
				{ 10, 20 }, 0));
		
		
		
		buildGraph();
	}

	private void buildGraph() {
		Void[] args = {};
		if (!quote.isUpdated)
			try {
			new TestUpdater().execute(args).get();
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		mainPaint.setColor(quote.color);
		int[] width =
			{ ExtraUtils.dp2px(50) };
		if (pointsGraph == null)
			pointsGraph = PathFactory.generatePath(
					quote, levelUps, width);
		if (bufferedGraph == null)
		{

			bufferedGraph = Bitmap.createBitmap(width[0] < ExtraUtils
					.getScreenWidth() ? (int) ExtraUtils.getScreenWidth()
							: width[0], ExtraUtils.dp2px(175), Bitmap.Config.ARGB_8888);
			resetted = true;
		}

		if (bufferedGraph.getWidth() != width[0])
		{
			bufferedGraph.recycle();
			bufferedGraph = null;
			bufferedGraph = Bitmap.createBitmap(width[0] < ExtraUtils
					.getScreenWidth() ? (int) ExtraUtils.getScreenWidth()
							: width[0], ExtraUtils.dp2px(175), Bitmap.Config.ARGB_8888);
			resetted = true;
		}
		bufferedGraphCanvas = new Canvas(bufferedGraph);
		if (resetted)
		{
			Log.w("HELLO GRAPH", "Resetted graph..");
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#eae9f2"));
			bufferedGraphCanvas.drawRect(0, 0, bufferedGraph.getWidth(),
					bufferedGraph.getHeight(), paint);
		}
		drawGraph();
		defaultSlide = -(bufferedGraph.getWidth() - ExtraUtils.getScreenWidth());
		slidex = defaultSlide;
		
	}

	public void drawGraph()
	{
		// bufferedGraphCanvas.drawLine(0, KeepUtils.dp2px(50),
		// bufferedGraphCanvas.getWidth(), KeepUtils.dp2px(25),
		// blueDottedPaint);
		for (int i = 1; i < levelUps.size() + 1; ++i)
		{
			bufferedGraphCanvas.drawCircle(
					levelUps.get(i - 1) + ExtraUtils.dp2px(25), 80, 15,
					mainPaint);
			bufferedGraphCanvas.drawText(Integer.toString(i),
					levelUps.get(i - 1) + ExtraUtils.dp2px(24), 87, textPaint);
		}

		bufferedGraphCanvas.drawPath(pointsGraph, mainPaint);

		bufferedGraphCanvas.save();

		
		for (HistoricResult entry : quote.history)
		{
		
			bufferedGraphCanvas.drawRect(-1, 0, -1, ExtraUtils.dp2px(175),
					blueDottedPaint);
			/*
			 * th = "/" + entry.getKey().get(Calendar.MONTH);

			bufferedGraphCanvas.drawText(
					Integer.toString(entry.getKey().get(Calendar.DAY_OF_MONTH))
					+ th, ExtraUtils.dp2px(5), ExtraUtils.dp2px(15),
					bigTextPaint);
			bufferedGraphCanvas.drawText(
					Integer.toString(entry.getValue().pointsFlow) + " pts",
					ExtraUtils.dp2px(5), ExtraUtils.dp2px(30), smallTextPaint);
*/
			bufferedGraphCanvas.drawText(
					Double.toString(entry.close) + " pts",
					ExtraUtils.dp2px(5), ExtraUtils.dp2px(160), smallTextPaint);
			bufferedGraphCanvas.translate(ExtraUtils.dp2px(50), 0);

		}
		bufferedGraphCanvas.translate(-ExtraUtils.dp2px(50), 0);

		bufferedGraphCanvas.restore();

	}


	public void onDraw(Canvas canvas)
	{
		canvas.save();
		if (swipeDecay)
		{
			if (slidex < defaultSlide - 100) diffX = 15;
			if (slidex > 100) diffX = -15;
			slidex = slidex + diffX;
			canvas.translate(slidex, 0);
			diffX -= (diffX / 10);
			invalidate();
			if (Math.abs(diffX) < 1f)
			{
				swipeDecay = false;
				Log.w("HELLO", "swipeDecay OFF");
			}
		}
		else
			canvas.translate(slidex, 0);

		canvas.drawBitmap(bufferedGraph, 0, 0, defaultPaint);
		Log.w("HELLO", "slidex > " + Float.toString(slidex));
		Log.w("HELLO", "diffX > " + Float.toString(diffX));
		canvas.restore();
		canvas.drawText(quote.tick, ExtraUtils.dp2px(20), ExtraUtils.dp2px(150), pointsPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg1)
	{

		switch (arg1.getAction())
		{
		case MotionEvent.ACTION_MOVE:
			Log.w("HELLO TOUCH", "ACTION MOVE");
			diffX = ExtraUtils.px2dp((int) (arg1.getX() - lastPosX));
			slidex += diffX;
			lastPosX = arg1.getX();
			invalidate();
			touch_events++;
			break;

		case MotionEvent.ACTION_DOWN:
			Log.w("HELLO TOUCH", "ACTION DOWN");
			lastPosX = arg1.getX();
			break;

		case MotionEvent.ACTION_UP:
			Log.w("HELLO TOUCH", "ACTION UP");
			if (new Rect(0, ExtraUtils.dp2px(185),
					(int) ExtraUtils.getScreenWidth(), ExtraUtils.dp2px(245))
			.contains((int) arg1.getX(), (int) arg1.getY()))
			{

			}
			else if (!swipeDecay && (touch_events <= 3))
			{
				int selected = (int) Math
						.floor(((arg1.getX() - slidex) / ExtraUtils.dp2px(50)));
				Log.w("HELLO SELECT", Float.toString(arg1.getX())
						+ " YOU HAVE SELECTED THE " + selected);

			}

			lastPosX = -1;
			if (Math.abs(diffX) > 10)
			{
				swipeDecay = true;
				Log.w("HELLO", "swipeDecay ON");
				invalidate();
			}
			touch_events = 0;
			break;
		}
		Log.w("HELLO TOUCH", "diffx > " + Float.toString(diffX));
		if (slidex < defaultSlide) slidex = defaultSlide;
		return true;
	}

	void changeQuote(Quote q)
	{
		Log.e("HELLO GRAPH", "Changed quote to " + q.tick);
		quote = q;
		bufferedGraph = null;
		bufferedGraphCanvas = null;
		resetted = true;
		pointsGraph = null;
		buildGraph();
		invalidate();
	}
}
