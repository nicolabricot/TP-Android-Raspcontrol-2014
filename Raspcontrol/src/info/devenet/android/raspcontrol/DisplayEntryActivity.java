package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.RaspDataBaseHelper;
import info.devenet.android.raspcontrol.database.RaspcontrolContract;
import info.devenet.android.raspcontrol.helper.HttpGetter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class DisplayEntryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_entry);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		long itemID = intent.getLongExtra(HomeActivity.EXTRA_ENTRY_ID, 0);
		
		if (itemID > 0) {
			
			RaspDataBaseHelper mDbHelper = new RaspDataBaseHelper(getBaseContext());
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			
			// Define 'where' part of query.
			String selection = RaspcontrolContract.RaspEntry._ID + " LIKE ?";
			// Specify arguments in placeholder order.
			String[] selectionArgs = { String.valueOf(itemID) };
			
			// Define a projection that specifies which columns from the database
			// you will actually use after this query.
			String[] projection = { RaspcontrolContract.RaspEntry._ID,
					RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME,
					RaspcontrolContract.RaspEntry.COLUMN_NAME_HOSTNAME };

			// How you want the results sorted in the resulting Cursor
			String sortOrder = RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME
					+ " ASC";

			Cursor c = db.query(RaspcontrolContract.RaspEntry.TABLE_NAME, // The
																			// table
																			// to
																			// query
					projection, // The columns to return
					selection, // The columns for the WHERE clause
					selectionArgs, // The values for the WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					sortOrder // The sort order
					);			
			
			c.moveToFirst();
			
			long itemId = c
					.getLong(c
							.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry._ID));
			String itemName = c
					.getString(c
							.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME));
			String itemHostname = c
					.getString(c
							.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry.COLUMN_NAME_HOSTNAME));
			
			Log.d("DEBUG", itemID + ": " + itemName + " - " + itemHostname);
			
			HttpGetter get = new HttpGetter();
			Log.d("DEBUG", get.execute("http://ensisa.devenet.info/server/api.php?username=fake&token=d985dfa98e44dda4cbd979affce1cf53&data=all").toString());
			
		}
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_entry, menu);
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

}
