package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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

	private long itemID;

	private EditText nameView;
	private Spinner protocolView;
	private EditText hostnameView;
	private EditText usernameView;
	private EditText tokenView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_entry);

		this.nameView = (EditText) this.findViewById(R.id.editTextName);
		this.protocolView = (Spinner) this.findViewById(R.id.spinnerProtocol);
		this.hostnameView = (EditText) this.findViewById(R.id.editTextHostname);
		this.usernameView = (EditText) this.findViewById(R.id.editTextUsername);
		this.tokenView = (EditText) this.findViewById(R.id.editTextToken);

		this.mDbHelper = new DatabaseHelper(getBaseContext());

		Intent intent = getIntent();
		this.itemID = intent.getLongExtra(HomeActivity.EXTRA_ENTRY_ID, 0);

		if (this.itemID > 0) {
			setTitle(R.string.title_edit_entry);

			this.db = mDbHelper.getReadableDatabase();

			// Define 'where' part of query.
			String selection = DatabaseContract.Entry._ID + " LIKE ?";
			// Specify arguments in placeholder order.
			String[] selectionArgs = { String.valueOf(itemID) };

			// Define a projection that specifies which columns from the
			// database
			// you will actually use after this query.
			String[] projection = { DatabaseContract.Entry._ID,
					DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME,
					DatabaseContract.Entry.COLUMN_NAME_HOSTNAME,
					DatabaseContract.Entry.COLUMN_NAME_PROTOCOL,
					DatabaseContract.Entry.COLUMN_NAME_USERNAME,
					DatabaseContract.Entry.COLUMN_NAME_TOKEN };

			// How you want the results sorted in the resulting Cursor
			String sortOrder = DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME
					+ " ASC";

			Cursor c = db.query(DatabaseContract.Entry.TABLE_NAME, projection, // The
																				// columns
																				// to
																				// return
					selection, // The columns for the WHERE clause
					selectionArgs, // The values for the WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					sortOrder // The sort order
					);

			c.moveToFirst();

			String itemName = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME));
			String itemHostname = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_HOSTNAME));
			String itemProtocol = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_PROTOCOL));
			String itemUsername = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_USERNAME));
			String itemToken = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_TOKEN));

			c.close();
			this.db.close();

			this.nameView.setText(itemName);
			this.hostnameView.setText(itemHostname);
			this.protocolView.setSelection(itemProtocol.equals("http") ? 0 : 1);
			this.usernameView.setText(itemUsername);
			this.tokenView.setText(itemToken);

		} else {
			setTitle(R.string.title_add_entry);
		}

		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@SuppressLint("NewApi")
	private void setupActionBar() {
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
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
		String name = this.nameView.getText().toString();
		if (name.isEmpty()) {
			Toast.makeText(getBaseContext(), "Name field is required",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String protocol = this.protocolView.getSelectedItem().toString();

		String hostname = this.hostnameView.getText().toString();
		if (hostname.isEmpty()) {
			Toast.makeText(getBaseContext(), "Hostname field is required",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (hostname.endsWith("/")) {
			hostname = hostname.substring(0, hostname.length() - 1);
			this.hostnameView.setText(hostname);
		}

		String username = this.usernameView.getText().toString();
		if (username.isEmpty()) {
			Toast.makeText(getBaseContext(), "Username field is required",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String token = this.tokenView.getText().toString();
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

		if (this.itemID == 0) {
			// Insert the new row, returning the primary key value of the new
			// row
			long newRowId;
			newRowId = db.insert(DatabaseContract.Entry.TABLE_NAME, "null",
					values);
		} else {
			// Which row to update, based on the ID
			String selection = DatabaseContract.Entry._ID + " LIKE ?";
			String[] selectionArgs = { String.valueOf(this.itemID) };

			db.update(DatabaseContract.Entry.TABLE_NAME,
					values, selection, selectionArgs);
		}
		finish();

	}

	/** Called when the user clicks the Cancel button */
	public void cancelEditEntry(View view) {
		finish();
	}

}
