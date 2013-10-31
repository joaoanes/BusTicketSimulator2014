package pt.feup.busticketpassenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ConfirmBuyDialogFragment extends DialogFragment{
	public final static String T1 = "T1";
	public final static String T2 = "T2";
	public final static String T3 = "T3";
	public final static String EXTRA = "EXTRA";
	public final static String PRICE = "PRICE";
	
	ConfirmBuyDialogListener listener;
	
	public interface ConfirmBuyDialogListener {
		public void confirmBuy();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
            listener = (ConfirmBuyDialogListener) activity;
        } catch (ClassCastException e) {
            
            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmPortDialogListener");
        }
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.dialog_confirm_buy, null);
		
		Bundle args = getArguments();
		
		int price = args.getInt(PRICE);
		int t1_qt = args.getInt(T1);
		int t2_qt = args.getInt(T2);
		int t3_qt = args.getInt(T3);
		String extra = args.getString(EXTRA);
		
		String price_string = "€ " + (price / 100) + "." + (price % 100); 
		
		((TextView) view.findViewById(R.id.confirm_price_val)).setText(price_string);
		
		if(t1_qt > 0) {
			((TextView) view.findViewById(R.id.confirm_t1_qt)).setText(String.valueOf(t1_qt));
			view.findViewById(R.id.confirm_t1).setVisibility(View.VISIBLE);
		}
		
		if(t2_qt > 0) {
			((TextView) view.findViewById(R.id.confirm_t2_qt)).setText(String.valueOf(t2_qt));
			view.findViewById(R.id.confirm_t2).setVisibility(View.VISIBLE);
		}
		
		if(t3_qt > 0) {
			((TextView) view.findViewById(R.id.confirm_t3_qt)).setText(String.valueOf(t3_qt));
			view.findViewById(R.id.confirm_t3).setVisibility(View.VISIBLE);
		}
		
		if(extra != null) {
			((TextView) view.findViewById(R.id.confirm_extra_val)).setText(extra);
			view.findViewById(R.id.confirm_extra).setVisibility(View.VISIBLE);
		}
			
		builder.setView(view);
		builder.setTitle("Confirm");
		
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.confirmBuy();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return builder.create();
	}
}
