package anomalyDetector.activities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
	
	private ListView listView;
	private ArrayAdapter<String> listViewAdapter;
	private ArrayList<String> selectedFeatures;
	
	public final static String SELECTED_FEATURES = "anomalyDetector.activities.SELECTED_FEATURES";
	private Button startServiceButton;
	private Button stopServiceButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_features);
		
//		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//		SharedPreferences.Editor editor = settings.edit();
//	    editor.putString("feature1", "Battery Temperature");
//	    
//	    editor.commit();
		
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
				selectedFeatures.add(line);
			}
			
			reader.close();
			
		} catch (IOException e) {
			Log.d("MainFeaturesActivity", "Error reading assets file");
		}
		
		listViewAdapter = new ArrayAdapter<String>(this, R.layout.row, selectedFeatures);
		listView.setAdapter(listViewAdapter);
	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_features, menu);
		return true;
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
		stopServiceButton.setEnabled(true);
	}
}
