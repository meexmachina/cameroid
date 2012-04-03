package widget;

import java.util.ArrayList;

import widget.CameraControlData.controlType;

import afarsek.namespace.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraControlAdapter extends BaseAdapter
{
	/**************************************************************************************************
	 * Private class properties
	 */
	private Context mContext;
	private ArrayList<CameraControlData> mControledList;

	/**************************************************************************************************
	 * class methods
	 */
	public CameraControlAdapter(Context context)
	{
		mContext = context;
		mControledList = new ArrayList<CameraControlData>();
	}
	
	public void setCameraControlDataList(ArrayList<CameraControlData> list)
	{
		mControledList = list;
	}

	public controlType getItemType(int position)
	{
		return mControledList.get(position).getType();
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null)
		{
			gridView = new View(mContext);
			gridView = inflater.inflate(R.layout.camera_control_item, null);
		} else
		{
			gridView = (View) convertView;
		}
		
		ImageView mSymbolView = (ImageView) gridView.findViewById(R.id.control_symbol);
		mSymbolView.setImageDrawable(mControledList.get(position).getIcon());

		RelativeLayout secLayout = (RelativeLayout) gridView.findViewById(R.id.value_layout);
		TextView textView = (TextView) secLayout.findViewById(R.id.control_value);
		textView.setText(mControledList.get(position).getText());
		
		ImageView mValueBottomView = (ImageView) secLayout.findViewById(R.id.control_value_background);
		mValueBottomView.setImageDrawable (mControledList.get(position).getValueBackground());

		return gridView;
	}

	public void add(CameraControlData view)
	{
		mControledList.add(view);
		notifyDataSetChanged();
	}

	public int getCount()
	{
		/* Set the number of element we want on the grid */
		return mControledList.size();
	}

	public void clear()
	{
		mControledList.clear();
		notifyDataSetChanged();
	}

	public Object getItem(int pos)
	{
		if (pos >= 0 && pos < mControledList.size())
		{
			return mControledList.get(pos);
		}

		return null;
	}

	public long getItemId(int pos)
	{
		return pos;
	}
}