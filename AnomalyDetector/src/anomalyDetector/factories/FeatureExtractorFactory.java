package anomalyDetector.factories;

import android.content.Context;
import anomalyDetector.featureExtraction.ActivePagesExtractor;
import anomalyDetector.featureExtraction.AnonPagesExtractor;
import anomalyDetector.featureExtraction.BatteryTemperatureExtractor;
import anomalyDetector.featureExtraction.BatteryVoltageExtractor;
import anomalyDetector.featureExtraction.CPUUsageExtractor;
import anomalyDetector.featureExtraction.CameraUsageExtractor;
import anomalyDetector.featureExtraction.FeatureExtractor;
import anomalyDetector.featureExtraction.MappedPagesExtractor;
import anomalyDetector.featureExtraction.RunningProcessesExtractor;
import anomalyDetector.featureExtraction.TotalEntitiesExtractor;
import anomalyDetector.featureExtraction.WifiRxPacketsExtractor;
import anomalyDetector.featureExtraction.WifiTxBytesExtractor;
import anomalyDetector.featureExtraction.WifiTxPacketsExtractor;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
public class FeatureExtractorFactory {

	public FeatureExtractor createFeatureExtractor(String feature, Context context){
		
		FeatureExtractor featureExtractor = null;
		
		if(feature.equals("Anonymous Pages")){
			featureExtractor = new AnonPagesExtractor();
		}
		else if(feature.equals("Battery Temperature")){
			featureExtractor = new BatteryTemperatureExtractor(context);
		}
		else if(feature.equals("Mapped Pages")){
			featureExtractor = new MappedPagesExtractor();
		}
		else if(feature.equals("Running Processes")){
			featureExtractor = new RunningProcessesExtractor(context);
		}
		else if(feature.equals("Total Entities")){
			featureExtractor = new TotalEntitiesExtractor();
		}
		else if(feature.equals("CPU Usage")){
			featureExtractor = new CPUUsageExtractor();
		}
		else if(feature.equals("Active Pages")){
			featureExtractor = new ActivePagesExtractor();
		}
		else if(feature.equals("Battery Voltage")){
			featureExtractor = new BatteryVoltageExtractor(context);
		}
		else if(feature.equals("Camera Usage")){
			featureExtractor = new CameraUsageExtractor();
		}
		else if(feature.equals("WifiTxBytes")){
			featureExtractor = new WifiTxBytesExtractor();
		}
		else if(feature.equals("WifiTxPackets")){
			featureExtractor = new WifiTxPacketsExtractor();
		}
		else if(feature.equals("WifiRxPackets")){
			featureExtractor = new WifiRxPacketsExtractor();
		}
		
		return featureExtractor;
	}
}
