package anomalyDetector.featureExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.util.jar.Attributes.Name;
//
//import android.app.ActivityManager;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
public class AnonPagesExtractor extends FeatureExtractor{

	private File memoryFile = new File("/proc/meminfo");
	
	
	public AnonPagesExtractor(){
		this.name = "Anonymous Pages";
	}
	
	public int extract() {

		int anonPages = 0;

		BufferedReader reader;
		String line;

		try {
			reader = new BufferedReader(new FileReader(memoryFile));
			while((line = reader.readLine()) != null){
				if(line.contains("AnonPages")) {
					anonPages = Integer.parseInt(line.split("\\s+")[1]);
					reader.close();
					break;
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("/proc/loadavg: Error while reading file.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("/proc/loadavg: Error while reading line");
			System.exit(1);
		}

		return anonPages;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
