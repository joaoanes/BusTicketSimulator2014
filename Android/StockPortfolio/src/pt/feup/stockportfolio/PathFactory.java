package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.graphics.Path;
import android.util.Log;

public class PathFactory
{



	/**
	 * 
	 * @param money
	 * @param xLevelUp Empty arraylist, is populated level up events
	 * @return Correct path!
	 */
	public static Path generatePath(Quote money, ArrayList<Integer> heights, int[] width)
	{
		Path returnee = new Path();
		heights.clear();
		returnee.moveTo(0, ExtraUtils.dp2px(175));

		ArrayList<HistoricResult> copyQuotesHistory = new ArrayList<HistoricResult>(money.history);

		double high = 0;
		double low = Integer.MAX_VALUE;


		for (HistoricResult h : copyQuotesHistory)
		{
			if (h.close > high)
				high = h.close;
			if (h.close < low)
				low = h.close;
		}

		Log.e("HELLO GRAPH", "Creating path for " + money.tick);
		double delta = high-low;
		double process = 0;
		int height = 0;
		for (HistoricResult h : money.history)
		{
			/*
		canvas.drawText(Integer.toString(h.getDate().getDay()), x, KeepUtils.dp2px(20), textPaint);
		canvas.drawText(Integer.toString(h.pointsFlow), x, KeepUtils.dp2px(40), textPaint);
			 */
			process = (h.close - low);
			height = ExtraUtils.dp2px((int) Math.floor(( process / delta) * 175.0));
			returnee.lineTo(width[0], ExtraUtils.dp2px(175) -  height);
			heights.add(height);
			width[0] += ExtraUtils.dp2px(40);
		}
		process = (money.value - low);
		
		height = ExtraUtils.dp2px((int) Math.floor(( process / delta) * 175.0));
		returnee.lineTo(width[0], ExtraUtils.dp2px(175) - height);
		heights.add(height);
		
		returnee.lineTo(width[0], ExtraUtils.dp2px(175));

		return returnee;
	}

	public static Path generateSmallerPath(Quote money, int[] width)
	{
		Path returnee = new Path();
		if (!money.isUpdated)
			try {
				money.update();
			} catch (NoInternetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		returnee.moveTo(0, ExtraUtils.dp2px(45));

		ArrayList<HistoricResult> copyQuotesHistory = new ArrayList<HistoricResult>(money.history);

		double high = 0;
		double low = Integer.MAX_VALUE;


		for (HistoricResult h : copyQuotesHistory)
		{
			if (h.close > high)
				high = h.close;
			if (h.close < low)
				low = h.close;
		}

		Log.e("HELLO GRAPH", "Creating small path for " + money.tick);

		double delta = high-low;
		HistoricResult h;
		
		for (int i = copyQuotesHistory.size()-8; i < copyQuotesHistory.size(); ++i)
		{
			h = money.history.get(i);
			/*
		canvas.drawText(Integer.toString(h.getDate().getDay()), x, KeepUtils.dp2px(20), textPaint);
		canvas.drawText(Integer.toString(h.pointsFlow), x, KeepUtils.dp2px(40), textPaint);
			 */
			double process = (h.close - low);


			returnee.lineTo(width[0], ExtraUtils.dp2px(45) - ExtraUtils.dp2px((int) Math.floor(( process / delta) * 45.0)));
			width[0] += ExtraUtils.dp2px(20);
		}
		returnee.lineTo(width[0], ExtraUtils.dp2px(175) - ExtraUtils.dp2px((int) Math.floor(((money.value - low) / delta) * 175.0)));
		width[0] -= ExtraUtils.dp2px(20);
		returnee.lineTo(width[0], ExtraUtils.dp2px(45));
		Log.e("HELLO GRAPH", "This one ended at " + money.history.get(money.history.size()-1).close);
		return returnee;
	}


}
