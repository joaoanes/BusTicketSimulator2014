package pt.feup.stockportfolio;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Utils {
	public static ArrayList<Quote> myQuotes = new ArrayList<Quote>();
	static String FILENAME = "quotes_file";
	
	public static void createAlertDialog(Context context, String title, String content) {
		new AlertDialog.Builder(context)
	    .setTitle(title)
	    .setMessage(content)
	    .setNeutralButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
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
		dialog.setCancelable(false);
		return dialog;
	}
	
	public static Quote getPortfolioQuote() {
		Quote portfolio = new Quote("Portfolio", 0);
		portfolio.name = "Portfolio";
		portfolio.history = new ArrayList<HistoricResult>(30);
		ArrayList<HistoricResult> history = portfolio.history;
		
		for(Quote quote : myQuotes) {
			for(int i = 0; i < quote.history.size(); ++i) {
				
				HistoricResult portfolio_result = history.get(i);
				HistoricResult quote_result = quote.history.get(i);
				
				if(portfolio_result == null) {
					portfolio_result = new HistoricResult();
					portfolio_result.date = quote_result.getDate();
					history.add(i, portfolio_result);
				}
				
				portfolio_result.increment(quote_result, quote.quantity);
			}
		}
		
		return portfolio;
	}
}
