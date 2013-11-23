package pt.feup.busticketpassenger;

import java.lang.reflect.Field;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import pt.feup.busticket.tickets.BusTicketUtils;
import pt.feup.busticket.tickets.HttpHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
	TextView app_name;
	TextView login_header;
	TextView register_header;
	Spinner register_card_type;
	DatePicker register_card_validity;
	
	LinearLayout login_wrapper;
	LinearLayout register_wrapper;
	
	ProgressDialog progress_dialog;
	protected boolean signInFormExpanded = true;
	protected boolean registerFormExpanded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		BusTicketUtils.setMetrics(getResources().getDisplayMetrics());
		app = (BusTicketPassenger) getApplication(); 

		instantiateForms();
		populateCardTypeSpinner();
		initializeOnClicks();
	}

	private void initializeOnClicks() {
		login_header.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0)
			{
			Animation anim = new DropDownAnim(login_wrapper, BusTicketUtils.dp2px(100), !signInFormExpanded );
				Animation anim2 = new DropDownAnim(register_wrapper, BusTicketUtils.dp2px(320), !registerFormExpanded );

				anim.setDuration(1300);
				anim2.setDuration(1300);
				
				if (signInFormExpanded && (!login_username.getText().toString().equals("")))
				{
					onLoginClick(null);
					return ;
				}
				LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) login_wrapper.getLayoutParams();
				p.height = !signInFormExpanded ? 1 : BusTicketUtils.dp2px(99);
				login_wrapper.setLayoutParams(p);
				login_wrapper.startAnimation(anim);

				signInFormExpanded = !signInFormExpanded;
				if (registerFormExpanded)
				{
					register_wrapper.startAnimation(anim2);
					registerFormExpanded = false;
				}
			}

		});

		register_header.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View arg0)
			{
				Animation anim = new DropDownAnim(login_wrapper, BusTicketUtils.dp2px(100), !signInFormExpanded);
				Animation anim2 = new DropDownAnim(register_wrapper, BusTicketUtils.dp2px(320), !registerFormExpanded);
				
				anim.setDuration(1300);
				anim2.setDuration(1300);
				
				if (registerFormExpanded && (!register_username.getText().toString().equals("")))
				{
					onRegisterClick(null);
					return ;
				}
				LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) register_wrapper.getLayoutParams();
				p.height = !registerFormExpanded ? 1 : BusTicketUtils.dp2px(319);
				register_wrapper.setLayoutParams(p);
				if (signInFormExpanded)
				{
					login_wrapper.startAnimation(anim);
					signInFormExpanded = false;
				}
				register_wrapper.startAnimation(anim2);

				Animation anim3 = AnimationUtils.loadAnimation(getBaseContext(), registerFormExpanded? R.anim.fade_in : R.anim.fade_out);
				anim3.setDuration(1300);
				anim3.setRepeatCount(0);
				anim3.setFillAfter(true);
				app_name.startAnimation(anim3);
				
					
				
				registerFormExpanded = !registerFormExpanded;
				
			}
		
		});
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
		login_wrapper = (LinearLayout) findViewById(R.id.login_wrapper);
		register_wrapper = (LinearLayout) findViewById(R.id.register_wrapper);
		login_header = (TextView) findViewById(R.id.login_header);
		register_header = (TextView) findViewById(R.id.register_header);
		app_name = (TextView) findViewById(R.id.app_name);
		instantiateDatePicker();
	}
	
	private void instantiateDatePicker() {
		register_card_validity = (DatePicker) findViewById(R.id.datepicker_register_card_validity);

		
		try {
			Time now = new Time();
			now.setToNow();
			long millis = now.toMillis(false);
			
			Time then = new Time();
			then.set(31, 12, 2020);
			
			register_card_validity.setMaxDate(then.toMillis(false));
			
			register_card_validity.setMinDate(millis);
			
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

		progress_dialog = BusTicketUtils.createProgressDialog(LoginActivity.this, "Signing In");
		progress_dialog.show();
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
		
		progress_dialog = BusTicketUtils.createProgressDialog(LoginActivity.this, "Registering");
		progress_dialog.show();
		RegisterTask task = new RegisterTask();
		task.execute(new Void[]{});	
	}

	public void login(String json_string) throws JSONException {
		JSONObject json = new JSONObject(json_string);
		if(app.token != null) {
			app.reset();
		}
		app.token = json.getString("auth");
		Intent intent = new Intent(getApplicationContext(), TicketsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public void showError(String context, String json_string) {
		JSONObject json;
		try {
			json = new JSONObject(json_string);
			String error = json.getString("error");
			BusTicketUtils.createAlertDialog(LoginActivity.this, context, error);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private class LoginTask extends AsyncTask<Void, Void, HttpHelper.HttpResult> {

		@Override
		protected HttpHelper.HttpResult doInBackground(Void... v) {
			HttpHelper helper = new HttpHelper();
			return helper.login(username, password);
		}

		@Override
		protected void onPostExecute(HttpHelper.HttpResult result) {
			progress_dialog.dismiss();
			
			switch(result.getCode()) {
				case HttpStatus.SC_OK:
					try {
						login(result.getResult());
					} catch (JSONException e) {
						BusTicketUtils.createAlertDialog(LoginActivity.this, "Login", "Didn't get token");
					}
					return;
				default:
					showError("Login", result.getResult());
					//BusTicketUtils.createAlertDialog(LoginActivity.this, "Login", result.toString());
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
			progress_dialog.dismiss();
			if(result.getCode() == HttpStatus.SC_OK) {
				try {
					login(result.getResult());
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			//BusTicketUtils.createAlertDialog(LoginActivity.this, "Register", result.getResult());
			showError("Register", result.getResult());
		}

	}
	
	public class DropDownAnim extends Animation {
		int targetHeight;
		View view;
		boolean down;

		public DropDownAnim(View view, int targetHeight, boolean down) {
			this.view = view;
			this.targetHeight = targetHeight;
			this.down = down;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			int newHeight;
			if (down) {
				newHeight = (int) (targetHeight * interpolatedTime);
			} else {
				newHeight = (int) (targetHeight * (1 - interpolatedTime));
			}
			view.getLayoutParams().height = newHeight;
			view.requestLayout();
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}

}
