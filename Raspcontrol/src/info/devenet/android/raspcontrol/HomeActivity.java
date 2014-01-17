package info.devenet.android.raspcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class HomeActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "info.devenet.android.raspcontrol.MESSAGE";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
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
