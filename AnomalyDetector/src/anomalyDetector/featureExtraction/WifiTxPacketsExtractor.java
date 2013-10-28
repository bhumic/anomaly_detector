package anomalyDetector.featureExtraction;

import android.net.TrafficStats;
import android.util.Log;

public class WifiTxPacketsExtractor extends FeatureExtractor{

	private long startTxPackets;
	
	public WifiTxPacketsExtractor(){
		this.startTxPackets = TrafficStats.getTotalTxPackets();
		this.name = "WifiTxPackets";
		if(this.startTxPackets == TrafficStats.UNSUPPORTED){
			Log.d("TXBYTES_ERROR", "The device doesn's support traffic monitoring");
		}
	}
	
	@Override
	public int extract() {
		long currentTxPackets = TrafficStats.getTotalTxPackets();
		long result = currentTxPackets - this.startTxPackets;
		this.startTxPackets = currentTxPackets;
		//Log.d("START_T", String.valueOf(this.startTxBytes));
		//Log.d("STOP_T", String.valueOf(currentTxBytes));
		return (int) result;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
