package anomalyDetector.activities;

import org.achartengine.GraphicalView;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import anomalyDetector.featureExtractor.R;
import anomalyDetector.graph.ActivePagesGraph;
import anomalyDetector.graph.AnonPagesGraph;
import anomalyDetector.graph.BatteryTemperatureGraph;
import anomalyDetector.graph.BatteryVoltageGraph;
import anomalyDetector.graph.MappedPagesGraph;
import anomalyDetector.graph.ReceivedPacketsGraph;
import anomalyDetector.graph.RunningProcGraph;
import anomalyDetector.graph.TotalEntitiesGraph;
import anomalyDetector.graph.TransmittedBytesGraph;
import anomalyDetector.graph.TransmittedPacketsGraph;
import anomalyDetector.services.ColectFeaturesService;
import anomalyDetector.utils.GraphChartAdapter;

public class RealTimeDataActivity extends Activity{

	private GraphicalView anonPagesChart;
	private GraphicalView batteryTempChart;
	private GraphicalView mappedPagesChart;
	private GraphicalView runningProcChart;
	private GraphicalView totalEntitiesChart;
	private GraphicalView activePagesChart;
	private GraphicalView batteryVoltageChart;
	private GraphicalView transmittedBytesChart;
	private GraphicalView transmittedPacketsChart;
	private GraphicalView receivedPacketsChart;
	
	private AnonPagesGraph anonPagesGraph;
	private BatteryTemperatureGraph batteryTempGraph;
	private MappedPagesGraph mappedPagesGraph;
	private RunningProcGraph runningProcGraph;
	private TotalEntitiesGraph totalEntitiesGraph;
	private ActivePagesGraph activePagesGraph;
	private BatteryVoltageGraph batteryVoltageGraph;
	private TransmittedBytesGraph transmittedBytesGraph;
	private TransmittedPacketsGraph transmittedPacketsGraph;
	private ReceivedPacketsGraph receivedPacketsGraph;
	
	private final String TAG = "SHOW_GRAPHS_ACTIVITY";
	
	static final int DATA_RECIVED = 1;
	
	private LinearLayout anonPagesLayout;
	private LinearLayout batteryTempLayout;
	private LinearLayout mappedPagesLayout;
	private LinearLayout runningProcLayout;
	private LinearLayout totalEntitiesLayout;
	private LinearLayout activePagesLayout;
	private LinearLayout batteryVoltageLayout;
	private LinearLayout transmittedBytesLayout;
	private LinearLayout transmittedPacketsLayout;
	private LinearLayout receivedPacketsLayout;
	
	/*
	 * Handle incoming messages from a service
	 */
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case ColectFeaturesService.MSG_DATA_RECEIVED:
				//Log.i(TAG, "NEW DATA!");
				anonPagesChart.repaint();
				batteryTempChart.repaint();
				mappedPagesChart.repaint();
				runningProcChart.repaint();
				totalEntitiesChart.repaint();
				activePagesChart.repaint();
				batteryVoltageChart.repaint();
				transmittedBytesChart.repaint();
				transmittedPacketsChart.repaint();
				receivedPacketsChart.repaint();
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
		Log.i(TAG, "ON CREATE!");
		anonPagesGraph = ColectFeaturesService.getLineGraph();
		batteryTempGraph = ColectFeaturesService.getBatteryTempGraph();
		mappedPagesGraph = ColectFeaturesService.getMappedPagesGraph();
		runningProcGraph = ColectFeaturesService.getRunningProcGraph();
		totalEntitiesGraph = ColectFeaturesService.getTotalEntitiesGraph();
		activePagesGraph = ColectFeaturesService.getActivePagesGraph();
		batteryVoltageGraph = ColectFeaturesService.getBatteryVoltageGraph();
		transmittedBytesGraph = ColectFeaturesService.getTransmittedBytesGraph();
		transmittedPacketsGraph = ColectFeaturesService.getTransmittedPacketsGraph();
		receivedPacketsGraph = ColectFeaturesService.getReceivedPacketsGraph();
		
		//Layouts for each individual graph object
		anonPagesLayout = (LinearLayout) findViewById(R.id.charAnonPages);
		batteryTempLayout = (LinearLayout) findViewById(R.id.chartBatteryTemp);
		mappedPagesLayout = (LinearLayout) findViewById(R.id.chartMappedPages);
		runningProcLayout = (LinearLayout) findViewById(R.id.chartRunningProc);
		totalEntitiesLayout = (LinearLayout) findViewById(R.id.chartTotalEntities);
		activePagesLayout = (LinearLayout) findViewById(R.id.chartActivePages);
		batteryVoltageLayout = (LinearLayout) findViewById(R.id.chartBatteryVoltage);
		transmittedBytesLayout = (LinearLayout) findViewById(R.id.chartTransmittedBytes);
		transmittedPacketsLayout = (LinearLayout) findViewById(R.id.chartTransmittedPackets);
		receivedPacketsLayout = (LinearLayout) findViewById(R.id.chartReceivedPackets);
		
		//Obtain the chart objects
		anonPagesChart = anonPagesGraph.getChart(this);
		batteryTempChart = batteryTempGraph.getChart(this);
		mappedPagesChart = mappedPagesGraph.getChart(this);
		runningProcChart = runningProcGraph.getChart(this);
		totalEntitiesChart = totalEntitiesGraph.getChart(this);
		activePagesChart = activePagesGraph.getChart(this);
		batteryVoltageChart = batteryVoltageGraph.getChart(this);
		transmittedBytesChart = transmittedBytesGraph.getChart(this);
		transmittedPacketsChart = transmittedPacketsGraph.getChart(this);
		receivedPacketsChart = receivedPacketsGraph.getChart(this);
		
		//Add the chart objects to their layouts
		anonPagesLayout.addView(anonPagesChart);
		batteryTempLayout.addView(batteryTempChart);
		mappedPagesLayout.addView(mappedPagesChart);
		runningProcLayout.addView(runningProcChart);
		totalEntitiesLayout.addView(totalEntitiesChart);
		activePagesLayout.addView(activePagesChart);
		batteryVoltageLayout.addView(batteryVoltageChart);
		transmittedBytesLayout.addView(transmittedBytesChart);
		transmittedPacketsLayout.addView(transmittedPacketsChart);
		receivedPacketsLayout.addView(receivedPacketsChart);
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
		//Log.i(TAG, "ON STOP!");
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
