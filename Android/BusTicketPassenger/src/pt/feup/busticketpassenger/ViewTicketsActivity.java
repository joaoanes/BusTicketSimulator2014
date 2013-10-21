package pt.feup.busticketpassenger;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

public class ViewTicketsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_tickets);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_tickets, menu);
		return true;
	}

}
