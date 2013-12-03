package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphViewSmall extends View
{

	Paint bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

	Quote quote = null;
	
	ArrayList<Integer> levelUps = new ArrayList<Integer>();
	Bitmap bufferedGraph;
	Canvas bufferedGraphCanvas;

	boolean swipeDecay = false;
	private boolean readyDraw = false;

	public GraphViewSmall(Context context, AttributeSet attrs, Quote quo)
	{
		super(context, attrs);
		quote = quo;
		Options opts = new Options();
		opts.inDither = false; 
		opts.inPurgeable = true; 
		opts.inInputShareable = true; 
		opts.inTempStorage = new byte[32 * 1024];

		ExtraUtils.setMetrics(getResources().getDisplayMetrics());
		ctxt = context;

		this.setBackgroundColor(Color.parseColor("#efefef"));
		bluerPaint.setColor(Color.parseColor("#587C9B"));
		bluePaint.setColor(Color.parseColor("#87B3D7"));
		blueDottedPaint.setStyle(Style.STROKE);
		blueDottedPaint.setColor(Color.parseColor("#87B3D7"));
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

		
		drawGraph();
	}

	public void drawGraph()
	{
		
		int[] width =
			{ 0 };
		if (pointsGraph == null)
			pointsGraph = PathFactory.generateSmallerPath(
					quote, width);
		if (bufferedGraph == null)
		{

			bufferedGraph = Bitmap.createBitmap(ExtraUtils.dp2px(130), ExtraUtils.dp2px(45), Bitmap.Config.ARGB_8888);
		
		}

	
		bufferedGraphCanvas = new Canvas(bufferedGraph);
		
		// bufferedGraphCanvas.drawLine(0, KeepUtils.dp2px(50),
		// bufferedGraphCanvas.getWidth(), KeepUtils.dp2px(25),
		// blueDottedPaint);
		for (int i = 1; i < levelUps.size() + 1; ++i)
		{
			bufferedGraphCanvas.drawCircle(
					levelUps.get(i - 1) + ExtraUtils.dp2px(25), 80, 15,
					bluePaint);
			bufferedGraphCanvas.drawText(Integer.toString(i),
					levelUps.get(i - 1) + ExtraUtils.dp2px(24), 87, textPaint);
		}
		bluePaint.setColor(quote.color);
		bufferedGraphCanvas.drawPath(pointsGraph, bluePaint);

		//Log.e("HELLO GRAPH", "created new graph for " + quote.tick);
		readyDraw  = true;
	}


	public void onDraw(Canvas canvas)
	{
		if (!readyDraw)
			return;
		canvas.drawBitmap(bufferedGraph, 0, 0, defaultPaint);
		canvas.drawText(quote.tick, ExtraUtils.dp2px(0), ExtraUtils.dp2px(0), bluePaint);
	}
}
