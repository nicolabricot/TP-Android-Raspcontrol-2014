package info.devenet.android.raspcontrol.core;

import info.devenet.android.raspcontrol.Raspcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
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

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class RaspService extends IntentService {

	private String url;
	private long itemID;
	private RaspReader controller;
	private Timer timer;

	public RaspService() {
		super("RaspService");
		this.controller = new RaspReader();
		this.timer = new Timer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.url = intent.getStringExtra(Raspcontrol.EXTRA_ENTRY_URL);
		this.itemID = intent.getLongExtra(Raspcontrol.EXTRA_ENTRY_ID, 0);
		Log.d(Raspcontrol.LOGGER, "Tracker " + this.itemID + " for " + this.url + " laucnhed");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (itemID > 0) {
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d(Raspcontrol.LOGGER, "Tracker " + itemID + " is updating data");
					new HttpTrackerRaspcontrol().execute(url);
				}
			}, 0, Raspcontrol.REFRESH_TIME);
		}
	}

	private class HttpTrackerRaspcontrol extends
			AsyncTask<String, Void, String> {

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
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return "Unknow hostname " + e.getLocalizedMessage();
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
		protected void onPostExecute(String result) {
			try {
				JSONObject json = new JSONObject(result);

				if (!json.has("code")) {
					// error
				}
				if (json.getInt("code") != 200) {
					// error
				} else {
					controller.compare(json);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// error
				timer.cancel();
				timer.purge();
				stopSelf();
				Log.d(Raspcontrol.LOGGER, "Tracker "+ itemID + " cancelled");
			}
		}
	}

}
