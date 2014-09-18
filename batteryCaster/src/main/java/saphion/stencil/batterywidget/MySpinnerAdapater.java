package saphion.stencil.batterywidget;

import saphion.batterycaster.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


public class MySpinnerAdapater extends BaseAdapter implements SpinnerAdapter {

	private Context mContext;
	private String SideBarItems[];
	private Activity myAct;

	public MySpinnerAdapater(Context c) {
		mContext = c;
	}

	public MySpinnerAdapater(Activity act, String SideBarItems[]) {
		mContext = act.getBaseContext();
		myAct = act;
		this.SideBarItems = SideBarItems.clone();
	}

	@Override
	public int getCount() {
		return SideBarItems.length;
	}

	@Override
	public Object getItem(int position) {
		return SideBarItems[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View sidebar;

		if (convertView == null) {
			sidebar = new View(mContext);
			LayoutInflater inflater = myAct.getLayoutInflater();

			sidebar = inflater.inflate(R.layout.spinnerlist, parent, false);

		} else {
			sidebar = (View) convertView;
		}
		try {
			((TextView) sidebar.findViewById(R.id.tvSpinner))
					.setText(SideBarItems[position]);
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		return sidebar;
	}

}