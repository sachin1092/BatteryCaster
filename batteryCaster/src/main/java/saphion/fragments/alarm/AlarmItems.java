package saphion.fragments.alarm;

import java.util.ArrayList;

public class AlarmItems {

	int mLevel;
	boolean enabled;
	String Label;
	ArrayList<String> days;
	String ringTone;
	boolean vibrate;
	boolean repeat;

	public AlarmItems() {
		this.mLevel = 100;
		this.enabled = false;
		this.Label = "";
		this.days = new ArrayList<String>();
		this.days.add("mon");
		this.days.add("tue");
		this.days.add("wed");
		this.days.add("thu");
		this.days.add("fri");
		this.days.add("sat");
		this.days.add("sun");
		this.repeat = true;
		this.ringTone = "Default";
		this.vibrate = true;

	}

	public AlarmItems(int mLevel, boolean enabled, String Label,
			ArrayList<String> days, String ringTone, boolean vibrate,
			boolean repeat) {

		this.mLevel = mLevel;
		this.enabled = enabled;
		this.Label = Label;
		this.days = days;
		this.ringTone = ringTone;
		this.vibrate = vibrate;
		this.repeat = repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean getRepeat() {
		return repeat;
	}

	public void setLevel(int mLevel) {
		this.mLevel = mLevel;
	}

	public void setEnable(boolean enabled) {
		this.enabled = enabled;
	}

	public void setLabel(String Label) {
		this.Label = Label;
	}

	public void setDays(ArrayList<String> days) {
		this.days = days;
	}

	public void setRingTone(String ringTone) {
		this.ringTone = ringTone;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public int getLevel() {
		return mLevel;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public String getLabel() {
		return Label;
	}

	public ArrayList<String> getDays() {
		return days;
	}

	public String getRingTone() {
		return ringTone;
	}

	public boolean getVibrate() {
		return vibrate;
	}
}
