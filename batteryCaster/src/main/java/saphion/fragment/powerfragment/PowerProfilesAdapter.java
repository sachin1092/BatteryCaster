package saphion.fragment.powerfragment;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.ArrayList;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.logger.Log;
import saphion.utils.PreferenceHelper;
import saphion.utils.ToggleHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoListener;

public class PowerProfilesAdapter extends BaseAdapter {

	private Context mContext;
	ArrayList<PowerProfItems> items;// = new ArrayList<String>();;
	private ActionBarActivity myAct;

	public PowerProfilesAdapter(Context c) {
		mContext = c;
	}

	public PowerProfilesAdapter(ActionBarActivity sherlockFragmentActivity,
			ArrayList<PowerProfItems> items) {
		mContext = sherlockFragmentActivity.getBaseContext();
		myAct = sherlockFragmentActivity;
		this.items = items;

		/*
		 * pos = new boolean[items.size()]; for (int i = 0; i < items.size();
		 * i++) { pos[i] = false; }
		 */

		// al = new ArrayList<Integer>();
		/*
		 * mMessageBar = new MessageBar(sherlockFragmentActivity);
		 * mMessageBar.clear(); mMessageBar.hide();
		 */

	}

	public int getCount() {

		return items.size();
	}

	public ArrayList<PowerProfItems> getAll() {
		return items;
	}

	public PowerProfItems getItem(int arg0) {

		return items.get(arg0);
	}

	public long getItemId(int arg0) {

		return arg0;
	}

	ViewHolder holder;

	// ArrayList<Integer> mylist;

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = new View(mContext);
			LayoutInflater inflater = myAct.getLayoutInflater();

			convertView = inflater.inflate(R.layout.poweritems, parent, false);

