package afarsek.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class preferencesPanelActivity extends Activity
{
	private ListView mListView;
	private ArrayAdapter<String> mListAdapter;
	private LinearLayout mLayout;
	private String mItemStrings[] =
	{ "Battery Level", "Image Size", "Compression Setting", "White Balance", "F-Stop", "Focal Length", "Focus Distance", "Focus Mode",
			"Exposure Metering Mode", "Flash Mode", "Exposure Time", "Exposure Program Mode", "Exposure Index",
			"Exposure Bias Compensation", "Date Time", "Capture Delay", "Still Capture Mode", "Contrast", "Sharpness", "Digital Zoom",
			"Effect Mode", "Burst Number", "Burst Interval", "Timelapse Number", "Timelapse Interval", "Focus Metering Mode", "Upload URL",
			"Artist", "Copyright Info" };

	private int mItemNumbers[] =
	{ 0x5001, 0x5003, 0x5004, 0x5005, 0x5007, 0x5008, 0x5009, 0x500a, 0x500b, 0x500c, 0x500d, 0x500e, 0x500f, 0x5010, 0x5011, 0x5012,
			0x5013, 0x5014, 0x5015, 0x5016, 0x5017, 0x5018, 0x5019, 0x501a, 0x501b, 0x501c, 0x501d, 0x501d, 0x501e, 0x501f };

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.preferences_panel);
		mLayout = (LinearLayout) findViewById(R.id.preferences_panel_layout);

		mListView = (ListView) mLayout.findViewById(R.id.property_list_view);

		mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mItemStrings);

		// Set option as Multiple Choice. So that user can able to select more the one option from list
		mListView.setAdapter(mListAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// When item is tapped, toggle checked properties of CheckBox and Planet.
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			/*
			 * Planet planet = listAdapter.getItem(position); planet.toggleChecked(); PlanetViewHolder viewHolder = (PlanetViewHolder)
			 * item.getTag(); viewHolder.getCheckBox().setChecked(planet.isChecked());
			 */

			public void onItemClick(AdapterView<?> parent, View item, int position, long id)
			{
				mListAdapter.getItem(position);
			}
		});
	}

}
