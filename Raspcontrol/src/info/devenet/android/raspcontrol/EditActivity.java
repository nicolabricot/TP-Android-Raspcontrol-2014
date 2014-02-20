package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class EditActivity extends Activity {
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_entry);
		
		this.mDbHelper = new DatabaseHelper(getBaseContext());
		
		Intent intent = getIntent();
		long itemID = intent.getLongExtra(HomeActivity.EXTRA_ENTRY_ID, 0);
		
		if (itemID > 0) {
			setTitle(R.string.title_edit_entry);
		}
		else {
			setTitle(R.string.title_add_entry);
		}

		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		// getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_entry, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called when the user clicks the Save button */
	public void saveRaspcontrolEntry(View view) {

		// Gets the data repository in write mode
		this.db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		EditText nameView = (EditText) this.findViewById(R.id.editTextName);
		String name = nameView.getText().toString();
		if (name.isEmpty()) {
			Toast.makeText(getBaseContext(), "Name field is blank",
					Toast.LENGTH_SHORT).show();
			return;
		}

		Spinner protocolView = (Spinner) this
				.findViewById(R.id.spinnerProtocol);
		String protocol = protocolView.getSelectedItem().toString();

		EditText hostnameView = (EditText) this
				.findViewById(R.id.editTextHostname);
		String hostname = hostnameView.getText().toString();
		if (hostname.isEmpty()) {
			Toast.makeText(getBaseContext(), "Hostname field is blank",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (hostname.endsWith("/")) {
			hostname = hostname.substring(0, hostname.length() - 1);
			hostnameView.setText(hostname);
		}

		EditText usernameView = (EditText) this
				.findViewById(R.id.editTextUsername);
		String username = usernameView.getText().toString();
		if (username.isEmpty()) {
			Toast.makeText(getBaseContext(), "Username field is blank",
					Toast.LENGTH_SHORT).show();
			return;
		}

		EditText tokenView = (EditText) this.findViewById(R.id.editTextToken);
		String token = tokenView.getText().toString();
		if (token.isEmpty()) {
			Toast.makeText(getBaseContext(), "Token field is blank",
					Toast.LENGTH_SHORT).show();
			return;
		}

		values.put(DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME, name);
		values.put(DatabaseContract.Entry.COLUMN_NAME_PROTOCOL, protocol);
		values.put(DatabaseContract.Entry.COLUMN_NAME_HOSTNAME, hostname);
		values.put(DatabaseContract.Entry.COLUMN_NAME_USERNAME, username);
		values.put(DatabaseContract.Entry.COLUMN_NAME_TOKEN, token);

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(DatabaseContract.Entry.TABLE_NAME, "null", values);
		finish();
	}
	
	/** Called when the user clicks the Cancel button */
	public void cancelEditEntry(View view) {
		finish();
	}

}
