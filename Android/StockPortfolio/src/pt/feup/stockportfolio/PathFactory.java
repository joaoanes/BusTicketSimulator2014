package pt.feup.stockportfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import pt.feup.stockportfolio.HttpHelper.HistoricResult;
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
	public static Path generatePath(Quote money, ArrayList<Integer> xLevelUp, int[] width)
	{
		Path returnee = new Path();

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
		for (HistoricResult h : money.history)
		{
			/*
		canvas.drawText(Integer.toString(h.getDate().getDay()), x, KeepUtils.dp2px(20), textPaint);
		canvas.drawText(Integer.toString(h.pointsFlow), x, KeepUtils.dp2px(40), textPaint);
			 */
			double process = (h.close - low);


			returnee.lineTo(width[0], ExtraUtils.dp2px(175) - ExtraUtils.dp2px((int) Math.floor(( process / delta) * 175.0)));
			width[0] += ExtraUtils.dp2px(50);
		}
		width[0] -= ExtraUtils.dp2px(50);
		returnee.lineTo(width[0], ExtraUtils.dp2px(175));

		return returnee;
	}

	public static Path generateSmallerPath(Quote money, int[] width)
	{
		Path returnee = new Path();
		if (!money.isUpdated)
			money.update();

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
		width[0] -= ExtraUtils.dp2px(20);
		returnee.lineTo(width[0], ExtraUtils.dp2px(45));
		Log.e("HELLO GRAPH", "This one ended at " + money.history.get(money.history.size()-1).close);
		return returnee;
	}


}
