package info.devenet.android.raspcontrol.core;

import info.devenet.android.raspcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Raspcontrol {

	public static final String API_FILE = "api.php";
	public static final String API_USERNAME = "username";
	public static final String API_TOKEN = "token";
	public static final String API_DATA = "data";
	public static final String API_DATA_ALL = "all";
	public static final String API_EQUAL = "=";
	public static final String API_FIRST_ARGUMENT = "?";
	public static final String API_OTHER_ARGUMENT = "&";

	private HashMap<String, String> rbpi;
	private String uptime;
	private HashMap<String, String> memory_ram;
	private HashMap<String, String> memory_swap;
	private HashMap<String, String> cpu_usage;
	private HashMap<String, String> cpu_heat;
	private ArrayList<HashMap<String, String>> hdd;
	private HashMap<String, String> net_connections;
	private HashMap<String, String> net_ethernet;
	private HashMap<String, String> users;
	private ArrayList<HashMap<String, String>> services;

	public Raspcontrol(JSONObject json) throws JSONException {

		this.rbpi = new HashMap<String, String>();
		this.memory_ram = new HashMap<String, String>();
		this.memory_swap = new HashMap<String, String>();
		this.cpu_usage = new HashMap<String, String>();
		this.cpu_heat = new HashMap<String, String>();
		this.hdd = new ArrayList<HashMap<String, String>>();

		JSONObject rbpi_data = json.getJSONObject("rbpi");
		this.rbpi.put("hostname", rbpi_data.getString("hostname"));
		this.rbpi.put("distribution", rbpi_data.getString("distribution"));
		this.rbpi.put("kernel", rbpi_data.getString("kernel"));
		this.rbpi.put("firmware", rbpi_data.getString("firmware"));
		JSONObject ip_data = rbpi_data.getJSONObject("ip");
		this.rbpi.put("external", ip_data.getString("external"));
		this.rbpi.put("internal", ip_data.getString("internal"));

		this.uptime = json.getString("uptime");

		JSONObject memory_data = json.getJSONObject("memory");
		JSONObject ram_data = memory_data.getJSONObject("ram");
		this.memory_ram.put("percentage",
				Integer.toString(ram_data.getInt("percentage")));
		this.memory_ram.put("alert", ram_data.getString("alert"));
		this.memory_ram.put("free", Integer.toString(ram_data.getInt("free")));
		this.memory_ram.put("used", Integer.toString(ram_data.getInt("used")));
		this.memory_ram.put("total", ram_data.getString("total"));
		JSONObject swap_data = memory_data.getJSONObject("swap");
		this.memory_swap.put("percentage",
				Integer.toString(swap_data.getInt("percentage")));
		this.memory_swap.put("alert", swap_data.getString("alert"));
		this.memory_swap.put("free", swap_data.getString("free"));
		this.memory_swap.put("used", swap_data.getString("used"));
		this.memory_swap.put("total", swap_data.getString("total"));

		JSONObject cpu_data = json.getJSONObject("cpu");
		JSONObject usage_data = cpu_data.getJSONObject("usage");
		this.cpu_usage.put("alert", usage_data.getString("alert"));
		this.cpu_usage.put("loads",
				Double.toString(usage_data.getDouble("loads")));
		this.cpu_usage.put("loads5",
				Double.toString(usage_data.getDouble("loads5")));
		this.cpu_usage.put("loads15",
				Double.toString(usage_data.getDouble("loads15")));
		this.cpu_usage.put("current", usage_data.getString("current"));
		this.cpu_usage.put("min", usage_data.getString("min"));
		this.cpu_usage.put("max", usage_data.getString("max"));
		this.cpu_usage.put("governor", usage_data.getString("governor"));
		JSONObject heat_data = cpu_data.getJSONObject("heat");
		this.cpu_heat.put("alert", heat_data.getString("alert"));
		this.cpu_heat.put("degrees",
				Integer.toString(heat_data.getInt("degrees")));
		this.cpu_heat.put("percentage",
				Integer.toString(heat_data.getInt("percentage")));

		JSONArray hdd_data = json.getJSONArray("hdd");
		HashMap<String, String> hTemp;
		JSONObject jTemp;
		for (int i = 0; i < hdd_data.length(); i++) {
			hTemp = new HashMap<String, String>();
			jTemp = (JSONObject) hdd_data.get(i);
			hTemp.put("name", jTemp.getString("name"));
			hTemp.put("format", jTemp.getString("format"));
			hTemp.put("percentage", jTemp.getString("percentage"));
			hTemp.put("used", jTemp.getString("used"));
			hTemp.put("free", jTemp.getString("free"));
			hTemp.put("total", jTemp.getString("total"));
			hTemp.put("alert", jTemp.getString("alert"));
			this.hdd.add(hTemp);
		}

	}

	public void refreshView(Activity a) {
		TextView tv;
		ProgressBar pgb;
		LinearLayout ll;
		LayoutInflater layoutInflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout modelLayout;

		/** System */
		tv = (TextView) a.findViewById(R.id.entry_tv_hostname);
		tv.setText(rbpi.get("hostname"));
		tv = (TextView) a.findViewById(R.id.entry_tv_distribution);
		tv.setText(rbpi.get("distribution"));
		tv = (TextView) a.findViewById(R.id.textViewKernel);
		tv.setText(rbpi.get("kernel"));
		tv = (TextView) a.findViewById(R.id.textViewFirmware);
		tv.setText(rbpi.get("firmware"));

		/** Uptime */
		tv = (TextView) a.findViewById(R.id.entry_tv_uptime);
		tv.setText(uptime);

		/** RAM */
		pgb = (ProgressBar) a.findViewById(R.id.entry_pgb_ram);
		pgb.setProgress(Integer.parseInt(this.memory_ram.get("percentage")));
		tv = (TextView) a.findViewById(R.id.entry_tv_ram_free);
		tv.setText(this.memory_ram.get("free") + "Mb");
		tv = (TextView) a.findViewById(R.id.entry_tv_ram_used);
		tv.setText(this.memory_ram.get("used") + "Mb");
		tv = (TextView) a.findViewById(R.id.entry_tv_ram_total);
		tv.setText(this.memory_ram.get("total") + "Mb");
		tv = (TextView) a.findViewById(R.id.entry_titleRAM);
		tv.setTextColor(this.alertToColor(this.memory_ram.get("alert")));

		/** Swap */
		pgb = (ProgressBar) a.findViewById(R.id.entry_pgb_swap);
		pgb.setProgress(Integer.parseInt(this.memory_swap.get("percentage")));
		tv = (TextView) a.findViewById(R.id.entry_tv_swap_free);
		tv.setText(this.memory_swap.get("free") + "Mb");
		tv = (TextView) a.findViewById(R.id.entry_tv_swap_used);
		tv.setText(this.memory_swap.get("used") + "Mb");
		tv = (TextView) a.findViewById(R.id.entry_tv_swap_total);
		tv.setText(this.memory_swap.get("total") + "Mb");
		tv = (TextView) a.findViewById(R.id.entry_titleSwap);
		tv.setTextColor(this.alertToColor(this.memory_ram.get("alert")));

		/** CPU */
		tv = (TextView) a.findViewById(R.id.entry_tv_loads);
		tv.setText(this.cpu_usage.get("loads"));
		tv = (TextView) a.findViewById(R.id.entry_tv_loads5);
		tv.setText(this.cpu_usage.get("loads5"));
		tv = (TextView) a.findViewById(R.id.entry_tv_loads15);
		tv.setText(this.cpu_usage.get("loads15"));
		tv = (TextView) a.findViewById(R.id.entry_tv_governor);
		tv.setText(this.cpu_usage.get("governor"));
		tv = (TextView) a.findViewById(R.id.entry_tv_frequency);
		tv.setText(this.cpu_usage.get("current"));
		tv = (TextView) a.findViewById(R.id.entry_titleCPU);
		tv.setTextColor(this.alertToColor(this.cpu_usage.get("alert")));

		/** CPU heat */
		pgb = (ProgressBar) a.findViewById(R.id.entry_pgb_heat);
		pgb.setProgress(Integer.parseInt(this.cpu_heat.get("percentage")));
		tv = (TextView) a.findViewById(R.id.entry_tv_heat);
		tv.setText(this.cpu_heat.get("degrees") + "°C");
		tv = (TextView) a.findViewById(R.id.entry_titleHeat);
		tv.setTextColor(this.alertToColor(this.cpu_heat.get("alert")));

		/** Storage */
		ll = (LinearLayout) a.findViewById(R.id.entry_layoutStorage);
		ll.removeAllViews();
		String storageAlert = "success";
		for (HashMap<String, String> disk : this.hdd) {
			modelLayout = (LinearLayout) layoutInflater.inflate(
					R.layout.layout_storage_list, null);
			tv = (TextView) modelLayout.findViewById(R.id.storage_name);
			tv.setText(disk.get("name"));
			tv = (TextView) modelLayout.findViewById(R.id.storage_format);
			tv.setText(disk.get("format"));
			pgb = (ProgressBar) modelLayout.findViewById(R.id.storage_pgb);
			pgb.setProgress(Integer.parseInt(disk.get("percentage")));
			tv = (TextView) modelLayout.findViewById(R.id.storage_tv_used);
			tv.setText(disk.get("used"));
			tv = (TextView) modelLayout.findViewById(R.id.storage_tv_free);
			tv.setText(disk.get("free"));
			tv = (TextView) modelLayout.findViewById(R.id.storage_tv_total);
			tv.setText(disk.get("total"));
			tv = (TextView) modelLayout.findViewById(R.id.storage_name);
			tv.setTextColor(this.alertToColor(disk.get("alert")));
			if ((storageAlert.equals("success") && !disk.get("alert").equals("success"))
				|| (storageAlert.equals("warning") && disk.get("alert").equals("danger"))) {
				storageAlert = disk.get("alert");
			}
			modelLayout.setPadding(0, 0, 0, 4);
			ll.addView(modelLayout);
		}
		tv = (TextView) a.findViewById(R.id.entry_titleStorage);
		tv.setTextColor(this.alertToColor(storageAlert));

		/** Network */
		tv = (TextView) a.findViewById(R.id.entry_tv_internalIP);
		tv.setText(rbpi.get("internal"));
		tv = (TextView) a.findViewById(R.id.entry_tv_externalIP);
		tv.setText(rbpi.get("external"));

	}

	private int alertToColor(String alert) {
		if (alert.equals("danger")) {
			return Color.rgb(221, 81, 76);
		} else if (alert.equals("warning")) {
			return Color.rgb(250, 167, 50);
		} else {
			return Color.rgb(94, 185, 95);
		}
	}

}
