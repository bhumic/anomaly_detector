package anomalyDetector.activities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationErrorReport.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import anomalyDetector.featureExtractor.R;
import anomalyDetector.services.ColectFeaturesService;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
public class MainFeaturesActivity extends Activity {
	
	private String featureFileName = "features";
	private FileInputStream fis;
	private FileOutputStream fos;
	public static final String PREFS_NAME = "FeatureExtractionPreferences";
	public final static String COLLECTED_DATA = "anomalyDetector.activities.COLLECTED_DATA";
	
	private ListView listView;
	private ArrayAdapter<String> listViewAdapter;
	private ArrayList<String> selectedFeatures;
	
	public final static String SELECTED_FEATURES = "anomalyDetector.activities.SELECTED_FEATURES";
	private Button startServiceButton;
	private Button stopServiceButton;
	
	private String fileName = "collected_features";
	private FileInputStream fInputStream;
	
	boolean isServiceRunning = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_features);
		
		listView = (ListView) findViewById(R.id.featuresListView);
		selectedFeatures = new ArrayList<String>();
		
		/*
		 * List of features that can be collected
		 */
		AssetManager assetManager = getAssets();
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new InputStreamReader(assetManager.open("features.txt")));
			String line;
			while((line = reader.readLine()) != null){
				if(line.startsWith("#")){
					continue;
				}
				selectedFeatures.add(line);
			}
			
			reader.close();
			
		} catch (IOException e) {
			Log.d("MainFeaturesActivity", "Error reading assets file");
		}
		
		listViewAdapter = new ArrayAdapter<String>(this, R.layout.row, selectedFeatures);
		listView.setAdapter(listViewAdapter);
		
		/**
		 * Check if the service is running and enable/disable the buttons
		 */
		
		ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(100);
		for(ActivityManager.RunningServiceInfo rsi : rs){
			String serviceClassName = rsi.service.getClassName();
			if(serviceClassName.equals("anomalyDetector.services.ColectFeaturesService")){
				isServiceRunning = true;
				break;
			}
		}
		
		if(isServiceRunning){
			startServiceButton = (Button) findViewById(R.id.startServiceButton);
			startServiceButton.setEnabled(false);
			stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
			stopServiceButton.setEnabled(true);
		}
		else{
			startServiceButton = (Button) findViewById(R.id.startServiceButton);
			startServiceButton.setEnabled(true);
			stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
			stopServiceButton.setEnabled(false);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_features, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		String collectedData = readCollectedData();
		
		if(collectedData == null){
			Toast toast = Toast.makeText(getApplicationContext(), "No data to show", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		switch(item.getItemId()){
		
		case R.id.show_anon_pages:
			Intent intent = new Intent(this, ShowAnonPagesActivity.class);
			intent.putExtra(COLLECTED_DATA, collectedData);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void startService(View view){
		
		Intent intent = new Intent(ColectFeaturesService.COLECT_FEATURE_SERVICE);
		
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(SELECTED_FEATURES, selectedFeatures);
		
		intent.putExtras(bundle);
		
		startService(intent);
		
		startServiceButton = (Button) findViewById(R.id.startServiceButton);
		startServiceButton.setEnabled(false);
		stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
		stopServiceButton.setEnabled(true);
	}
	
	public void stopService(View view){
		
		Intent intent = new Intent(ColectFeaturesService.COLECT_FEATURE_SERVICE);

		stopService(intent);
		
		startServiceButton = (Button) findViewById(R.id.startServiceButton);
		startServiceButton.setEnabled(true);
		stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
		stopServiceButton.setEnabled(false);
		isServiceRunning = false;
	}
	
	public String readCollectedData(){
		
		StringBuilder sb = new StringBuilder();
		try{
			fInputStream = openFileInput(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fInputStream, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			fInputStream.close();
		} catch(OutOfMemoryError om){
			om.printStackTrace();
		} catch(FileNotFoundException ex){
			return null;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		String result = sb.toString();
		return result;
		//Log.d("rezultat", result);
	}
}
