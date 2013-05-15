package anomalyDetector.services;

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
	
	public static final String COLECT_FEATURE_SERVICE = "anomalyDetector.services.ColectFeaturesService";
	
	@Override
	public void onCreate() {
		
		//Log.d("krecemo", "krecemo");
		factory = new FeatureExtractorFactory();
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
		featureExtractors = new ArrayList<FeatureExtractor>();
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
		
		for(FeatureExtractor f : featureExtractors){
			Log.d(f.getName(), String.valueOf(f.extract()));
		}
	}

}
