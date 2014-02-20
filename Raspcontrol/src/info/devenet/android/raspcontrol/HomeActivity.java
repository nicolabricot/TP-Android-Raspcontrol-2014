package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {

	public final static String EXTRA_MESSAGE = "info.devenet.android.raspcontrol.MESSAGE";
	public final static String EXTRA_ENTRY_ID = "info.devenet.android.raspcontrol.ENTRY_ID";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase db;
	private LinearLayout list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new DatabaseHelper(getBaseContext());
		db = mDbHelper.getReadableDatabase();

		setContentView(R.layout.activity_home);

		list = (LinearLayout) findViewById(R.id.linearLayoutRasp);

	}

	protected void refreshList() {

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { DatabaseContract.Entry._ID,
				DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME,
				DatabaseContract.Entry.COLUMN_NAME_HOSTNAME };

		// How you want the results sorted in the resulting Cursor
		String sortOrder = DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME
				+ " ASC";

		Cursor c = db.query(DatabaseContract.Entry.TABLE_NAME,
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		if (c.getCount() > 0) {
			
			list.removeAllViews();
			
			final HomeActivity mySelf = this;

			c.moveToFirst();
			do {
				final long itemId = c
						.getLong(c
								.getColumnIndexOrThrow(DatabaseContract.Entry._ID));
				String itemName = c
						.getString(c
								.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME));
				String itemHostname = c
						.getString(c
								.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_HOSTNAME));
				LinearLayout l = (LinearLayout) layoutInflater.inflate(
						R.layout.raspcontrol_list, null);
				
				LinearLayout lc = (LinearLayout) l.findViewById(R.id.LinearLayoutContainer);
				lc.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mySelf, DisplayEntryActivity.class);
						intent.putExtra(EXTRA_ENTRY_ID, itemId);
						startActivity(intent);
					}
				});

				list.addView(l);
				TextView tv = (TextView) l.findViewById(R.id.raspListName);
				tv.setText(itemName);
				tv = (TextView) l.findViewById(R.id.raspListHostname);
				tv.setText(itemHostname);
				
				Button b = (Button) l.findViewById(R.id.raspListButtonDelete);
				b.setOnClickListener(new Button.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mySelf, DeleteActivity.class);
						intent.putExtra(EXTRA_ENTRY_ID, itemId);
						startActivity(intent);
					}
				});

			} while (c.moveToNext());			
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            Log.d("DEBUG", "Settings button clicked.");
	            return true;
	        case R.id.action_quit:
	        	quitApplication();
	        	return true;
	        case R.id.action_add:
	        	addHost();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		
	}


	public void addHost() {
		Intent intent = new Intent(this, EditActivity.class);
		this.startActivity(intent);
	}

	public void quitApplication() {
		finish();
	}

}
