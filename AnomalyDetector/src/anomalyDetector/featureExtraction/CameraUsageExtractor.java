package anomalyDetector.featureExtraction;

import android.hardware.Camera;

public class CameraUsageExtractor extends FeatureExtractor{

	public CameraUsageExtractor() {
		this.name = "Camera Usage";
	}
	@Override
	public int extract() {
		Camera camera = null;
		try{
			camera = Camera.open();
		}catch(RuntimeException e){
			return 1; //aka True
		}finally{
			if(camera != null) camera.release();
		}
		return 0;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
