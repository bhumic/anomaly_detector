package anomalyDetector.featureExtraction;

import android.app.AlertDialog;
import android.net.TrafficStats;
import android.util.Log;

public class WifiTxBytesExtractor extends FeatureExtractor{

	private long startTxBytes;
	
	public WifiTxBytesExtractor(){
		this.name = "WifiTxBytes";
		this.startTxBytes = TrafficStats.getTotalTxBytes();
		if(this.startTxBytes == TrafficStats.UNSUPPORTED){
			Log.d("TXBYTES_ERROR", "The device doesn's support traffic monitoring");
		}
	}
	
	@Override
	public int extract() {
		long currentTxBytes = TrafficStats.getTotalTxBytes();
		long result = currentTxBytes - this.startTxBytes;
		this.startTxBytes = currentTxBytes;
		//Log.d("START_T", String.valueOf(this.startTxBytes));
		//Log.d("STOP_T", String.valueOf(currentTxBytes));
		return (int) result;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
