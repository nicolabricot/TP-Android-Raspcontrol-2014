package info.devenet.android.raspcontrol.database;

import android.provider.BaseColumns;

public class DatabaseContract {

	public DatabaseContract() {
		// TODO Auto-generated constructor stub
	}
	
	public static abstract class Entry implements BaseColumns {
		public static final String TABLE_NAME = "hosts";
        //public static final String COLUMN_NAME_ENTRY_ID = "entryid";
		public static final String COLUMN_NAME_ENTRY_NAME = "name";
        public static final String COLUMN_NAME_PROTOCOL = "protocol";
        public static final String COLUMN_NAME_HOSTNAME = "hostname";
        public static final String COLUMN_NAME_USERNAME = "user";
        public static final String COLUMN_NAME_TOKEN = "password";
	}	
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	protected static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + Entry.TABLE_NAME + " (" +
	    		Entry._ID + " INTEGER PRIMARY KEY," +
	    		//RaspEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
	    		Entry.COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
	    		Entry.COLUMN_NAME_PROTOCOL + TEXT_TYPE + COMMA_SEP +
	    		Entry.COLUMN_NAME_HOSTNAME + TEXT_TYPE + COMMA_SEP +
	    		Entry.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
	    		Entry.COLUMN_NAME_TOKEN + TEXT_TYPE + 
	    " )";

	protected static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;
	
	
}
