package saphion.stencil.batterywidget;

import saphion.batterycaster.R;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class FontSelectorSpinner extends BaseAdapter implements SpinnerAdapter {

	private Context mContext;

	String[] fontNamesS;
	String[] fontretS;
	ActionBarActivity myact;

	public FontSelectorSpinner(ActionBarActivity c, String[] font, String[] fontname) {

		myact = c;
		mContext = c.getBaseContext();
		fontretS = font.clone();
		fontNamesS = fontname.clone();
	}

	public int getCount() {

		return fontretS.length;
	}

	public Object getItem(int arg0) {

		return fontretS[arg0];
	}

	public long getItemId(int arg0) {

		return arg0;
	}

	@SuppressWarnings("finally")
	public View getView(int position, View convertView, ViewGroup parent) {

		View grid;

		if (convertView == null) {
			grid = new View(mContext);
			LayoutInflater inflater = myact.getLayoutInflater();

			grid = inflater.inflate(R.layout.mylist, parent, false);

		} else {
			grid = (View) convertView;
		}
		try {
			((TextView) grid.findViewById(R.id.tvlist))
					.setText(fontretS[position]);
		} catch (ArrayIndexOutOfBoundsException e) {
		} finally {

			/*
			 * if (getPrefs.getString("font", "").equals(fontNamesS[position]))
			 * { ((TextView)
			 * grid.findViewById(R.id.tvlist)).setTextColor(Color.rgb( 51, 181,
			 * 229)); Log.d("selected is:", fontNamesS[position]); } else {
			 * ((TextView) grid.findViewById(R.id.tvlist))
			 * .setTextColor(Color.WHITE); Log.d("unselected is:",
			 * fontNamesS[position]); }
			 */

			try {
				if (position != 0) {
					((TextView) grid.findViewById(R.id.tvlist))
							.setTypeface(Typeface
									.createFromFile(fontNamesS[position]));
					Log.d("font for", position + " " + fontNamesS[position]);
				} else {
					((TextView) grid.findViewById(R.id.tvlist))
							.setTypeface(Typeface.DEFAULT);
				}
			} catch (RuntimeException e) {
				Log.d("Exception: ", e + "");
			} finally {
				return grid;
			}

		}
	}
}
