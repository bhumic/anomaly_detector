package anomalyDetector.featureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.TextView;

public class MainFeaturesActivity extends Activity {

	private TextView batteryTemperatureView;
	private TextView totalEntitiesView;
	private TextView anonymousPagesView;
	private TextView mappedPagesView;
	private TextView runningProcessesView;
	
	private File loadAvgFile = new File("/proc/loadavg");
	private File statFile = new File("/proc/stat");
	private File memoryFile = new File("/proc/meminfo");
	
	private int totalEntities;
	private int runningProcesses;
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context arg0, Intent intent) {
	      // TODO Auto-generated method stub
	      double temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.;
	      batteryTemperatureView.setText("Battery temperature: " + String.valueOf(temperature) + " C");
	    }
	  };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_features);
		
		batteryTemperatureView = (TextView) this.findViewById(R.id.battery_temperature);
		totalEntitiesView = (TextView) this.findViewById(R.id.total_entites);
		runningProcessesView = (TextView) this.findViewById(R.id.running_processes);
		anonymousPagesView = (TextView) this.findViewById(R.id.anonymous_pages);
		mappedPagesView = (TextView) this.findViewById(R.id.mapped_pages);
	    
		this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		Thread t = new Thread() {

	        @Override
	        public void run() {
	            try {
	                while (!isInterrupted()) {
	                    Thread.sleep(3000);
	                    runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                            updateTextViews();
	                        }
	                    });
	                }
	            } catch (InterruptedException e) {
	            }
	        }
	    };

	    t.start();
	    
	}
	
	public void updateTextViews() {
		
		int[] pages;
		totalEntitiesView.setText("Total Entities: " + String.valueOf(getTotalEntities()));
		runningProcessesView.setText("Running processes: " + String.valueOf(getRunningProcesses()));
		pages = getPagesInformation();
		anonymousPagesView.setText("Anonymous pages: " + pages[0] + " kB");
		mappedPagesView.setText("Mapped pages: " + pages[1] + " kB");
	}

	
	public int getTotalEntities() {
		
		BufferedReader reader;
		String line;
		
		try {
			reader = new BufferedReader(new FileReader(loadAvgFile));
			line = reader.readLine().split("\\s+")[3];
			totalEntities = Integer.parseInt(line.split("/")[1]);
		} catch (FileNotFoundException e) {
			System.err.println("/proc/loadavg: Error while reading file.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("/proc/loadavg: Error while reading line");
			System.exit(1);
		}
		
		return totalEntities;
	}
	
	public int[] getPagesInformation() {
		
		int[] pages = new int[2];
		
		BufferedReader reader;
		String line;
		
		try {
			reader = new BufferedReader(new FileReader(memoryFile));
			while((line = reader.readLine()) != null){
				if(line.contains("AnonPages")){
					pages[0] = Integer.parseInt(line.split("\\s+")[1]);
				}
				else if(line.contains("Mapped")) {
					pages[1] = Integer.parseInt(line.split("\\s+")[1]);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("/proc/loadavg: Error while reading file.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("/proc/loadavg: Error while reading line");
			System.exit(1);
		}
		
		return pages;
	}
	
	public int getRunningProcesses(){
		
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = manager.getRunningAppProcesses();
		
		runningProcesses = runningAppProcessesList.size();
		
		return runningProcesses;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_features, menu);
		return true;
	}

}
