package afarsek.namespace;

import ptp.DeviceInfo;
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

	public class propertyList
	{
		public String mItemString;
		public int mCode;
		public boolean mAvailable;
		public boolean mChosen;

		public propertyList(String name, int code, boolean avail, boolean chosen)
		{
			mItemString = name;
			mCode = code;
			mAvailable = avail;
			mChosen = chosen;
			if (avail == false)
				mChosen = false;
		}
	}

	private propertyList[] mProperties =
	{ new propertyList("Battery Level", 0x5001, false, false), new propertyList("White Balance", 0x5005, false, false),
			new propertyList("Aperture", 0x5007, false, false), new propertyList("Focal Length", 0x5008, false, false),
			new propertyList("Focus Distance", 0x5009, false, false), new propertyList("Focus Mode", 0x500a, false, false),
			new propertyList("Flash Mode", 0x500c, false, false), new propertyList("Shutter Speed", 0x500d, false, false) };

	private int mNumOfAvailableProperties = 0;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.preferences_panel);
		mLayout = (LinearLayout) findViewById(R.id.preferences_panel_layout);
		mListView = (ListView) mLayout.findViewById(R.id.property_list_view);

		// Create the items list
		String[] mItemStrings = new String[mNumOfAvailableProperties];
		int count = 0;

		for (int i = 0; i < mProperties.length; i++)
		{
			if (mProperties[i].mAvailable == true)
			{
				mItemStrings[count] = mProperties[i].mItemString;
				count++;
			}
		}

		mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mItemStrings);

		// Set option as Multiple Choice. So that user can able to select more the one option from list
		mListView.setAdapter(mListAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		int[] currentCodes = icicle.getIntArray("CurrentChosen");
		for (int i=0; i<mProperties.length; i++)
		{
			mProperties[i].mChosen = false;
			for (int j=0; j<currentCodes.length; j++)
			{
				if (currentCodes[j]==mProperties[i].mCode)
				{
					mProperties[i].mChosen=true;
					break;
				}
			}
			
			if (mProperties[i].mChosen==true)
			{
				int k;
				boolean found = false;
				for (k=0; k<mListAdapter.getCount(); k++)
				{
					if (mListAdapter.getItem(i)==mProperties[i].mItemString)
					{
						found = true;
						break;
					}
				}
				
				if (found == true)
					mListView.setItemChecked(k, true);
			}
		}

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

	@Override
	public void onBackPressed()
	{
		int count = 0;
		for (int i = 0; i < mProperties.length; i++)
		{
			if (mProperties[i].mChosen == true)
				count++;
		}

		int[] chosen = new int[count];
		count = 0;
		for (int i = 0; i < mProperties.length; i++)
		{
			if (mProperties[i].mChosen == true)
			{
				chosen[count] = mProperties[i].mCode;
				count++;
			}
		}

		Intent myIntent = new Intent(this, mainPanelActivity.class);
		myIntent.putExtra("ChosenProperties", chosen);
		setResult(RESULT_OK, myIntent);
		super.onBackPressed();
	}

	public void setDeviceInfo(DeviceInfo info)
	{
		int[] properties = info.propertiesSupported;
		mNumOfAvailableProperties = 0;

		for (int i = 0; i < mProperties.length; i++)
		{
			int curProp = mProperties[i].mCode;
			mProperties[i].mAvailable = false;

			for (int j = 0; j < properties.length; j++)
			{
				if (properties[j] == curProp)
				{
					mProperties[i].mAvailable = true;
					mNumOfAvailableProperties++;
				}
			}
		}
	}

}
