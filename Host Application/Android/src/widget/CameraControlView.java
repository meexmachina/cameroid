package widget;

import afarsek.namespace.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class CameraControlView extends LinearLayout
{

	public CameraControlView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.actionbar_item, this);
	}

}
