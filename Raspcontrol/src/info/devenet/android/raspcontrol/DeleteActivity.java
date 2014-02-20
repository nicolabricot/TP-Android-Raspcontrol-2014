package info.devenet.android.raspcontrol;

import info.devenet.android.raspcontrol.database.DatabaseHelper;
import info.devenet.android.raspcontrol.database.DatabaseContract;
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
			
			DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			
			
			// Define 'where' part of query.
			String selection = DatabaseContract.Entry._ID + " LIKE ?";
			// Specify arguments in placeholder order.
			String[] selectionArgs = { String.valueOf(itemID) };
			// Issue SQL statement.
			db.delete(DatabaseContract.Entry.TABLE_NAME, selection, selectionArgs);
			
		}
		
		finish();
	}

}
