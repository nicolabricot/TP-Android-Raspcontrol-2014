package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;

public class EditActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		//getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
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
		DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
		
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		EditText name = (EditText) this.findViewById(R.id.editTextName);
		Spinner protocol = (Spinner) this.findViewById(R.id.spinnerProtocol);
		EditText hostname = (EditText) this.findViewById(R.id.editTextHostname);
		EditText username = (EditText) this.findViewById(R.id.editTextUsername);
		EditText token = (EditText) this.findViewById(R.id.editTextToken);
		values.put(DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME, name.getText().toString());
		values.put(DatabaseContract.Entry.COLUMN_NAME_PROTOCOL, protocol.getSelectedItem().toString());
		values.put(DatabaseContract.Entry.COLUMN_NAME_HOSTNAME, hostname.getText().toString());
		values.put(DatabaseContract.Entry.COLUMN_NAME_USERNAME, username.getText().toString());
		values.put(DatabaseContract.Entry.COLUMN_NAME_TOKEN, token.getText().toString());
		
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
				DatabaseContract.Entry.TABLE_NAME,
				"null",
				values);
		
		finish();
	}

}
