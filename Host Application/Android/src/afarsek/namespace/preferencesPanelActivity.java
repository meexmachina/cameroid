package afarsek.namespace;

import ptp.DeviceInfo;
import widget.CameraControlData.controlType;
import android.app.Activity;
import android.content.Intent;
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
	int[] mAvailableProperties = null;
	boolean[] mActiveProperties = null;
	int mAvailablePropertyCount = 0;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.preferences_panel);
		mLayout = (LinearLayout) findViewById(R.id.preferences_panel_layout);
		mListView = (ListView) mLayout.findViewById(R.id.property_list_view);

		Intent received = getIntent();
		Bundle recBundle = received.getExtras();

		mAvailableProperties = recBundle.getIntArray("AvailableProperties");
		mActiveProperties = recBundle.getBooleanArray("ActiveProperties");
		mAvailablePropertyCount = recBundle.getInt("AvailablePropertiesCount");
		String[] mItemStrings = new String[mAvailablePropertyCount];

		mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mItemStrings);

		// Set option as Multiple Choice. So that user can able to select more the one option from list
		mListView.setAdapter(mListAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		for (int i = 0; i < mAvailablePropertyCount; i++)
		{
			controlType type = controlType.getTypeFromCode(mAvailableProperties[i]);
			mItemStrings[i] = type.toString();
			mListView.setItemChecked(i, mActiveProperties[i]);
		}

		// When item is tapped, toggle checked properties of CheckBox
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View item, int position, long id)
			{
				mActiveProperties[position] = mListView.isItemChecked(position);
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		Intent mainPanelIntent = new Intent(this, mainPanelActivity.class);
		
		mainPanelIntent.putExtra("AvailableProperties", mAvailableProperties);
		mainPanelIntent.putExtra("ActiveProperties", mActiveProperties);
		mainPanelIntent.putExtra("AvailablePropertiesCount", mAvailablePropertyCount);

		setResult(RESULT_OK, mainPanelIntent);
		super.onBackPressed();
	}
}
