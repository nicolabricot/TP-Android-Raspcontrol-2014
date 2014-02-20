package info.devenet.android.raspcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import info.devenet.android.raspcontrol.core.Raspcontrol;
import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class DisplayEntryActivity extends Activity {

	private Context context;
	@SuppressWarnings("unused")
	private TableLayout layout;
	private long itemID = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show the Up button in the action bar.
		setupActionBar();

		this.context = getApplicationContext();
		this.layout = (TableLayout) findViewById(R.id.TableLayoutEntry);

		Intent intent = getIntent();
		long itemID = intent.getLongExtra(HomeActivity.EXTRA_ENTRY_ID, 0);
		this.itemID = itemID;
		
		this.loadDisplay();

		setContentView(R.layout.activity_display_entry);

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
		getMenuInflater().inflate(R.menu.display_entry, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
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
		case R.id.action_delete:
			finish();
			intent = new Intent(this, DeleteActivity.class);
			intent.putExtra(HomeActivity.EXTRA_ENTRY_ID, this.itemID);
			startActivity(intent);
			return true;
		case R.id.action_edit:
			intent = new Intent(this, EditActivity.class);
			intent.putExtra(HomeActivity.EXTRA_ENTRY_ID, this.itemID);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			this.loadDisplay();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadDisplay() {
		if (this.itemID > 0) {

			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				// We have the network :)

				DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
				SQLiteDatabase db = mDbHelper.getReadableDatabase();

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

				Cursor c = db.query(DatabaseContract.Entry.TABLE_NAME,
						projection, // The columns to return
						selection, // The columns for the WHERE clause
						selectionArgs, // The values for the WHERE clause
						null, // don't group the rows
						null, // don't filter by row groups
						sortOrder // The sort order
						);

				c.moveToFirst();

				@SuppressWarnings("unused")
				long itemId = c.getLong(c
						.getColumnIndexOrThrow(DatabaseContract.Entry._ID));
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
				db.close();

				setTitle(itemName);

				String url = itemProtocol + "://" + itemHostname + "/"
						+ Raspcontrol.API_FILE + Raspcontrol.API_FIRST_ARGUMENT
						+ Raspcontrol.API_USERNAME + Raspcontrol.API_EQUAL
						+ itemUsername + Raspcontrol.API_OTHER_ARGUMENT
						+ Raspcontrol.API_TOKEN + Raspcontrol.API_EQUAL
						+ itemToken + Raspcontrol.API_OTHER_ARGUMENT
						+ Raspcontrol.API_DATA + Raspcontrol.API_EQUAL
						+ Raspcontrol.API_DATA_ALL;
				
				new HttpGetterRaspcontrol().execute(url);

			} else {
				// we have a probem huston!
				Toast.makeText(context, R.string.error_no_connectivity,
						Toast.LENGTH_LONG).show();
			}

		}
	}

	private class HttpGetterRaspcontrol extends AsyncTask<String, Void, String> {
		
		private Timer timer;
		private Toast loadingToast;
		
		@SuppressLint("ShowToast")
		@Override
		protected void onPreExecute() {
			this.loadingToast = Toast.makeText(getBaseContext(), R.string.loading, Toast.LENGTH_SHORT);
			this.timer = new Timer();
			this.timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					publishProgress();
				}
			}, 0, 1000);
		}

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urls[0]);
			
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					return "Error " + statusLine.getStatusCode() + ": "
							+ statusLine.getReasonPhrase();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}

			return builder.toString();
		}
		
		@Override
		protected void onProgressUpdate(Void... progress) {
	         loadingToast.show();
	     }
		

		@Override
		protected void onPostExecute(String result) {
			
			this.timer.cancel();
			this.timer.purge();
			loadingToast.cancel();

			try {
				JSONObject json = new JSONObject(result);

				if (!json.has("code")) {
					Toast.makeText(context, "Invalid response from API",
							Toast.LENGTH_LONG).show();
				}

				if (json.getInt("code") != 200) {
					Toast.makeText(context, json.getString("error"),
							Toast.LENGTH_LONG).show();
				} else {

					JSONObject rbpi = json.getJSONObject("rbpi");
					JSONObject ip = rbpi.getJSONObject("ip");
					String uptime = json.getString("uptime");

					TextView tv;
					tv = (TextView) findViewById(R.id.textViewHostname);
					tv.setText(rbpi.getString("hostname"));

					tv = (TextView) findViewById(R.id.textViewDistribution);
					tv.setText(rbpi.getString("distribution"));

					tv = (TextView) findViewById(R.id.textViewKernel);
					tv.setText(rbpi.getString("kernel"));

					tv = (TextView) findViewById(R.id.textViewFirmware);
					tv.setText(rbpi.getString("firmware"));

					tv = (TextView) findViewById(R.id.textViewInternalIP);
					tv.setText(ip.getString("internal"));

					tv = (TextView) findViewById(R.id.TextViewExternalIP);
					tv.setText(ip.getString("external"));

					tv = (TextView) findViewById(R.id.textViewUptime);
					tv.setText(uptime);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// finish();
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			}
		}
	}

}
