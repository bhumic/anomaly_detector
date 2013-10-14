package anomalyDetector.featureExtraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class CPUUsageExtractor extends FeatureExtractor{

	public CPUUsageExtractor(){
		this.name = "CPU Usage";
	}
	
	@Override
	public int extract() {
		
		int usage = 0;
		BufferedReader reader = null;
		Process proc = null;
		try {
			proc = new ProcessBuilder().command("top").redirectErrorStream(true).start();
			InputStream in = proc.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			Log.d("CPU Usage", reader.readLine());
		} catch (IOException e) {
			Log.d("anomalyDetector", "CPU Usage error", e);
			proc.destroy();
			System.exit(1);
		} finally {
			proc.destroy();
		}
		return usage;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
