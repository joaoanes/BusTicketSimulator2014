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



	public PieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	
	Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
	
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
			canvas.drawArc(new RectF(0, 0, 200, 200), start, (int) (offset), true, p);
			start = (int) (start + offset);
		}
		
		canvas.rotate((float) (Math.random()*360));
	}

}
