package anomalyDetector.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.achartengine.GraphicalView;

import android.annotation.SuppressLint;
//import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import anomalyDetector.activities.MainFeaturesActivity;
import anomalyDetector.factories.FeatureExtractorFactory;
import anomalyDetector.featureExtraction.FeatureExtractor;
import anomalyDetector.graph.AnonPagesGraph;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
@SuppressLint("SimpleDateFormat")
public class ColectFeaturesService extends Service {

	private FeatureExtractorFactory factory;
	private ArrayList<String> features;
	private ScheduledExecutorService scheduleTaskExecutor;
	private ArrayList<FeatureExtractor> featureExtractors;
	
	public static String externalStorageDirName = "collected_features";
	//private FileOutputStream fOutputStream;
	private String fileName = "collected_features";
	private String filePath = "collectedFeatureStorage";
	private FileOutputStream externalFileOutputStream;
	private File externalFile;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
	private int elapsedTime = 0;
	/*
	 * Reference to graph object
	 */
	private static AnonPagesGraph lineGraph;
	
	public static final String COLECT_FEATURE_SERVICE = "anomalyDetector.services.ColectFeaturesService";
	
	
	public class LocalBinder extends Binder{
		ColectFeaturesService getService(){
			return ColectFeaturesService.this;
		}
	}
	private final IBinder mBinder = new LocalBinder();
	
	@Override
	public void onCreate() {
		
		factory = new FeatureExtractorFactory();
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
		featureExtractors = new ArrayList<FeatureExtractor>();
		lineGraph = new AnonPagesGraph("Anonymous pages");
		
		/*
		 * Directory in external memory where the data is stored. 
		 * A file in that directory where the data is stored
		 */
		//fileName += dateFormat.format(new Date());
		
		if(isExternalStorageWritable()){
			externalFile = new File(getExternalFilesDir(filePath), fileName);
		}
		else{
			Log.d("Service:EXTERNAL_STORAGE", "External storage is not writable");
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Bundle bundle = intent.getExtras();
		features = bundle.getStringArrayList(MainFeaturesActivity.SELECTED_FEATURES);
		
		createExtractors();
		String fstLine = "";
		for(FeatureExtractor fExtractor : featureExtractors){
			fstLine += fExtractor.getName() + ",";
		}
		
		try {
			if(externalFile.exists()){
				//Log.d("POKRENUT", String.valueOf(externalFile.exists()));
				externalFileOutputStream = new FileOutputStream(externalFile, true);
			}
			else{
				externalFileOutputStream = new FileOutputStream(externalFile, true);
				fstLine += "\n";
				writeToExternalStorage(fstLine);
			}
			/*
			 * File output stream to the file in external storage
			 */
		} catch (FileNotFoundException e) {
			Log.d("Fos error", "Error opening file output stream for external storage");
		}
		
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
			//fOutputStream.close();
			externalFileOutputStream.close();
			//fInputStream.close();
		} catch (IOException e) {
			Log.d("Fos error", "Error closing file output stream");
		}
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
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
			//Log.d(f.getName(), String.valueOf(f.extract()));
			buffer.append(f.extract() + " ");
		}

		buffer.append("\n");
		String line = buffer.toString();
		elapsedTime += 2;
		lineGraph.addNewPoint(line, elapsedTime);
		
		if(!isExternalStorageWritable()){
			Log.d("External storage INFO", "External storage IS NOT writable");
		}
		else{
			writeToExternalStorage(line);
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
	public static boolean isExternalStorageWritable(){
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
	public static boolean isExternalStorageReadable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			return true;
		}
		return false;
	}
	
	/**
	 * Create the directory structure on external storage
	 * where the data will be saved.
	 * @param context Application context
	 * @param directoryName name of file to store the data
	 * @return File reference to where the data will be stored
	 */
	public static File getFileStorageDir(Context context, String directoryName){
		
		File file = new File(context.getExternalFilesDir(null), directoryName);
		if(!file.mkdirs()){
			Log.e("External storage error", "Directory not created");
		}
		
		return file;
	}
	
	public static AnonPagesGraph getLineGraph() {
		return lineGraph;
	}
}
