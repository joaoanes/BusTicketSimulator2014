package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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

	ArrayList<Integer> heights = new ArrayList<Integer>();
	Bitmap bufferedGraph;
	Canvas bufferedGraphCanvas;

	boolean swipeDecay = false;



	public GraphView(Context context, AttributeSet attrs, Quote _quote)
	{
		super(context, attrs);
		this.quote = _quote;
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

		float[] hsv = new float[3];
		int color = _quote.color;
		Color.colorToHSV(color, hsv);
		hsv[2] *= 2.5f; 
		color = Color.HSVToColor(hsv);

		blueDottedPaint.setColor(color);
		textPaint.setColor(Color.parseColor("#eae9f2"));
		textPaint.setTextSize(ExtraUtils.dp2px(14));
		textPaint.setTextAlign(Align.CENTER);

		smallTextPaint.setColor(Color.parseColor("#efefef"));

		smallTextPaint.setTypeface(Typeface.createFromAsset(
				context.getAssets(), "fonts/Roboto-Light.ttf"));
		smallTextPaint.setTextSize(ExtraUtils.dp2px(6));
		bigTextPaint.setColor(Color.parseColor("#587C9B"));
		bigTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Regular.ttf"));
		bigTextPaint.setTextSize(ExtraUtils.dp2px(11));

		pointsPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Regular.ttf"));
		pointsPaint.setTextSize(ExtraUtils.dp2px(60));
		pointsPaint.setColor(Color.parseColor("#efefef"));
		pointsPaint.setTextAlign(Align.LEFT);

		objectiveTextPaint.setTypeface(Typeface.createFromAsset(
				context.getAssets(), "fonts/Roboto-Light.ttf"));
		objectiveTextPaint.setTextSize(ExtraUtils.dp2px(20));
		objectiveTextPaint.setColor(Color.WHITE);

		whitePaint.setColor(Color.WHITE);




		buildGraph();

	}

	private void buildGraph() {


		mainPaint.setColor(quote.color);




		int[] width =
			{ ExtraUtils.dp2px(40) };
		if (pointsGraph == null)
			pointsGraph = PathFactory.generatePath(
					quote, heights, width);
		if (quote.tick == "Portfolio")
			width[0] -= ExtraUtils.dp2px(40);
		if (bufferedGraph == null)
		{

			bufferedGraph = Bitmap.createBitmap(width[0] < ExtraUtils
					.getScreenWidth() ? (int) ExtraUtils.getScreenWidth()
							: width[0], ExtraUtils.dp2px(175), Bitmap.Config.ARGB_8888);

		}

		if (bufferedGraph.getWidth() != width[0])
		{
			bufferedGraph.recycle();
			bufferedGraph = null;
			bufferedGraph = Bitmap.createBitmap(width[0] < ExtraUtils
					.getScreenWidth() ? (int) ExtraUtils.getScreenWidth()
							: width[0], ExtraUtils.dp2px(175), Bitmap.Config.ARGB_8888);

		}
		bufferedGraphCanvas = new Canvas(bufferedGraph);

		drawGraph();
		defaultSlide = -(bufferedGraph.getWidth() - ExtraUtils.getScreenWidth());
		slidex = defaultSlide - ExtraUtils.getScreenWidth();
		swipeDecay = true;

	}

	public void drawGraph()
	{
		// bufferedGraphCanvas.drawLine(0, KeepUtils.dp2px(50),
		// bufferedGraphCanvas.getWidth(), KeepUtils.dp2px(25),
		// blueDottedPaint);

		float[] hsv = new float[3];
		int color = quote.color;
		Color.colorToHSV(color, hsv);
		hsv[2] *= 2.5f; 
		color = Color.HSVToColor(hsv);

		blueDottedPaint.setColor(color);


		bufferedGraphCanvas.drawPath(pointsGraph, mainPaint);

		bufferedGraphCanvas.save();

		int i = 0;
		for (HistoricResult entry : quote.history)
		{
			if (i != 0)
				bufferedGraphCanvas.drawRect(0, ExtraUtils.dp2px(175) - heights.get(i-1), 0, ExtraUtils.dp2px(175),
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
			String worth = String.valueOf(entry.close);
			int dot = worth.indexOf(".");

			worth = worth.substring(0, (dot + 3) >= worth.length() ? (dot + 2) : (dot + 3));
			bufferedGraphCanvas.drawText(
					'$' + worth,
					ExtraUtils.dp2px(12), ExtraUtils.dp2px(162), smallTextPaint);
			bufferedGraphCanvas.drawText(
					entry.date.substring(entry.date.indexOf("-")+1),
					ExtraUtils.dp2px(12), ExtraUtils.dp2px(170), smallTextPaint);
			bufferedGraphCanvas.translate(ExtraUtils.dp2px(40), 0);
			++i;

		}
		if (quote.value != 0)
		{
			bufferedGraphCanvas.drawRect(0, ExtraUtils.dp2px(175) - heights.get(i-1), 0, ExtraUtils.dp2px(175),
					blueDottedPaint);
			bufferedGraphCanvas.drawText(
					'$' + Double.toString(quote.value),
					ExtraUtils.dp2px(12), ExtraUtils.dp2px(162), smallTextPaint);
			bufferedGraphCanvas.drawText("now",
					ExtraUtils.dp2px(12), ExtraUtils.dp2px(170), smallTextPaint);
		}
		bufferedGraphCanvas.restore();

	}


	public void onDraw(Canvas canvas)
	{
		canvas.save();
		Paint p = new Paint(pointsPaint);
		p.setColor(Color.parseColor("#efefef"));
		p.setStrokeWidth(4);
		p.setShadowLayer(2, 0, 0, quote.color);

		canvas.drawText(quote.tick, ExtraUtils.dp2px(20), ExtraUtils.dp2px(150), p);
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
				//Log.w("HELLO", "swipeDecay OFF");
			}
		}
		else
			canvas.translate(slidex, 0);

		canvas.drawBitmap(bufferedGraph, 0, 0, defaultPaint);
		//Log.w("HELLO", "slidex > " + Float.toString(slidex));
		//Log.w("HELLO", "diffX > " + Float.toString(diffX));
		canvas.restore();
		canvas.drawText(quote.tick, ExtraUtils.dp2px(20), ExtraUtils.dp2px(150), pointsPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg1)
	{

		switch (arg1.getAction())
		{
		case MotionEvent.ACTION_MOVE:
			//Log.w("HELLO TOUCH", "ACTION MOVE");
			diffX = ExtraUtils.px2dp((int) (arg1.getX() - lastPosX));
			slidex += diffX;
			lastPosX = arg1.getX();
			invalidate();
			touch_events++;
			break;

		case MotionEvent.ACTION_DOWN:
			//Log.w("HELLO TOUCH", "ACTION DOWN");
			lastPosX = arg1.getX();
			break;

		case MotionEvent.ACTION_UP:


			lastPosX = -1;
			if (Math.abs(diffX) > 5)
			{
				swipeDecay = true;
				//Log.w("HELLO", "swipeDecay ON");
				invalidate();
			}
			touch_events = 0;
			break;
		}
		//Log.w("HELLO TOUCH", "diffx > " + Float.toString(diffX));
		if (slidex < defaultSlide) slidex = defaultSlide;
		return true;
	}

	void changeQuote(Quote q)
	{
		//Log.e("HELLO GRAPH", "Changed quote to " + q.tick);
		quote = q;
		bufferedGraph = null;
		bufferedGraphCanvas = null;
		pointsGraph = null;
		buildGraph();
		invalidate();
	}
}
