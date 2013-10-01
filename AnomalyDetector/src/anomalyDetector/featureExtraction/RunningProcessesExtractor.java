package anomalyDetector.featureExtraction;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
public class RunningProcessesExtractor extends FeatureExtractor{

	
	private int runningProcesses;
	private ActivityManager manager;
	
	public RunningProcessesExtractor(Context context){
		
		this.name = "Running Processes";
		manager = (ActivityManager) context.getSystemService(android.content.Context.ACTIVITY_SERVICE);
	}
	
	public int extract(){
		
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = manager.getRunningAppProcesses();
		
		runningProcesses = runningAppProcessesList.size();
		
		return runningProcesses;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
