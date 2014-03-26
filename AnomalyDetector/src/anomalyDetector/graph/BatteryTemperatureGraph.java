package anomalyDetector.graph;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class BatteryTemperatureGraph {

	//Object for storing (x,y) pair values
		private XYSeries series;
		
		//Object that can hold multiple XYSeries objects and plot them
		private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		
		//Renderer object that efects all the XYSeries in the dataset
		private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		
		//Renderer object that effects a single XYSeries
		private XYSeriesRenderer renderer;
		
		//A View object that is used by the activity to show the graph to the user
		private GraphicalView mChart;
		
		public BatteryTemperatureGraph() {
			series = new XYSeries("Battery Temperature");
			renderer = new XYSeriesRenderer();
			renderer.setColor(Color.GREEN);
			renderer.setLineWidth(2);
			renderer.setFillPoints(true);
			dataset.addSeries(series);
			
			mRenderer.addSeriesRenderer(renderer);
			mRenderer.setBackgroundColor(Color.BLACK);
			mRenderer.setYTitle("Battery Temperature");
			mRenderer.setApplyBackgroundColor(true);
			mRenderer.setAxisTitleTextSize(23.0f);
			mRenderer.setChartTitleTextSize(23.0f);
			mRenderer.setLabelsTextSize(20.0f);
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
			double value = Double.parseDouble(lineData.split("\\s+")[1])/10;
			series.add(second, value);
			
			int maxValue = second;
			int minValue = (second - 60)>0 ? (second - 60) : 2;
			mRenderer.setXAxisMax(maxValue);
			mRenderer.setXAxisMin(minValue);
			mRenderer.setYAxisMax(value + 30);
			mRenderer.setYAxisMin(value - 30);
		}
		
		public XYMultipleSeriesRenderer getmRenderer() {
			return mRenderer;
		}
}
