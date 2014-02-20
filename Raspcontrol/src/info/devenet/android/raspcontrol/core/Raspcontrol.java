package info.devenet.android.raspcontrol.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

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
	
	public Raspcontrol(JSONObject json) {
		
		
		
	}
	

}
