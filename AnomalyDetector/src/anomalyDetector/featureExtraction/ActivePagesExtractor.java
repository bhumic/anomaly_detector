package anomalyDetector.featureExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ActivePagesExtractor extends FeatureExtractor{

	private File memoryFile = new File("/proc/meminfo");
	
	
	public ActivePagesExtractor(){
		this.name = "Active Pages";
	}
	
	@Override
	public int extract() {
		
		int activePages = 0;

		BufferedReader reader;
		String line;

		try {
			reader = new BufferedReader(new FileReader(memoryFile));
			while((line = reader.readLine()) != null){
				if(line.contains("Active:")) {
					activePages = Integer.parseInt(line.split("\\s+")[1]);
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

		return activePages;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
