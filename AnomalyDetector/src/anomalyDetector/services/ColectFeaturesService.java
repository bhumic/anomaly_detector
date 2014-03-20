package anomalyDetector.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
//import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import anomalyDetector.activities.MainFeaturesActivity;
import anomalyDetector.factories.FeatureExtractorFactory;
import anomalyDetector.featureExtraction.FeatureExtractor;
import anomalyDetector.graph.AnonPagesGraph;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class ColectFeaturesService extends Service {

	private FeatureExtractorFactory factory;
	private ArrayList<String> features;
	private ScheduledExecutorService scheduleTaskExecutor;
	private ArrayList<FeatureExtractor> featureExtractors;
	
	public static String externalStorageDirName = "collected_features";
	private String fileName = "collected_features";
	private String filePath = "collectedFeatureStorage";
	private FileOutputStream externalFileOutputStream;
	private File externalFile;
	private int elapsedTime = 0;
	
	public static final int MSG_DATA_RECEIVED = 0;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	ArrayList<Messenger> toClientMessengers;
	
	//Handler object that handles incoming messages from clients.
	//In this case, it performs only the registration/unregistration
	//of the clients.
	class IncomingHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case ColectFeaturesService.MSG_REGISTER_CLIENT:
				toClientMessengers.add(msg.replyTo);
				break;
			case ColectFeaturesService.MSG_UNREGISTER_CLIENT:
				toClientMessengers.remove(msg.replyTo);
				break;
			}
		}
	}
	final Messenger fromClientMessenger = new Messenger(new IncomingHandler());
	
	//Reference to graph objects 
	private static AnonPagesGraph anonPagesGraph;
	
	public static final String COLECT_FEATURE_SERVICE = "anomalyDetector.services.ColectFeaturesService";
	
	
	@Override
	public void onCreate() {
		
		factory = new FeatureExtractorFactory();
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
		featureExtractors = new ArrayList<FeatureExtractor>();
		anonPagesGraph = new AnonPagesGraph("Anonymous pages");
		toClientMessengers = new ArrayList<Messenger>();
		
		if(isExternalStorageWritable()){
			externalFile = new File(getExternalFilesDir(filePath), fileName);
		}
		else{
			Log.d("Service:EXTERNAL_STORAGE", "External storage is not writable");
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		/*
		 * Obtain features that need to be collected.
		 */
		Bundle bundle = intent.getExtras();
		features = bundle.getStringArrayList(MainFeaturesActivity.SELECTED_FEATURES);
		
		//Create feature extractor objects.
		createExtractors();
		
		/*
		 * Variable that contains the name of all the features that
		 * are going to be extracted.
		 */
		String fstLine = "";
		for(FeatureExtractor fExtractor : featureExtractors){
			fstLine += fExtractor.getName() + ",";
		}
		
		try {
			/*
			 * null condition added to check whether the external storage is
			 * mounted or not.
			 */
			if(externalFile != null && externalFile.exists()){
				externalFileOutputStream = new FileOutputStream(externalFile, true);
			}
			else if(externalFile != null){
				externalFileOutputStream = new FileOutputStream(externalFile, true);
				fstLine += "\n";
				writeToExternalStorage(fstLine);
			}
		} catch (FileNotFoundException e) {
			Log.d("Fos error", "Error opening file output stream for external storage");
		}
		
		/*
		 * Collect data every 2 seconds from the data extractors
		 */
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				
				getData();
				
			}
		}, 2, 2, TimeUnit.SECONDS);
		
		return START_STICKY;
	}
	
	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}
	
	@Override
	public void onDestroy() {
		
		Log.d("STOP", "Service stoped");
		scheduleTaskExecutor.shutdown();
		try {
			externalFileOutputStream.close();
		} catch (IOException e) {
			Log.d("Fos error", "Error closing file output stream");
		}
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return fromClientMessenger.getBinder();
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
			buffer.append(f.extract() + " ");
		}

		buffer.append("\n");
		String line = buffer.toString();
		elapsedTime += 2;
		anonPagesGraph.addNewPoint(line, elapsedTime);
		
		notifyActivityObservers();
		
		if(!isExternalStorageWritable()){
			Log.d("External storage INFO", "External storage IS NOT writable");
		}
		else{
			writeToExternalStorage(line);
		}
	}
	
	public void notifyActivityObservers(){
		
		for(Messenger toClientMessenger : toClientMessengers){
			try {
				if(toClientMessenger != null){
					Message msg = Message.obtain(null, MSG_DATA_RECEIVED);
					toClientMessenger.send(msg);
				}
			} catch (RemoteException e) {
				Log.d("SERVICE-ACTIVITY", "Error communicating to activity from service");
			}
		}
	}
	
	/**
	 * Write collected data to external storage
	 * @param line Feature values that will be written to external storage
	 * @return True if the writing was successful, false otherwise
	 */
	private boolean writeToExternalStorage(String line){
		
		try {
			if(isExternalStorageWritable() && externalFileOutputStream != null){
				externalFileOutputStream.write(line.getBytes());
				externalFileOutputStream.flush();
			}
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
		return anonPagesGraph;
	}
}
