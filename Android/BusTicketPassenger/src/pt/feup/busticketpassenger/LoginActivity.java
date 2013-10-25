package pt.feup.busticketpassenger;

import java.lang.reflect.Field;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.HttpHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends Activity {
	String username;
	String password;
	String name;
	String card_type;
	String card_validity;
	String card_number;
	
	BusTicketPassenger app;
	
	EditText login_username;
	EditText login_password;
	EditText register_username;
	EditText register_password;
	EditText register_name;
	EditText register_card_number;
	Spinner register_card_type;
	DatePicker register_card_validity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		app = (BusTicketPassenger) getApplication(); 

		instantiateForms();
		populateCardTypeSpinner();
		
		//Intent intent = new Intent(getApplicationContext(), TicketsActivity.class);
		//Intent intent = new Intent(getApplicationContext(), BuyActivity.class);
		//startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void instantiateForms() {
		login_username = (EditText) findViewById(R.id.edittext_login_username);
		login_password = (EditText) findViewById(R.id.edittext_login_password);
		
		register_username = (EditText) findViewById(R.id.edittext_register_username);
		register_password = (EditText) findViewById(R.id.edittext_register_password);
		register_name = (EditText) findViewById(R.id.edittext_register_name);
		register_card_number = (EditText) findViewById(R.id.edittext_register_card_number);
		register_card_type = (Spinner) findViewById(R.id.spinner_register_card_type);
		
		instantiateDatePicker();
	}
	
	private void instantiateDatePicker() {
		register_card_validity = (DatePicker) findViewById(R.id.datepicker_register_card_validity);
		try {
			Field f[] = register_card_validity.getClass().getDeclaredFields();
			for (Field field : f) {

				if (field.getName().equals("mDaySpinner")
						|| field.getName().equals("mDayPicker")) {
					field.setAccessible(true);
					Object dayPicker = new Object();
					dayPicker = field.get(register_card_validity);
					((View) dayPicker).setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			Log.d("ERROR", e.getMessage());
		}
		
		Time now = new Time();
		now.setToNow();
		register_card_validity.setMinDate(now.toMillis(false));
	}

	private void populateCardTypeSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner_register_card_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.card_types, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public void onLoginClick(View view) {
		username = ((EditText) findViewById(R.id.edittext_login_username)).getText().toString();
		password = ((EditText) findViewById(R.id.edittext_login_password)).getText().toString();

		if(username.equals("") || password.equals("")) {
			Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
			return;
		}

		LoginTask task = new LoginTask();
		task.execute(new Void[]{});		
	}
	
	public void onRegisterClick(View view) {
		username = ((EditText) findViewById(R.id.edittext_register_username)).getText().toString();
		password = ((EditText) findViewById(R.id.edittext_register_password)).getText().toString();
		name = ((EditText) findViewById(R.id.edittext_register_name)).getText().toString();
		card_number = ((EditText) findViewById(R.id.edittext_register_card_number)).getText().toString();
		card_type = ((Spinner) findViewById(R.id.spinner_register_card_type)).getSelectedItem().toString();
		card_validity = (register_card_validity.getMonth() + 1)+ "/" + register_card_validity.getYear();
		
		if(username.equals("") || password.equals("") || name.equals("") || card_number.equals("")) {
			Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
			return;
		}

		RegisterTask task = new RegisterTask();
		task.execute(new Void[]{});	
	}

	private class LoginTask extends AsyncTask<Void, Void, HttpHelper.HttpResult> {

		@Override
		protected HttpHelper.HttpResult doInBackground(Void... v) {
			HttpHelper helper = new HttpHelper();
			return helper.login(username, password);
		}

		@Override
		protected void onPostExecute(HttpHelper.HttpResult result) {
			switch(result.getCode()) {
				case HttpStatus.SC_OK:
					try {
						JSONObject json = new JSONObject(result.getResult());
						if(app.token != null) {
							app.reset();
						}
						app.token = json.getString("auth");
						Intent intent = new Intent(getApplicationContext(), TicketsActivity.class);
						startActivity(intent);
					} catch (JSONException e) {
						BusTicketUtils.createAlertDialog(LoginActivity.this, "Login", "Didn't get token");
					}
					return;
				default:
					BusTicketUtils.createAlertDialog(LoginActivity.this, "Login", result.toString());
					return;
			}
		}
	}
		
	private class RegisterTask extends AsyncTask<Void, Void, HttpHelper.HttpResult> {

		@Override
		protected HttpHelper.HttpResult doInBackground(Void... v) {
			HttpHelper helper = new HttpHelper();
			return helper.register(username, password, name, card_type, card_validity, card_number);
		}

		@Override
		protected void onPostExecute(HttpHelper.HttpResult result) {
			BusTicketUtils.createAlertDialog(LoginActivity.this, "Register", result.toString());
		}

	}

}
