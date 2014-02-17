package info.devenet.android.raspcontrol;

import java.util.ArrayList;

import database.RaspDataBaseHelper;
import database.RaspcontrolContract;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	public final static String EXTRA_MESSAGE = "info.devenet.android.raspcontrol.MESSAGE";

	private RaspDataBaseHelper mDbHelper;
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new RaspDataBaseHelper(getBaseContext());
		db = mDbHelper.getReadableDatabase();
		
		setContentView(R.layout.activity_home);
	}

	protected void refreshList() {
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { RaspcontrolContract.RaspEntry._ID,
				RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME };

		// How you want the results sorted in the resulting Cursor
		String sortOrder = RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME
				+ " ASC";

		Cursor c = db.query(RaspcontrolContract.RaspEntry.TABLE_NAME, // The
																		// table
																		// to
																		// query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		if (c.getCount() > 0) {

			c.moveToFirst();
			long itemId = c.getLong(c
					.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry._ID));
			String itemName = c
					.getString(c
							.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME));
			Log.d("DEBUG", Long.toString(itemId) + ": " + itemName);
			while (c.moveToNext()) {
				itemId = c
						.getLong(c
								.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry._ID));
				itemName = c
						.getString(c
								.getColumnIndexOrThrow(RaspcontrolContract.RaspEntry.COLUMN_NAME_ENTRY_NAME));
				Log.d("DEBUG", Long.toString(itemId) + ": " + itemName);
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		// Do something in response to button
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) this.findViewById(R.id.edit_message);
		intent.putExtra(EXTRA_MESSAGE, editText.getText().toString());
		this.startActivity(intent);
	}

	/** Called when the user clicks the Add button */
	public void addHost(View view) {
		Intent intent = new Intent(this, EditActivity.class);
		this.startActivity(intent);
	}

	/** Called when the user clicks the Quit button */
	public void quitApplication(View view) {
		finish();
	}

}
