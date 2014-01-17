package anomalyDetector.graph;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class AnonPagesGraph {

	//private TimeSeries series;
	private XYSeries series;
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeriesRenderer renderer;
	private GraphicalView mChart;
	
	public AnonPagesGraph(String dataDescription) {
		series = new XYSeries(dataDescription);
		renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		renderer.setLineWidth(2);
		renderer.setFillPoints(true);
		//renderer.setFillBelowLine(true);
		dataset.addSeries(series);
		
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setYTitle("AnonymousPages(MB)");
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setAxisTitleTextSize(23.0f);
		mRenderer.setChartTitleTextSize(23.0f);
		mRenderer.setLabelsTextSize(20.0f);
		//mRenderer.setXLabels(0);
		mRenderer.setMargins(new int[] {5,50,5,50});
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setPanEnabled(false, false);
		//mRenderer.setYAxisAlign(Align.RIGHT, 0);
	}
	
	public GraphicalView getChart(Context context){
		mChart = ChartFactory.getCubeLineChartView(context, dataset, mRenderer, 0.3f);
		return mChart;
	}
	
	public void addNewPoint(String lineData, int second){
		double value = Double.parseDouble(lineData.split("\\s+")[0])/1000;
		series.add(second, value);
		
		int maxValue = second;
		int minValue = (second - 60)>0 ? (second - 60) : 0;
		mRenderer.setXAxisMax(maxValue);
		mRenderer.setXAxisMin(minValue);
		mRenderer.setYAxisMax(value + 200);
		mRenderer.setYAxisMin(value - 200);
	}
	
	public XYMultipleSeriesRenderer getmRenderer() {
		return mRenderer;
	}
}