package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.graphics.Color;

public class ColorFactory {

	static ArrayList<Integer> availableColor =  new ArrayList<Integer>();
	
	
	ColorFactory()
	{
		refresh();
	}
	
	static int getDefault()
	{
		return Color.parseColor("#D6DF23");
	}
	
	static void refresh()
	{
		availableColor.add(Color.parseColor("#D6DF23"));
		availableColor.add(Color.parseColor("#F7931D"));
		availableColor.add(Color.parseColor("#27A9E1"));
		availableColor.add(Color.parseColor("#D91B5B"));
		availableColor.add(Color.parseColor("#652C90"));
		availableColor.add(Color.parseColor("#FBAF3F"));
		availableColor.add(Color.parseColor("#F941C5"));
		availableColor.add(Color.parseColor("#8A5D3B"));
	}
	static synchronized Integer getColor()
	{
		if (availableColor.size() == 0)
			refresh();
		Integer ret = availableColor.get(0);
		availableColor.remove(ret);
		return ret;
	}
	
}
