package anomalyDetector.activities;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.R.layout;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings.TextSize;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build;
import anomalyDetector.featureExtractor.R;
import anomalyDetector.graph.AnonPagesGraph;
import anomalyDetector.services.ColectFeaturesService;

public class ShowAnonPagesActivity extends Activity {

	private TimeSeries series;
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	private XYSeriesRenderer mRenderer;
	private GraphicalView mChart;
	private AnonPagesGraph anonPagesGraph;
	private Timer updateGraph;
	
	static final int DATA_RECIVED = 1;
	
	class IncomingHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case DATA_RECIVED:
				Log.d("PODACI", "stigllo");
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	final Messenger messenger = new Messenger(new IncomingHandler());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_anon_pages);
		// Show the Up button in the action bar.
		setupActionBar();
		anonPagesGraph = ColectFeaturesService.getLineGraph();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//Log.d("START", "u destroy je");
		mChart = anonPagesGraph.getChart(this);		
		LinearLayout layout = (LinearLayout) findViewById(R.id.charAnonPages);
		layout.addView(mChart);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Log.d("DESTROY", "u destroy je");
		updateGraph.cancel();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateGraph = new Timer();
		updateGraph.schedule(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mChart.repaint();
					}
				});
			}
		}, 0, 2000);
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
