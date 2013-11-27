package pt.feup.stockportfolio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PieView extends View {

	
	
	public PieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void onDraw(Canvas canvas)
	{
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		canvas.drawCircle(200, 200, 100, p);
	}

}
