package anomalyDetector.utils;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import anomalyDetector.featureExtractor.R;

public class GraphChartAdapter extends BaseAdapter{

	//List of chart items
	private final List<GraphicalView> charts = new ArrayList<GraphicalView>();
	
	//Application context
	private Context mContext;
	
	public GraphChartAdapter(Context context) {
		this.mContext = context;
	}
	
	/**
	 * Add new chart item to the adapter and
	 * notify the observers that the dataset has
	 * changed.
	 * @param graphChart Instance of chart object
	 * that will be added to the dataset.
	 */
	public void add(GraphicalView graphChart){
		charts.add(graphChart);
		notifyDataSetChanged();
	}
	
	/**
	 * Clear the adapter of all the items
	 */
	public void clear(){
		charts.clear();
		notifyDataSetChanged();
	}
	
	/**
	 * Return the number of items in the adapter
	 */
	@Override
	public int getCount() {
		return charts.size();
	}

	/**
	 * Return a specific item from the adapter
	 */
	@Override
	public Object getItem(int position) {
		return charts.get(position);
	}

	/**
	 * Get the item ID of a chart object
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	
	/**
	 * Create a View object to display a certain
	 * chart item at the specified position in 
	 * charts list.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//Get the current chart
		final GraphicalView chart = (GraphicalView) getItem(position);
		
		//Inflate the view for this chart from
		//graph_element.xml
		LinearLayout itemLayout = (LinearLayout) ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.graph_element, null);
		
		//Add the chart to the layout
		final LinearLayout chartRow = (LinearLayout) itemLayout.findViewById(R.id.chartRow);
		chartRow.addView(chart);
		
		return itemLayout;
	}

}
