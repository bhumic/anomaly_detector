package anomalyDetector.utils;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import anomalyDetector.activities.RealTimeDataActivity;

public class DrawerItemClickListener implements ListView.OnItemClickListener{

	private final String TAG = "DRAWER_ITEM_CLICK_LISTENER";
	private boolean isServiceRunning = false;
	
	public DrawerItemClickListener() {
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		/*
		 * If we selected to view the graphs
		 */
		if(position == 0){
			if(!isServiceRunning){
				Toast toast = Toast.makeText(parent.getContext(), "Service not running", Toast.LENGTH_SHORT);
				toast.show();
			}
			else{
				Intent intent = new Intent(parent.getContext(), RealTimeDataActivity.class);
				parent.getContext().startActivity(intent);
			}
		}
		else{
			//TODO
		}
	}
	
	public void setServiceRunning(boolean isServiceRunning) {
		this.isServiceRunning = isServiceRunning;
	}

}
