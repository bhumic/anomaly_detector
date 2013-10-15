package anomalyDetector.featureExtraction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryVoltageExtractor extends FeatureExtractor{

	
	int voltage;

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context arg0, Intent intent) {

	      voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
	      //Log.d("temperatura", String.valueOf(temperature));
	    }
	  };
	  
	
	public BatteryVoltageExtractor(Context context){
		this.name = "Battery Voltage";
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	@Override
	public int extract() {
		return voltage;
	}

	@Override
	public String getName() {
		return this.name;
	}

	
}
