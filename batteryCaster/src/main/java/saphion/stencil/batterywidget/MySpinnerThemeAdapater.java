package saphion.stencil.batterywidget;

import saphion.batterycaster.R;
import saphion.logger.Log;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MySpinnerThemeAdapater extends ArrayAdapter<String> implements
		SpinnerAdapter {

	private Context mContext;
	private String SideBarItems[];
	private Activity myAct;
	private int ids[];

	public MySpinnerThemeAdapater(Activity act, String SideBarItems[], int[] ids) {
		super(act.getBaseContext(), R.layout.notithemespinnerlist, SideBarItems);
		mContext = act.getBaseContext();
		myAct = act;
		this.SideBarItems = SideBarItems.clone();
		this.ids = ids.clone();
	}

	@Override
	public int getCount() {
		return SideBarItems.length;
	}

	@Override
	public String getItem(int position) {
		return SideBarItems[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		View sidebar;

		if (convertView == null) {
			sidebar = new View(mContext);
			LayoutInflater inflater = myAct.getLayoutInflater();

			sidebar = inflater.inflate(R.layout.notithemespinnerlist, parent,
					false);

		} else {
			sidebar = (View) convertView;
		}
		try {
			((TextView) sidebar.findViewById(R.id.tvSpinnertheme))
					.setText(SideBarItems[position]);
			try {
				if (ids != null)
					((TextView) sidebar.findViewById(R.id.tvSpinnertheme))
							.setCompoundDrawables(
									textviewDrawable(mContext, ids[position]),
									null, null, null);
			} catch (Exception ex) {
				Log.d(ex.toString());
			}
		} catch (Exception ex) {
			Log.d(ex.toString());
		}

		return sidebar;
	}

	public Drawable textviewDrawable(Context mContext, int id) {

		Bitmap outBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), id);

		Drawable d = new BitmapDrawable(mContext.getResources(), outBitmap);
		d.setBounds(new Rect(0, 0, outBitmap.getWidth(), outBitmap.getHeight()));
		return d;
	}
}