package anomalyDetector.activities;

import org.achartengine.GraphicalView;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import anomalyDetector.featureExtractor.R;
import anomalyDetector.graph.AnonPagesGraph;
import anomalyDetector.services.ColectFeaturesService;

public class RealTimeDataActivity extends Activity{

	private GraphicalView mChart;
	private AnonPagesGraph anonPagesGraph;
	private final String TAG = "SHOW_GRAPHS_ACTIVITY";
	
	static final int DATA_RECIVED = 1;
	
	private LinearLayout llayout;
	
	/*
	 * Handle incoming messages from a service
	 */
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case ColectFeaturesService.MSG_DATA_RECEIVED:
				mChart.repaint();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	// Messenger to receive data from service
	final Messenger fromServiceMessenger = new Messenger(new IncomingHandler());
	
	// Messenger to send data to service
	Messenger toServiceMessenger = null;
	
	// Is activity bound to the service
	boolean mIsBound = false;
	
	private ServiceConnection toServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			toServiceMessenger = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			toServiceMessenger = new Messenger(service);
			
			try {
				//Register this activity with the data collection service
				Message msg = Message.obtain(null, ColectFeaturesService.MSG_REGISTER_CLIENT);
				msg.replyTo = fromServiceMessenger;
				toServiceMessenger.send(msg);	
			} catch (RemoteException e) {
				Log.d("ACTIVITY-SERVICE", "Error communicating to service from activity");
			}
		}
	};
	
	void doBindService(){
		//Bind this activity to data collection service
		bindService(new Intent(this, ColectFeaturesService.class), toServiceConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}
	
	void doUnbindService(){
		if(mIsBound){
			if(toServiceMessenger != null){
				try {
					Message msg = Message.obtain(null, ColectFeaturesService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = fromServiceMessenger;
					toServiceMessenger.send(msg);
				} catch (RemoteException e) {
					Log.d("ACTIVITY-SERVICE", "Error while unbounding from service");
				}
			}
			
			unbindService(toServiceConnection);
			mIsBound = false;
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_show_anon_pages);
		// Show the Up button in the action bar.
		setupActionBar();
		anonPagesGraph = ColectFeaturesService.getLineGraph();	
		llayout = (LinearLayout) findViewById(R.id.charAnonPages);
		
		mChart = anonPagesGraph.getChart(this);	
		llayout.addView(mChart);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		doBindService();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doUnbindService();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_anon_pages, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
