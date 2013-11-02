package pt.feup.busticketpassenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ChangeIPAndPortDialogFragment extends DialogFragment {
	public enum Device {SERVER, BUS, INSPECTOR};
	public final static String DEVICE_ID = "DEVICE_ID";
	
	ChangeIPAndPortDialogListener listener;
	EditText ip_field;
	EditText port_field;
	BusTicketPassenger app;
	Device device;
	
	
	public interface ChangeIPAndPortDialogListener {
		public void onBusPositiveClick();
		public void onInspectorPositiveClick();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		app = (BusTicketPassenger) getActivity().getApplication();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.dialog_change_ip_port, null);
		
		ip_field = (EditText) view.findViewById(R.id.edittext_ip);
		port_field = (EditText) view.findViewById(R.id.edittext_port);
		
		String temp = getArguments().getString(DEVICE_ID);
		String ip = null;
		String port = null;
		
		if(temp.equals(Device.SERVER.toString())) {
			device = Device.SERVER;
			temp = "Input Server IP and Port";
			ip = app.server_ip;
			port = String.valueOf(app.server_port);
			
		}
		else if(temp.equals(Device.BUS.toString())) {
			device = Device.BUS;
			temp = "Input Bus IP and Port";
			ip = app.bus_ip;
			port = String.valueOf(app.bus_port);
		}
		else if(temp.equals(Device.INSPECTOR.toString())) {
			device = Device.INSPECTOR;
			temp = "Input Inspector IP and Port";
			ip = app.server_ip;
			port = String.valueOf(app.inspector_port);
		}
		else {
			//TODO
			temp = "Error";
		}
		
		builder.setTitle(temp);
		ip_field.setText(ip);
		port_field.setText(port);
				
		builder.setView(view);
		
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(device == Device.BUS) {
					app.bus_ip = ip_field.getText().toString();
					app.bus_port = Integer.parseInt(port_field.getText().toString());
					
					listener.onBusPositiveClick();
				}
				else if(device == Device.SERVER) {
					app.server_ip = ip_field.getText().toString();
					app.server_port = Integer.parseInt(port_field.getText().toString());
				}
				else if(device == Device.INSPECTOR) {
					app.inspector_ip = ip_field.getText().toString();
					app.inspector_port = Integer.parseInt(port_field.getText().toString());
					
					listener.onInspectorPositiveClick();
				}
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
            listener = (ChangeIPAndPortDialogListener) activity;
        } catch (ClassCastException e) {
            
            throw new ClassCastException(activity.toString()
                    + " must implement ChangeIPAndPortDialogListener");
        }
	}
}