			holder = new ViewHolder();
			holder.amode = (ImageView) convertView.findViewById(R.id.ivamode);
			holder.bness = (ImageView) convertView.findViewById(R.id.ivbness);
			holder.btooth = (ImageView) convertView.findViewById(R.id.ivbtooth);
			holder.data = (ImageView) convertView.findViewById(R.id.ivdata);
			holder.sync = (ImageView) convertView.findViewById(R.id.ivsync);
			holder.wifi = (ImageView) convertView.findViewById(R.id.ivwifi);
			holder.selected = (RadioButton) convertView
					.findViewById(R.id.rbselectProf);
			holder.name = (TextView) convertView.findViewById(R.id.tvprofname);
			holder.positions = position;
			holder.click = (LinearLayout) convertView
					.findViewById(R.id.llcontainer);
			holder.back = (RelativeLayout) convertView
					.findViewById(R.id.rlpowerback);
			holder.delete = convertView.findViewById(R.id.delete);
			holder.footerFiller = convertView
					.findViewById(R.id.alarm_footer_filler);
			holder.footerFiller.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Do nothing.
				}
			});
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.positions = position;

		animate(holder.delete).alpha(0.5f).setDuration(0);

		setimg(R.drawable.wifi_off, R.drawable.wifi_on, holder.wifi,
				items.get(position).getWifi());
		setimg(R.drawable.mdata_off, R.drawable.mdata_on, holder.data, items
				.get(position).getData());
		setimg(R.drawable.sync_off, R.drawable.sync_on, holder.sync,
				items.get(position).getSync());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			holder.amode.setVisibility(View.GONE);
		setimg(R.drawable.amode_off, R.drawable.amode_on, holder.amode, items
				.get(position).getAmode());
		setimg(R.drawable.btooth_off, R.drawable.btooth_on, holder.btooth,
				items.get(position).getBluetooth());
		holder.name.setText(items.get(position).getProfileName());
		holder.name.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
				"cnlbold.ttf"));
		holder.click.setTag(holder);
		holder.click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder vh = (ViewHolder) v.getTag();

				// al.clear();
				Intent intent = new Intent(mContext, EditPower.class)
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				intent.putExtra(EditPower.TRIGGER_MODE, items.get(vh.positions)
						.getTriggerMode());

				intent.putExtra(EditPower.WIFI, items.get(vh.positions)
						.getWifi());
				intent.putExtra(EditPower.MDATA, items.get(vh.positions)
						.getData());
				intent.putExtra(EditPower.BLUETOOTH, items.get(vh.positions)
						.getBluetooth());
				intent.putExtra(EditPower.AMODE, items.get(vh.positions)
						.getAmode());
				intent.putExtra(EditPower.SYNC, items.get(vh.positions)
						.getSync());
				intent.putExtra(EditPower.BNESS_SEEKBAR, items
						.get(vh.positions).getBrightness());

				intent.putExtra("extraTitle", items.get(vh.positions)
						.getProfileName());

				intent.putExtra(EditPower.VIB, items.get(vh.positions)
						.getHapticFeedback());

				intent.putExtra(EditPower.AROTATE, items.get(vh.positions)
						.getAutoRotate());

				intent.putExtra(EditPower.RINGMODE, items.get(vh.positions)
						.getRingMode());

				intent.putExtra(EditPower.SCREEN_TIMEOUT,
						items.get(vh.positions).getScreenTimeout());

				intent.putExtra(EditPower.POS, vh.positions);

				intent.putExtra(EditPower.TRIGGER, items.get(vh.positions)
						.getTrigger());
				intent.putExtra(EditPower.TRIGGER_B_LEVEL,
						items.get(vh.positions).getTiggerLevel());
				intent.putExtra(EditPower.S_OFF_INT_MDATA,
						items.get(vh.positions).getS_Off_int_mdata());
				intent.putExtra(EditPower.S_OFF_INT_WIFI,
						items.get(vh.positions).getS_Off_int_wifi());
				intent.putExtra(EditPower.S_OFF_MDATA, items.get(vh.positions)
						.getS_Off_mdata());
				intent.putExtra(EditPower.S_OFF_WIFI, items.get(vh.positions)
						.getS_Off_wifi());

				mContext.startActivity(intent);
				myAct.overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);

			}
		});

		holder.selected.setChecked(getChecked(position));
		holder.selected.setTag(holder);
		holder.selected.setOnClickListener(new OnClickListener() {
			ViewHolder vhetro;
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(final View v) {
				if(((RadioButton)v).isChecked()){
					Log.CToast(mContext, "Switching profile, Please wait.");
				vhetro = (ViewHolder) v.getTag();
				forceChecked = vhetro.positions;
				mContext.getSharedPreferences(
						"saphion.batterycaster_preferences"
								+ "_new_power",
						Context.MODE_MULTI_PROCESS) 
						.edit()
						.putInt(PreferenceHelper.POSITION,
								vhetro.positions).commit();
				mContext.getSharedPreferences(
						"saphion.batterycaster_preferences"
								+ "_new_power",
						Context.MODE_MULTI_PROCESS)
						.edit()
						.putInt(PreferenceHelper.POSITIONS,
								vhetro.positions).commit();
				mContext.sendBroadcast(new Intent(
						Intents.SELECTOR_INTENT));

				Handler h = new Handler();
				h.postDelayed(new Runnable() {

					@Override
					public void run() { // notifyDataSetChanged();
						mContext.sendBroadcast(new Intent(
								Intents.SWITCHER_INTENT)
								.putExtra(
										EditPower.BNESS_SEEKBAR,
										items.get(vhetro.positions)
												.getBrightness())
								.putExtra(PreferenceHelper.POSITION,
										vhetro.positions)
								.putExtra("use", true));
					}
				}, 10);
				
				h.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						((RadioButton)v).setChecked(true);
						notifyDataSetChanged();
					}
				}, 15);
				
				}
				

			}
		});
		holder.selected
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					//ViewHolder vhetro;

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
/*						vhetro = (ViewHolder) buttonView.getTag();
						mContext.getSharedPreferences(
								"saphion.batterycaster_preferences"
										+ "_new_power",
								Context.MODE_MULTI_PROCESS)
								.edit()
								.putInt(PreferenceHelper.POSITION,
										vhetro.positions).commit();
						mContext.getSharedPreferences(
								"saphion.batterycaster_preferences"
										+ "_new_power",
								Context.MODE_MULTI_PROCESS)
								.edit()
								.putInt(PreferenceHelper.POSITIONS,
										vhetro.positions).commit();
						mContext.sendBroadcast(new Intent(
								Intents.SELECTOR_INTENT));

						Handler h = new Handler();
						h.postDelayed(new Runnable() {

							@Override
							public void run() { // notifyDataSetChanged();
								mContext.sendBroadcast(new Intent(
										Intents.SWITCHER_INTENT)
										.putExtra(
												EditPower.BNESS_SEEKBAR,
												items.get(vhetro.positions)
														.getBrightness())
										.putExtra(PreferenceHelper.POSITION,
												vhetro.positions)
										.putExtra("use", true));
							}
						}, 10);
						
*/
						
					}
				});

		/*
		 * holder.click.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) { try { if (count == 0)
		 * { ViewHolder vh = (ViewHolder) v.getTag(); count++;
		 * al.add(vh.positions); setBackground(vh.back,
		 * al.contains(vh.positions)); try { pos[vh.positions] = true; } catch
		 * (Exception ex) { } mContext.sendBroadcast(new Intent(
		 * Intents.ActionModeIntent)); } } catch (Exception ex) { } return true;
		 * } });
		 */

		/*
		 * if (getSelCount() > 0) setBackground(holder.back,
		 * al.contains(position)); else setBackground(holder.back, false);
		 */

		setImageView(holder.bness, items.get(position).getBrightness());

		holder.footerFiller.setVisibility(position < getCount() - 1 ? View.GONE
				: View.VISIBLE);
		holder.delete.setTag(position);// pf.deleteListner);
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(getChecked((Integer)v.getTag())){
					Log.CToast(mContext, "Unable to delete.\nProfile currently in use.");
					return;
				}
				showUndoBar(remove((Integer) v.getTag()));
			}
		});

		return convertView;

	}
	
	
	
	boolean getChecked(int position){
		boolean m = mContext.getSharedPreferences(
				"saphion.batterycaster_preferences" + "_new_power",
				Context.MODE_MULTI_PROCESS).getInt(PreferenceHelper.POSITION,
				getCount() - 1) == position;
		
		if(forceChecked != null){
			boolean k = forceChecked == position;
			if(checkedCount == getCount()){
			forceChecked = null;
			checkedCount = 0;
			}
			checkedCount++;
			//Log.Toast(mContext, "setting checked for position: " + position + " value: " + (m||k), Toast.LENGTH_LONG);
			return k;
		}
		//Log.Toast(mContext, "setting checked for position: " + position + " value: " + m, Toast.LENGTH_LONG);
		return m;
	}
	
	int checkedCount = 0;
	
	Integer forceChecked = null;
	
	private void showUndoBar(final Pair<Integer, PowerProfItems> p) {

		final Bundle b = new Bundle();
		b.putInt("index", p.first);
		mDeleted = p;
		UndoBarController.show(myAct, "Power Profile Deleted", mController, b);
	}

	Pair<Integer, PowerProfItems> mDeleted;

	UndoListener mController = new UndoListener() {

		@Override
		public void onUndo(Parcelable token) {
			if (mDeleted != null)
				add(mDeleted);
		}
	};

	
	private void setImageView(ImageView bness, int brightness) {
		if (brightness == -99) {
			bness.setImageResource(R.drawable.bauto_on);
		} else {
			if (brightness <= ToggleHelper.MINIMUM_BACKLIGHT) {
				bness.setImageResource(R.drawable.blow_on);
			} else if (brightness == ToggleHelper.MAXIMUM_BACKLIGHT) {
				bness.setImageResource(R.drawable.bfull_on);
			} else {
				bness.setImageResource(R.drawable.bhalf_on);
			}
		}

	}

	protected void removeObj(ArrayList<Integer> al2, int positions) {
		int i = 0;
		for (i = 0; i < al2.size(); i++) {
			if (al2.get(i) == positions) {
				break;
			}
		}
		al2.remove(i);

	}

	/*private void setBackground(RelativeLayout back, boolean select) {
		if (select)
			back.setBackgroundColor(mContext.getResources().getColor(
					R.color.alarm_selected_color));
		else
			back.setBackgroundColor(0x00ffffff);// mContext.getResources().getColor(
		// R.color.alarm_whiteish)
	}*/

	private void setimg(int idOff, int idOn, ImageView iv, boolean b) {
		if (b)
			iv.setImageResource(idOn);
		else
			iv.setImageResource(idOff);

	}

	PowerProfItems addedItem = null;

	public Pair<Integer, PowerProfItems> remove(int id) {
		
			
		Pair<Integer, PowerProfItems> p = new Pair<Integer, PowerProfItems>(id,
				items.remove(id));
		fixSelection();
		
		/*Log.Toast(
				mContext,
				"Removing-> pos: " + p.first + " item: "
						+ p.second.getProfileName() + " items size: "
						+ items.size() + " adapter size: " + getCount(),
				Toast.LENGTH_LONG);*/
		notifyDataSetChanged();
		PowerPreference.savePower(mContext, getAll());
		return p;
	}

	public void add(int id, PowerProfItems ai) {
		if (addedItem != null) {
			items.remove(addedItem);
			addedItem = null;
		}
		
		items.add(id, ai);
		fixSelection();
		/*Log.Toast(mContext,
				"Undoing-> pos: " + id + " item: " + ai.getProfileName()
						+ " items size: " + items.size() + " adapter size: "
						+ getCount(), Toast.LENGTH_LONG);
*/		
		notifyDataSetChanged();

		PowerPreference.savePower(mContext, getAll());

	}
	
	
	public void add(Pair<Integer, PowerProfItems> p) {
		if (addedItem != null) {
			items.remove(addedItem);
			addedItem = null;
		}
		
		items.add(p.first, p.second);
		fixSelection();
		/*Log.Toast(
				mContext,
				"Undoing-> pos: " + p.first + " item: "
						+ p.second.getProfileName() + " items size: "
						+ items.size() + " adapter size: " + getCount(),
				Toast.LENGTH_LONG);*/
		notifyDataSetChanged();
		PowerPreference.savePower(mContext, getAll());

	}
	
	/**
	 * Fixes the radio button selection
	 */
	
	void fixSelection(){
		boolean flag = false;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isPowerProfequal(
					ToggleHelper.isWifiEnabled(mContext),
					ToggleHelper.isDataEnable(mContext),
					ToggleHelper.isBluetoothEnabled(mContext),
					ToggleHelper.isAModeEnabled(mContext),
					ToggleHelper.isSyncEnabled(),
					ToggleHelper.getBrightness(mContext),
					ToggleHelper.isHapticFback(mContext),
					ToggleHelper.isRotationEnabled(mContext),
					ToggleHelper.getRingerMode(mContext),
					String.valueOf(ToggleHelper.getScreenTimeOut(mContext)))) {
				mContext.getSharedPreferences(
						"saphion.batterycaster_preferences" + "_new_power",
						Context.MODE_MULTI_PROCESS).edit()
						.putInt(PreferenceHelper.POSITION, i).commit();
				flag = true;
			}
		}
		if (!flag) {
			int pos = mContext.getSharedPreferences(
					"saphion.batterycaster_preferences" + "_new_power",
					Context.MODE_MULTI_PROCESS).getInt(
					PreferenceHelper.POSITIONS, getCount() - 1);
			if (getCount() <= pos)
				pos = 0;
			PowerProfItems mItem = items.get(pos);
			items.add(
					getCount(),
					addedItem = new PowerProfItems(ToggleHelper
							.isWifiEnabled(mContext), ToggleHelper
							.isDataEnable(mContext), ToggleHelper
							.isBluetoothEnabled(mContext), ToggleHelper
							.isAModeEnabled(mContext), ToggleHelper
							.isSyncEnabled(), ToggleHelper
							.getBrightness(mContext), "User Defined " + (items.size()),
							ToggleHelper.isHapticFback(mContext), ToggleHelper
									.isRotationEnabled(mContext), ToggleHelper
									.getRingerMode(mContext), String
									.valueOf(ToggleHelper
											.getScreenTimeOutIndex(mContext)),
							false, 100, mItem.getS_Off_int_mdata(), mItem
									.getS_Off_int_wifi(), mItem
									.getS_Off_mdata(), mItem.getS_Off_wifi(), 2));
			/*mContext.getSharedPreferences(
					"saphion.batterycaster_preferences" + "_new_power",
					Context.MODE_MULTI_PROCESS).edit()
					.remove(PreferenceHelper.POSITION).commit();*/
			mContext.getSharedPreferences(
					"saphion.batterycaster_preferences" + "_new_power",
					Context.MODE_MULTI_PROCESS).edit()
					.putInt(PreferenceHelper.POSITION, items.size() - 1).commit();
		}
		
	/*	if(forceChecked != null){
			mContext.getSharedPreferences(
					"saphion.batterycaster_preferences" + "_new_power",
					Context.MODE_MULTI_PROCESS).edit()
					.putInt(PreferenceHelper.POSITION, forceChecked).commit();
		}*/
	}


	public static class ViewHolder {

		public View delete;
		ImageView wifi;
		ImageView data;
		ImageView btooth;
		ImageView amode;
		ImageView sync;
		ImageView bness;
		RadioButton selected;
		TextView name;
		RelativeLayout back;
		int positions;
		LinearLayout click;
		View footerFiller;

	}

	/*
	 * int count = 0; public boolean pos[];
	 */
	// ArrayList<Integer> al;

	/*
	 * @Override public void notifyDataSetChanged() { count = 0; pos = new
	 * boolean[getCount()]; for (int i = 0; i < pos.length; i++) pos[i] = false;
	 * al = new ArrayList<Integer>(); al.clear(); super.notifyDataSetChanged();
	 * }
	 */

	/*
	 * public int getSelCount() { return count; }
	 * 
	 * public void setSelCount(int x) { count = x; }
	 * 
	 * public void delete() { int k = 0; int size = getCount(); if (size > 1) {
	 * for (int i = 0; i < size; i++) if (pos[i]) { remove(i - k); k++; } } else
	 * { Log.Toast(mContext, "Cannot Delete all the profiles",
	 * Toast.LENGTH_LONG); } }
	 */

}
