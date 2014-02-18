package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.RaspDataBaseHelper;
import info.devenet.android.raspcontrol.database.RaspcontrolContract;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class DeleteActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		long itemID = intent.getLongExtra(HomeActivity.EXTRA_ENTRY_ID, 0);
		
		if (itemID > 0) {
			
			RaspDataBaseHelper mDbHelper = new RaspDataBaseHelper(getBaseContext());
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			
			
			// Define 'where' part of query.
			String selection = RaspcontrolContract.RaspEntry._ID + " LIKE ?";
			// Specify arguments in placeholder order.
			String[] selectionArgs = { String.valueOf(itemID) };
			// Issue SQL statement.
			db.delete(RaspcontrolContract.RaspEntry.TABLE_NAME, selection, selectionArgs);
			
		}
		
		finish();
	}

}
