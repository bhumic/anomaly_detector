package anomalyDetector.featureExtraction;

//import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
//import android.util.Log;

/**
 * 
 * @author Bruno Humic
 * @License GPLv3
 */
public class BatteryTemperatureExtractor extends FeatureExtractor {
	
	int temperature;

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context arg0, Intent intent) {

	      temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
	      //Log.d("temperatura", String.valueOf(temperature));
	    }
	  };
	  
	  
	 public BatteryTemperatureExtractor(Context context){
		 
		 this.name = "Battery Temperature";
		 context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	 }


	@Override
	public int extract() {
		
		return temperature;
	}


	@Override
	public String getName() {
		return this.name;
	}
}
