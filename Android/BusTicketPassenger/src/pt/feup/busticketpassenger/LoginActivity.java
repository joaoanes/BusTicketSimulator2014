package pt.feup.busticketpassenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
	BusTicketPassenger app;
	DatePicker datepicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		app = (BusTicketPassenger) getApplication(); 

		removeDayFromDatePicker();

		populateCardTypeSpinner();
		//Intent intent = new Intent(getApplicationContext(), TicketsActivity.class);
		//Intent intent = new Intent(getApplicationContext(), ViewTicketsActivity.class);
		//Intent intent = new Intent(getApplicationContext(), BuyActivity.class);
		//startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void removeDayFromDatePicker() {
		datepicker = (DatePicker) findViewById(R.id.datepicker_register_card_validity);
		try {
			Field f[] = datepicker.getClass().getDeclaredFields();
			for (Field field : f) {

				if (field.getName().equals("mDaySpinner")
						|| field.getName().equals("mDayPicker")) {
					field.setAccessible(true);
					Object dayPicker = new Object();
					dayPicker = field.get(datepicker);
					((View) dayPicker).setVisibility(View.GONE);
				}
			}
		} catch (SecurityException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalAccessException e) {
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

		LoginTask task = new LoginTask();
		task.execute(new Void[]{});		

	}

	private class LoginTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... v) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				URL url =  new URL("http", app.server_ip, app.server_port, "/login");
				HttpPost post = new HttpPost(url.toURI());

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("username", username));
				nameValuePairs.add(new BasicNameValuePair("password", password));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					String responseString = out.toString();
					return responseString;
					//Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
				} else{
					//Closes the connection.
					response.getEntity().getContent().close();
					//Toast.makeText(getApplicationContext(), "falhou", Toast.LENGTH_SHORT).show();
					throw new IOException(statusLine.getReasonPhrase());
				}

			}catch(Exception e) {
				Log.i("thread", "falhou e lançou excepçoões");
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "falhou", Toast.LENGTH_SHORT).show();
					}
				});
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
		}

	}

}
