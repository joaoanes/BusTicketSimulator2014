package pt.feup.busticketpassenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		populateCardTypeSpinner();
		Intent intent = new Intent(getApplicationContext(), ViewTicketsActivity.class);
		//Intent intent = new Intent(getApplicationContext(), BuyActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void populateCardTypeSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner_register_card_type);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.card_types, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	public void onLoginClick(View view) {
		String username = ((EditText) findViewById(R.id.edittext_login_username)).getText().toString();
		String password = ((EditText) findViewById(R.id.edittext_login_password)).getText().toString();

		if(username.equals("") || password.equals("")) {
			Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.i("thread", "aqui");
		(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.i("thread", "aqui tambem");
					HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(new HttpGet("http://172.30.5.67:8080/login"));
					StatusLine statusLine = response.getStatusLine();
					if(statusLine.getStatusCode() == HttpStatus.SC_OK){
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.getEntity().writeTo(out);
						out.close();
						String responseString = out.toString();
						Log.i("thread", "consegui");
						Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
					} else{
						//Closes the connection.
						response.getEntity().getContent().close();
						Log.i("thread", "falhou");
						Toast.makeText(getApplicationContext(), "falhou", Toast.LENGTH_SHORT).show();
						throw new IOException(statusLine.getReasonPhrase());
					}

				}catch(Exception e) {
					Log.i("thread", "falhou e lan�ou excep�o�es");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "falhou", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		})).start();

	}

}