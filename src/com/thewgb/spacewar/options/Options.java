package com.thewgb.spacewar.options;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thewgb.spacewar.Game;
import com.thewgb.spacewar.util.Logger;

public class Options {
	private static final File FILE = new File("options.txt");
	
	private Map<String, Object> values;
	private List<OptionEventListener> listeners;
	
	public Options() {
		this.values = new HashMap<>();
		this.listeners = new ArrayList<>();
		
		reloadConfig();
		
		String optionsVer = getString("options_version") == null ? "none" : getString("options_version");
		Logger.debug(optionsVer);
		if(!optionsVer.equalsIgnoreCase(Game.version.replace(" ", ""))) {
			try {
				createConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void set(String key, Object val) {
		Object oldVal = null;
		
		if(values.containsKey(key)) {
			oldVal = values.get(key);
			values.remove(key);
		}
		
		values.put(key, val);
		
		for(OptionEventListener oel : listeners)
			oel.optionChangeEvent(key, oldVal, val);
	}
	
	public Object get(String key) {
		if(!values.containsKey(key))
			return null;
		
		return values.get(key);
	}
	
	public Byte getByte(String key) {
		if(!values.containsKey(key))
			return 0;
		
		return Byte.valueOf(String.valueOf(values.get(key)));
	}
	
	public Short getShort(String key) {
		if(!values.containsKey(key))
			return 0;
		
		return Short.valueOf(String.valueOf(values.get(key)));
	}
	
	public Integer getInt(String key) {
		if(!values.containsKey(key))
			return 0;
		
		return Integer.parseInt(String.valueOf(values.get(key)));
	}
	
	public Long getLong(String key) {
		if(!values.containsKey(key))
			return 0L;
		
		return Long.valueOf(String.valueOf(values.get(key)));
	}
	
	public Float getFloat(String key) {
		if(!values.containsKey(key))
			return 0F;
		
		return Float.valueOf(String.valueOf(values.get(key)));
	}
	
	public Double getDouble(String key) {
		if(!values.containsKey(key))
			return 0D;
		
		return Double.valueOf(String.valueOf(values.get(key)));
	}
	
	public Boolean getBoolean(String key) {
		if(!values.containsKey(key))
			return false;
		
		return Boolean.valueOf(String.valueOf(values.get(key)));
	}
	
	public String getString(String key) {
		if(!values.containsKey(key))
			return null;
		
		return String.valueOf(values.get(key));
	}
	
	public List<Object> getList(String key) {
		List<Object> res = new ArrayList<>();
		
		if(!values.containsKey(key))
			return res;
		
		String strList = getString(key);
		
		if(!strList.contains(",")) {
			Logger.warning("Invaild list in config! " + key);
			return res;
		}
		
		strList = strList.replace("[", "").replace("]", "").replace(", ", ",");
		
		String[] parts = strList.split(",");
		for(String s : parts) {
			res.add(s);
		}
		
		return res;
	}
	
	public List<String> getStringList(String key) {
		List<String> res = new ArrayList<>();
		
		if(!values.containsKey(key))
			return res;
		
		String strList = getString(key);
		
		if(!strList.contains(",")) {
			Logger.warning("Invaild list in config! " + key);
			return res;
		}
		
		strList = strList.replace("[", "").replace("]", "").replace(", ", ",");
		
		String[] parts = strList.split(",");
		for(String s : parts) {
			res.add(s);
		}
		
		return res;
	}
	
	public List<Integer> getIntList(String key) {
		List<Integer> res = new ArrayList<>();
		
		if(!values.containsKey(key))
			return res;
		
		String strList = getString(key);
		
		if(!strList.contains(",")) {
			Logger.warning("Invaild list in config! " + key);
			return res;
		}
		
		strList = strList.replace("[", "").replace("]", "").replace(", ", ",");
		
		String[] parts = strList.split(",");
		for(String s : parts) {
			res.add(Integer.parseInt(s));
		}
		
		return res;
	}
	
	public void addEventListener(OptionEventListener oel) {
		listeners.add(oel);
	}
	
	public void removeEventListener(OptionEventListener oel) {
		listeners.remove(oel);
	}
	
	public void reloadConfig() {
		if(!FILE.exists()) {
			try{
				createConfig();
			}catch(IOException e) {
				Logger.error("Could not save " + FILE.getName() + "! " + e);
				return;
			}
		}
		
		values = new HashMap<>();
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(FILE));
			
			String line = reader.readLine();
			while(line != null) {
				line = line.trim().replace(" ", "");
				if(line.equalsIgnoreCase("") || line.startsWith("#") || !line.contains("=")) {
					line = reader.readLine();
					continue;
				}
				
				String[] parts = line.split("=");
				String key = parts[0];
				Object val = parts[1];
				
				values.put(key, val);
				
				line = reader.readLine();
			}
			
			reader.close();
		}catch(IOException e) {
			Logger.error("Could not read " + FILE.getName() + "! " + e);
		}
	}
	
	public boolean saveConfig() {
		boolean succeed = false;
		
		try{
			if(FILE.exists()) {
				FILE.delete();
				FILE.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE));
			
			for(String key : values.keySet()) {
				Object val = values.get(key);
				writer.write(key + "=" + String.valueOf(val));
				writer.newLine();
			}
			
			writer.close();
			succeed = true;
		}catch(IOException e) {
			Logger.error("Could not save " + FILE.getName() + "! " + e);
		}
		
		for(OptionEventListener oel : listeners)
			oel.optionSaveEvent(succeed);
		
		return succeed;
	}
	
	
	
	private void createConfig() throws IOException {
		if(FILE.exists())
			FILE.delete();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/options.txt")));
		BufferedWriter writer = new BufferedWriter(new FileWriter(FILE));
		
		writer.write("options_version=" + Game.version);
		writer.newLine();
		
		String line = reader.readLine();
		while(line != null) {
			writer.write(line);
			writer.newLine();
			
			line = reader.readLine();
		}
		
		reader.close();
		writer.close();
	}
}
