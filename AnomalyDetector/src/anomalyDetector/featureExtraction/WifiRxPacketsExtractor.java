package anomalyDetector.featureExtraction;

import android.net.TrafficStats;
import android.util.Log;

public class WifiRxPacketsExtractor extends FeatureExtractor{

	private long startRxPackets;
	
	public WifiRxPacketsExtractor(){
		this.startRxPackets = TrafficStats.getTotalRxPackets();
		this.name = "WifiRxPackets";
		if(this.startRxPackets == TrafficStats.UNSUPPORTED){
			Log.d("TXBYTES_ERROR", "The device doesn's support traffic monitoring");
		}
	}
	
	@Override
	public int extract() {
		long currentRxPackets = TrafficStats.getTotalRxPackets();
		long result = currentRxPackets - this.startRxPackets;
		this.startRxPackets = currentRxPackets;
		return (int) result;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
