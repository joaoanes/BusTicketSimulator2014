package pt.feup.busticket.tickets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

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
}
