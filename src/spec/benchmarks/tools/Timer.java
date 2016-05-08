package spec.benchmarks.tools;

import java.util.*;
import org.apache.commons.lang3.time.StopWatch;

public class Timer {
	private static Timer instance = null;
	private Map<String, Long> timeBank = new HashMap<String, Long>();
	private Map<String, StopWatch> watchStore = new HashMap<String, StopWatch>();
	
	protected Timer() {
		// Exists only to defeat instantiation.
	}
	
	public StopWatch getWatch(String key) {
		StopWatch watch = watchStore.get(key);
		if (watch == null) {
			watch = new StopWatch();
			watch.start();
			watch.suspend();
			watchStore.put(key, watch);
		}
		return watch;
	}
	
	public void printTimer () {
		System.out.println("Start printing StopWatch...");
		for (Map.Entry<String, StopWatch> entry : watchStore.entrySet())
		{
			entry.getValue().stop();
		    System.out.println(entry.getKey() + ": " + entry.getValue().getTime() + "ms");
		}
	}
	
	public static Timer getInstance() {
		if (instance == null) {
			instance = new Timer();
		}
		return instance;
	}
	
	public void setTime(String name, Long time) {	
		if (timeBank.get(name) != null) {
			time += timeBank.get(name);
			timeBank.replace(name, time);
		}
		else {
			timeBank.put(name, time);
		}
	}
	
	public Long getTime(String name) {
		return timeBank.get(name);
	}
	
	public void print() {
		if (timeBank.size() == 0) {
			System.out.println("Nothing is track in Timer.");
		}
		else {
			for (Map.Entry<String, Long> entry : timeBank.entrySet())
			{
			    System.out.println(entry.getKey() + ": " + entry.getValue() + "ms");
			}
		}
	}
}
