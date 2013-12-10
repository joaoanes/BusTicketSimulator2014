package pt.feup.stockportfolio;

import android.util.DisplayMetrics;

public class ExtraUtils
{

	static DisplayMetrics metrics = constructMetrics();

	public ExtraUtils()
	{
		metrics.density = 1;
	}
	//Converts a DP unit to a PX unit, taking care of conversion automatically
	//BTW, we are assuming the max screen width as 320dp, height is occupied as we can
	public static int dp2px(int dp)
	{
		return (int) Math.round(dp * metrics.density);
	}

	//Converts a pixel unit to an adequate DP unit, taking care of conversion automatically
	public static int px2dp(int px)
	{
		return (int) Math.round(px / metrics.density);
	}
	//Don't you live it when function names basically explain themselves?
	public static float getScreenWidth()
	{
		return metrics.widthPixels;
	}

	//Sets the display metrics object, set this at runtime (or everything goes to hell)
	public static void setMetrics(DisplayMetrics displayMetrics)
	{
		metrics = displayMetrics;
	}

	public static DisplayMetrics constructMetrics()
	{
		DisplayMetrics h = new DisplayMetrics();
		h.density = 1.5f;
		h.widthPixels = 480;

		return h;
	}

	//converts m/s to km/h
	public static double convertToKmH(double averageVelocity)
	{
		return averageVelocity*3.6;
	}
}
