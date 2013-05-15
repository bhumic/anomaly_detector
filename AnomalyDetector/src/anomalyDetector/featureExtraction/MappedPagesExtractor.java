package anomalyDetector.featureExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;

public class MappedPagesExtractor extends FeatureExtractor{

	private File memoryFile = new File("/proc/meminfo");
	
	
	public MappedPagesExtractor(){
		this.name = "Mapped Pages";
	}
	
	public int extract() {

		int mappedPages = 0;

		BufferedReader reader;
		String line;

		try {
			reader = new BufferedReader(new FileReader(memoryFile));
			while((line = reader.readLine()) != null){
				if(line.contains("Mapped")) {
					mappedPages = Integer.parseInt(line.split("\\s+")[1]);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("/proc/loadavg: Error while reading file.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("/proc/loadavg: Error while reading line");
			System.exit(1);
		}

		return mappedPages;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
