package anomalyDetector.activities;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.R.layout;
import android.os.Bundle;
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

public class ShowAnonPagesActivity extends Activity {

	private XYSeries series;
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	private XYSeriesRenderer mRenderer;
	private GraphicalView mChart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_anon_pages);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		String data = intent.getStringExtra(MainFeaturesActivity.COLLECTED_DATA);
		String[] dataPerTick = data.split("\n");
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		
		series = new XYSeries("Anonymous pages(MB)");
		dataset.addSeries(series);
		mRenderer = new XYSeriesRenderer();
		mRenderer.setColor(Color.YELLOW);
		//mRenderer.setPointStyle(PointStyle.CIRCLE);
		mRenderer.setFillPoints(true);
		
		renderer.addSeriesRenderer(mRenderer);
		renderer.setBackgroundColor(Color.BLACK);
		//renderer.setChartTitle("Anonymous pages data");
		renderer.setYTitle("Megabytes");
		renderer.setApplyBackgroundColor(true);
		renderer.setAxisTitleTextSize(23.0f);
		renderer.setChartTitleTextSize(23.0f);
		renderer.setLabelsTextSize(20.0f);
		
		int dataEntries = dataPerTick.length;
		for(int i = 0; i < dataEntries; ++i){
			series.add(i, Double.parseDouble(dataPerTick[i].split("\\s+")[0])/1000);
		}
		
		mChart = ChartFactory.getCubeLineChartView(this, dataset, renderer, 0.3f);
		layout.addView(mChart);
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
