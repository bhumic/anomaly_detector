package anomalyDetector.featureExtraction;

import android.app.ActivityManager;

public abstract class FeatureExtractor {

	protected String name;
	
	public abstract int extract();
	public abstract String getName();
	//public abstract int extract(ActivityManager manager);
}
