package pt.feup.stockportfolio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
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
			allStocks += q.quantity;
		int start = 0;
		double offset = 0;
		for (Quote q : Utils.myQuotes)
		{
			offset = Math.round(((double) q.quantity/ (double)allStocks)*360);

			Log.i("HELLO PIE", "Processing " + q.tick + " starting at " + start + "with offset " + (int) (offset));
			if (offset < 2)
				continue;
				
			p.setColor(q.color);
			if (q == activatedQuote)
				canvas.drawArc(new RectF(ExtraUtils.dp2px(10), ExtraUtils.dp2px(10), ExtraUtils.dp2px(160), ExtraUtils.dp2px(160)), start, (int) (offset), true, p);
			
			else
				canvas.drawArc(new RectF(ExtraUtils.dp2px(20), ExtraUtils.dp2px(20), ExtraUtils.dp2px(150), ExtraUtils.dp2px(150)), start, (int) (offset), true, p);
			start = (int) (start + offset);
		}
		
	}

}
