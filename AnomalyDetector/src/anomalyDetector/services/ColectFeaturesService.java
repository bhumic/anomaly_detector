package anomalyDetector.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;




//import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import anomalyDetector.activities.MainFeaturesActivity;
import anomalyDetector.factories.FeatureExtractorFactory;
import anomalyDetector.featureExtraction.FeatureExtractor;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
public class ColectFeaturesService extends Service {

	private FeatureExtractorFactory factory;
	private ArrayList<String> features;
	private ScheduledExecutorService scheduleTaskExecutor;
	private ArrayList<FeatureExtractor> featureExtractors;
	
	private String fileName = "collected_features";
	private FileOutputStream fOutputStream;
	
	private String externalFileName = "external_collected_features";
	private FileOutputStream externalFileOutputStream;
	private File externalDirectory;
	private File externalFile;
	
	public static final String COLECT_FEATURE_SERVICE = "anomalyDetector.services.ColectFeaturesService";
	
	@Override
	public void onCreate() {
		
		factory = new FeatureExtractorFactory();
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
		featureExtractors = new ArrayList<FeatureExtractor>();
		
		/*
		 * Directory in external memory where the data is stored. 
		 * A file in that directory where the data is stored
		 */
		externalDirectory = getFileStorageDir(getApplicationContext(), fileName);
		externalFile = new File(externalDirectory.getAbsolutePath(), externalFileName);
		
		try {
			fOutputStream = openFileOutput(fileName, Context.MODE_APPEND);
			/*
			 * File output stream to the file in external storage
			 */
			externalFileOutputStream = new FileOutputStream(externalFile);
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
			externalFileOutputStream.close();
			//fInputStream.close();
		} catch (IOException e) {
			Log.d("Fos error", "Error closing file output stream");
		}
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		//TODO
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
		
		if(!isExternalStorageWritable()){
			Log.d("External storage INFO", "External storage IS NOT writable");
		}
		else{
			writeToExternalStorage(line);
		}
		
		
		try {
			fOutputStream.write(line.getBytes());
			fOutputStream.flush();
		} catch (IOException e) {
			Log.d("Fos error", "Error while writing data to file output stream");
		}
	}
	
	/**
	 * Write collected data to external storage
	 * @param line Feature values that will be written to external storage
	 * @return True if the writing was successful, false otherwise
	 */
	private boolean writeToExternalStorage(String line){
		
		try {
			externalFileOutputStream.write(line.getBytes());
			externalFileOutputStream.flush();
		} catch (IOException e) {
			Log.d("ERROR EXTERNAL MEM", "Errow while writing file to external memory");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if external storage is available for read and write
	 * @return True if external storage is available, false otherwise
	 */
	public boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if external storage is available to at least reading
	 * @return True if external storage is at least readable, false otherwise
	 */
	public boolean isExternalStorageReadable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			return true;
		}
		return false;
	}
	
	/**
	 * Directory where the data will be stored
	 * @param context Application context
	 * @param fileName name of file to store the data
	 * @return File reference to where the data will be stored
	 */
	public File getFileStorageDir(Context context, String fileName){
		
		File file = new File(context.getExternalFilesDir(null), fileName);
		if(!file.mkdirs()){
			Log.e("External storage error", "Directory not created");
		}
		return file;
	}
}
