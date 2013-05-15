package anomalyDetector.factories;

import android.content.Context;
import anomalyDetector.featureExtraction.AnonPagesExtractor;
import anomalyDetector.featureExtraction.BatteryTemperatureExtractor;
import anomalyDetector.featureExtraction.FeatureExtractor;
import anomalyDetector.featureExtraction.MappedPagesExtractor;
import anomalyDetector.featureExtraction.RunningProcessesExtractor;
import anomalyDetector.featureExtraction.TotalEntitiesExtractor;

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
		
		return featureExtractor;
	}
}
