package pt.feup.busticket.tickets;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;

public class BusTicketUtils {
	
	public static void createAlertDialog(Context context, String title, String content) {
		new AlertDialog.Builder(context)
	    .setTitle(title)
	    .setMessage(content)
	    .setNeutralButton("Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		})
	    .show();
	}
	
	//can be cancelled
	public static ProgressDialog createProgressDialog(Context context, String message, DialogInterface.OnCancelListener onCancelListener) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setMessage(message);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(onCancelListener);
		return dialog;
	}
	
	//can't be cancelled
	public static ProgressDialog createProgressDialog(Context context, String message) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setMessage(message);
		return dialog;
	}
	
	static DisplayMetrics metrics = constructMetrics();
    
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
	
}
