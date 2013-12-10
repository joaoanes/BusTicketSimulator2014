package pt.feup.stockportfolio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class PieView extends View {


	public PieView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

		// TODO Auto-generated constructor stub
		this.setBackgroundColor(Color.parseColor("#efefef"));
	}

	public PieView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}

	Quote activatedQuote = null;
	Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
	boolean quantityMode = false;
	float rand = 0;
	public void setActivated(Quote q)
	{
		activatedQuote = q;
		invalidate();
	}
	
	
	
	public void onDraw(Canvas canvas)
	{

		p.setColor(Color.BLACK);

		int allStocks = 0;

		for (Quote q : Utils.myQuotes)
		{
			if (quantityMode)
				allStocks += q.quantity;
			else
				allStocks += q.quantity*q.value;
			
		}
		int start = 0;
		double offset = 0;
		canvas.save();
		if (rand == 0)
			rand = (float) Math.random()*360;
		canvas.rotate(rand, (float) ExtraUtils.dp2px(85), (float) ExtraUtils.dp2px(85));
		
		for (Quote q : Utils.myQuotes)
		{
			if (quantityMode)
				offset = Math.round(((double) q.quantity/ (double)allStocks)*360);
			else
				offset = Math.round(((((double) q.quantity)*((double) q.value)) /(double)allStocks)*360);
			//Log.i("HELLO PIE", "Processing " + q.tick + " starting at " + start + "with offset " + (int) (offset));
			if (offset < 2)
				continue;
				
			if (activatedQuote == null)
				activatedQuote = q;
			
			p.setColor(q.color);
			if (q == activatedQuote)
				canvas.drawArc(new RectF(ExtraUtils.dp2px(10), ExtraUtils.dp2px(10), ExtraUtils.dp2px(160), ExtraUtils.dp2px(160)), start, (int) (offset), true, p);
			
			else
			{
			
				canvas.drawArc(new RectF(ExtraUtils.dp2px(20), ExtraUtils.dp2px(20), ExtraUtils.dp2px(150), ExtraUtils.dp2px(150)), start, (int) (offset), true, p);
			}
			start = (int) (start + offset);
		}
		canvas.restore();
		
		Paint smalltext = new Paint(Paint.ANTI_ALIAS_FLAG);
		smalltext.setTypeface(Typeface.createFromAsset(
				getContext().getAssets(), "fonts/Roboto-Light.ttf"));
		smalltext.setColor(Color.parseColor("#efefef"));
		smalltext.setTextSize(ExtraUtils.dp2px(15));
		smalltext.setTextAlign(Align.CENTER);
		canvas.drawText(quantityMode ? "ownership" : "worth", ExtraUtils.dp2px(85), ExtraUtils.dp2px(125), smalltext);
		smalltext.setColor(Color.parseColor("#afafaf"));
		smalltext.setTextSize(ExtraUtils.dp2px(8));
		canvas.drawText("tap to switch", ExtraUtils.dp2px(85), ExtraUtils.dp2px(132), smalltext);
		
	}

}
