package info.devenet.android.raspcontrol.core;

import info.devenet.android.raspcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
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
		JSONObject rbpi_data = json.getJSONObject("rbpi");
		this.rbpi.put("hostname", rbpi_data.getString("hostname"));
		this.rbpi.put("distribution", rbpi_data.getString("distribution"));
		this.rbpi.put("kernel", rbpi_data.getString("kernel"));
		this.rbpi.put("firmware", rbpi_data.getString("firmware"));
		JSONObject ip_data = rbpi_data.getJSONObject("ip");
		this.rbpi.put("external", ip_data.getString("external"));
		this.rbpi.put("internal", ip_data.getString("internal"));
		
		this.uptime = json.getString("uptime");
		
		this.memory_ram = new HashMap<String, String>();
		this.memory_swap = new HashMap<String, String>();
		JSONObject memory_data = json.getJSONObject("memory");
		JSONObject ram_data = memory_data.getJSONObject("ram");
		this.memory_ram.put("percentage", Integer.toString(ram_data.getInt("percentage")));
		this.memory_ram.put("alert", ram_data.getString("alert"));
		this.memory_ram.put("free", Integer.toString(ram_data.getInt("free")));
		this.memory_ram.put("used", Integer.toString(ram_data.getInt("used")));
		this.memory_ram.put("total", ram_data.getString("total"));
		JSONObject swap_data = memory_data.getJSONObject("swap");
		this.memory_swap.put("percentage", Integer.toString(swap_data.getInt("percentage")));
		this.memory_swap.put("alert", swap_data.getString("alert"));
		this.memory_swap.put("free", swap_data.getString("free"));
		this.memory_swap.put("used", swap_data.getString("used"));
		this.memory_swap.put("total", swap_data.getString("total"));
		
	}
	
	
	public void refreshView(Activity a) {
		TextView tv;
		
		tv = (TextView) a.findViewById(R.id.textViewHostname);
		tv.setText(rbpi.get("hostname"));
		tv = (TextView) a.findViewById(R.id.textViewDistribution);
		tv.setText(rbpi.get("distribution"));
		tv = (TextView) a.findViewById(R.id.textViewKernel);
		tv.setText(rbpi.get("kernel"));
		tv = (TextView) a.findViewById(R.id.textViewFirmware);
		tv.setText(rbpi.get("firmware"));
		tv = (TextView) a.findViewById(R.id.textViewInternalIP);
		tv.setText(rbpi.get("internal"));
		tv = (TextView) a.findViewById(R.id.TextViewExternalIP);
		tv.setText(rbpi.get("external"));
		tv = (TextView) a.findViewById(R.id.textViewUptime);
		tv.setText(uptime);
		
		/*
		
		*/
	}
	

}
