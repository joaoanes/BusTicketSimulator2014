package pt.feup.busticketpassenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class TicketQuantityDialogFragment extends DialogFragment {
	int button_id=-1;
	NumberPicker np;
	TicketQuantityDialogListener listener;
	
	public interface TicketQuantityDialogListener {
		public void onPositiveClick(DialogFragment dialog, int quantity, int button_id);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		button_id = getArguments().getInt(BuyActivity.QUANTITY_ID);
		
		String ticket_type = "";
		
		switch (button_id) {
			case R.id.button_change_t1:
				ticket_type = getString(R.string.label_t1);
				break;
			case R.id.button_change_t2:
				ticket_type = getString(R.string.label_t2);
				break;
			case R.id.button_change_t3:
				ticket_type = getString(R.string.label_t3);
				break;
			default:
				break;
		}
		
		builder.setTitle("Input Quantity for " + ticket_type);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.dialog_ticket_quantity, null);
		np = (NumberPicker) view.findViewById(R.id.ticket_quantiy_id);
		np.setMinValue(0);
		np.setMaxValue(10);
		np.setWrapSelectorWheel(false);
		
		builder.setView(view);
		
		builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int quantity = np.getValue();
				
				listener.onPositiveClick(TicketQuantityDialogFragment.this, quantity, button_id);
				
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
            
            listener = (TicketQuantityDialogListener) activity;
        } catch (ClassCastException e) {
            
            throw new ClassCastException(activity.toString()
                    + " must implement TicketQuantityDialogListener");
        }
	}
	
	
	
	

}
