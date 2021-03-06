package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase db;
	private LinearLayout listLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new DatabaseHelper(getBaseContext());

		setContentView(R.layout.activity_main);

		listLayout = (LinearLayout) findViewById(R.id.linearLayoutRasp);

	}

	@SuppressWarnings("unused")
	private void displayNotification(String text, String ticker, int ID) {
		NotificationCompat.Builder builder = new Builder(this);
		builder.setSmallIcon(R.drawable.raspcontrol_small);
		builder.setContentTitle(Raspcontrol.APPLICATION_NAME);
		builder.setContentText(text);
		// builder.setStyle(new
		// NotificationCompat.BigTextStyle().bigText(message));
		builder.setTicker(ticker);
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		builder.setContentIntent(pi);
		Notification notification = builder.build();
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(Raspcontrol.APPLICATION_ID + ID, notification);
	}

	protected void refreshList() {
		listLayout.removeAllViews();

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		db = mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { DatabaseContract.Entry._ID,
				DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME,
				DatabaseContract.Entry.COLUMN_NAME_HOSTNAME };

		// How you want the results sorted in the resulting Cursor
		String sortOrder = DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME
				+ " ASC";

		Cursor c = db.query(DatabaseContract.Entry.TABLE_NAME, projection, // The
																			// columns
																			// to
																			// return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		if (c.getCount() > 0) {

			final MainActivity mySelf = this;

			c.moveToFirst();
			do {
				final long itemId = c.getLong(c
						.getColumnIndexOrThrow(DatabaseContract.Entry._ID));
				String itemName = c
						.getString(c
								.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_ENTRY_NAME));
				String itemHostname = c
						.getString(c
								.getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_HOSTNAME));
				LinearLayout l = (LinearLayout) layoutInflater.inflate(
						R.layout.layout_home_list, null);

				LinearLayout lc = (LinearLayout) l
						.findViewById(R.id.LinearLayoutContainer);
				lc.setPadding(0, 0, 0, 10);
				lc.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mySelf,
								DisplayEntryActivity.class);
						intent.putExtra(Raspcontrol.EXTRA_ENTRY_ID, itemId);
						startActivity(intent);
					}
				});

				listLayout.addView(l);
				TextView tv = (TextView) l.findViewById(R.id.raspListName);
				tv.setText(itemName);
				tv = (TextView) l.findViewById(R.id.raspListHostname);
				tv.setText(itemHostname);

				ImageButton b = (ImageButton) l
						.findViewById(R.id.raspListButtonDelete);
				b.setOnClickListener(new ImageButton.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mySelf, DeleteActivity.class);
						intent.putExtra(Raspcontrol.EXTRA_ENTRY_ID, itemId);
						startActivity(intent);
					}
				});

			} while (c.moveToNext());
			c.close();
			db.close();
		} else {
			TextView tv = new TextView(getBaseContext());
			tv.setLinkTextColor(Color.BLUE);
			tv.setText("Holy crap, no entry found... \nAdd quickly a new entry :)");
			tv.setEnabled(true);
			listLayout.addView(tv);
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
