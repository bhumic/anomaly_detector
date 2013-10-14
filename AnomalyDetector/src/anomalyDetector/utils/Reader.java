package anomalyDetector.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class Reader extends Thread{
	
	InputStream is;
	
	public Reader(InputStream is){
		this.is = is;
	}
	
	public void run(){
		InputStreamReader inStreamReader = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(inStreamReader);
		try {
			Log.d("CPU Usage", reader.readLine());
		} catch (IOException e) {
			Log.d("anomalyDetector", "CPU Usage error", e);
		}
	}
}
