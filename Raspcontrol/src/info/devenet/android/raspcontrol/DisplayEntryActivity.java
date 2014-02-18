package info.devenet.android.raspcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import info.devenet.android.raspcontrol.database.RaspDataBaseHelper;
import info.devenet.android.raspcontrol.database.RaspcontrolContract;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class DisplayEntryActivity extends Activity {
	
	Context context;
	TableLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_entry);
		// Show the Up button in the action bar.
		setupActionBar();
		
		this.context = getApplicationContext();
		this.layout = (TableLayout) findViewById(R.id.TableLayoutEntry);
		
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
			
			setTitle(itemName);
			
					
			HttpGetterRaspcontrol get = new HttpGetterRaspcontrol();
			get.execute("http://ensisa.devenet.info/server/api.php?username=fake&token=d985dfa98e44dda4cbd979affce1cf53&data=all");	
			
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

	private class HttpGetterRaspcontrol extends AsyncTask<String, Void, String> {

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
					Log.e("Getter", "Failed to download file");
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return builder.toString();
		}
		
		@Override
	    protected void onPostExecute(String result) {
			
			if (result == null) {
				Toast.makeText(context, "PROBLEM, HUSTON, WE HAVE A BIG PROBLEM!", Toast.LENGTH_LONG).show();
				return;
			}
			
			
			try {
				JSONObject json = new JSONObject(result);
				
				JSONObject rbpi = json.getJSONObject("rbpi");
				
				TextView tv;
				tv = (TextView) findViewById(R.id.textViewHostname);
				tv.setText(rbpi.getString("hostname"));
				
				tv = (TextView) findViewById(R.id.textViewDistribution);
				tv.setText(rbpi.getString("distribution"));
				
				tv = (TextView) findViewById(R.id.textViewKernel);
				tv.setText(rbpi.getString("kernel"));
				
				tv = (TextView) findViewById(R.id.textViewFirmware);
				tv.setText(rbpi.getString("firmware"));
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	
}
