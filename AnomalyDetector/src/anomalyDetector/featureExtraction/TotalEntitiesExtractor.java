package anomalyDetector.featureExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;

public class TotalEntitiesExtractor extends FeatureExtractor{

	private int totalEntities;
	private File loadAvgFile = new File("/proc/loadavg");
	
	public TotalEntitiesExtractor(){
		this.name = "Total Entities";
	}
	
	public int extract() {
		
		BufferedReader reader;
		String line;
		
		try {
			reader = new BufferedReader(new FileReader(loadAvgFile));
			line = reader.readLine().split("\\s+")[3];
			totalEntities = Integer.parseInt(line.split("/")[1]);
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("/proc/loadavg: Error while reading file.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("/proc/loadavg: Error while reading line");
			System.exit(1);
		}
		
		return totalEntities;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
