package anomalyDetector.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import anomalyDetector.activities.MainFeaturesActivity;
import anomalyDetector.factories.FeatureExtractorFactory;
import anomalyDetector.featureExtraction.FeatureExtractor;

public class ColectFeaturesService extends Service {

	private FeatureExtractorFactory factory;
	private ArrayList<String> features;
	private ScheduledExecutorService scheduleTaskExecutor;
	private ArrayList<FeatureExtractor> featureExtractors;
	
	private String fileName = "collected_features";
	private FileOutputStream fOutputStream;
	private FileInputStream fInputStream;
	
	public static final String COLECT_FEATURE_SERVICE = "anomalyDetector.services.ColectFeaturesService";
	
	@Override
	public void onCreate() {
		
		//Log.d("krecemo", "krecemo");
		factory = new FeatureExtractorFactory();
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
		featureExtractors = new ArrayList<FeatureExtractor>();
		
		try {
			fOutputStream = openFileOutput(fileName, Context.MODE_APPEND);
			fInputStream = openFileInput(fileName);
		} catch (FileNotFoundException e) {
			Log.d("Fos error", "Error opening file output stream");
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Bundle bundle = intent.getExtras();
		features = bundle.getStringArrayList(MainFeaturesActivity.SELECTED_FEATURES);
		
		createExtractors();
		
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				
				getData();
				//readCollectedData();
				
			}
		}, 2, 2, TimeUnit.SECONDS);
		
		
		return 0;
	}
	
	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		return super.stopService(name);
	}
	
	@Override
	public void onDestroy() {
		
		Log.d("STOP", "Service stoped");
		scheduleTaskExecutor.shutdown();
		try {
			fOutputStream.close();
			fInputStream.close();
		} catch (IOException e) {
			Log.d("Fos error", "Error closing file output stream");
		}
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void createExtractors(){
				
		for(String feature : features){
			FeatureExtractor extractor = factory.createFeatureExtractor(feature, getApplicationContext());
			featureExtractors.add(extractor);
		}
	}
	
	private void getData(){
		
		StringBuffer buffer = new StringBuffer();
		
		for(FeatureExtractor f : featureExtractors){
			Log.d(f.getName(), String.valueOf(f.extract()));
			buffer.append(f.extract() + " ");
		}
		
		buffer.append("\n");
		String line = buffer.toString();
		try {
			fOutputStream.write(line.getBytes());
		} catch (IOException e) {
			Log.d("Fos error", "Error while writing data to file output stream");
		}
		
	}
	
	private void readCollectedData(){
		
		StringBuilder sb = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(fInputStream, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
//            fInputStream.close();
        } catch(OutOfMemoryError om){
            om.printStackTrace();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        String result = sb.toString();
       Log.d("rezultat", result);
	}

}
