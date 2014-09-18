package saphion.fragment.powerfragment;

import saphion.logger.Log;

public class PowerProfItems {

	private boolean wifi;
	private boolean data;
	private boolean btooth;
	private boolean amode;
	private boolean sync;
	private int bness; // -99 for auto
	private String ProfName;
	private boolean hfback;
	private boolean aRotate;
	private int ringMode;
	private String screenTimeout;
	private boolean trigger;
	private int trig_level;
	private boolean s_off_mdata;
	private boolean s_off_wifi;
	private int s_off_int_mdata;
	private int s_off_int_wifi;
	private int trigger_mode;

	public PowerProfItems() {
		this.wifi = false;
		this.data = false;
		this.btooth = false;
		this.amode = false;
		this.sync = false;
		this.bness = 1;
		this.ProfName = "Custom";
		this.hfback = false;
		this.aRotate = false;
		this.ringMode = 0;
		this.screenTimeout = "0";
		this.trigger = false;
		this.trig_level = 100;
		this.s_off_int_mdata = -99;
		this.s_off_int_wifi = -99;
		this.s_off_mdata = false;
		this.s_off_wifi = false;
		this.trigger_mode = 2;
	}
	
	public PowerProfItems(int count) {
		this.wifi = false;
		this.data = false;
		this.btooth = false;
		this.amode = false;
		this.sync = false;
		this.bness = 1;
		this.ProfName = "Custom Profile " + count;
		this.hfback = false;
		this.aRotate = false;
		this.ringMode = 0;
		this.screenTimeout = "0";
		this.trigger = false;
		this.trig_level = 100;
		this.s_off_int_mdata = -99;
		this.s_off_int_wifi = -99;
		this.s_off_mdata = false;
		this.s_off_wifi = false;
		this.trigger_mode = 2;
	}

	public PowerProfItems(boolean wifi, boolean data, boolean btooth,
			boolean amode, boolean sync, int bness, String ProfName,
			boolean hfback, boolean aRotate, int ringMode, String sTimeout,
			boolean trigger, int trig_level, int s_off_int_mdata,
			int s_off_int_wifi, boolean s_off_mdata, boolean s_off_wifi, int t_mode) {
		this.wifi = wifi;
		this.data = data;
		this.btooth = btooth;
		this.amode = amode;
		this.sync = sync;
		this.bness = bness;
		this.ProfName = ProfName;
		this.hfback = hfback;
		this.aRotate = aRotate;
		this.ringMode = ringMode;
		this.screenTimeout = sTimeout;
		this.trigger = trigger;
		this.trig_level = trig_level;
		this.s_off_int_mdata = s_off_int_mdata;
		this.s_off_int_wifi = s_off_int_wifi;
		this.s_off_mdata = s_off_mdata;
		this.s_off_wifi = s_off_wifi;
		this.trigger_mode = t_mode;
	}

	public boolean isPowerProfequal(boolean wifi, boolean data, boolean btooth,
			boolean amode, boolean sync, int bness, boolean hfback,
			boolean aRotate, int ringMode, String sTimeout) {
		boolean ret = this.wifi == wifi && this.data == data && this.btooth == btooth
				&& this.amode == amode && this.sync == sync
				&& isBnessEqual(bness) && this.hfback == hfback
				&& this.aRotate == aRotate && isRingModeEqual(ringMode)
				&& isTimeoutEqual(sTimeout);
		
		Log.d("Power Profile equality: " + ret);
		return ret;
	}

	public boolean isBnessEqual(int bness) {
		Log.d("Brightness equality is: " + (bness == this.bness)
				+ " and original bness is: " + bness + " profile bness is: "
				+ this.bness);
		return bness == this.bness;
	}

	public boolean isRingModeEqual(int rmode) {
		Log.d("Ringmode equality is: " + (rmode == this.ringMode));
		return rmode == this.ringMode;
	}

	public boolean isTimeoutEqual(String tout) {
		int time = 0;
		switch (Integer.parseInt(this.screenTimeout)) {
		case 0:
			time = 15000;
			break;
		case 1:
			time = 30000;
			break;
		case 2:
			time = 60000;
			break;
		case 3:
			time = 120000;
			break;
		case 4:
			time = 600000;
			break;
		case 5:
			time = 1800000;
			break;
		case -1:
			time = -1;
		default:
			time = -1;
		}
		Log.d("Timeout equality is: " + (time == Integer.parseInt(tout)));
		return (time == Integer.parseInt(tout));
	}

	public void setTrigger(boolean x) {
		this.trigger = x;
	}
	
	public void setTriggerMode(int x){
		this.trigger_mode = x;
	}

	public void setTiggerLevel(int x) {
		this.trig_level = x;
	}

	public void setS_Off_mdata(boolean x) {
		this.s_off_mdata = x;
	}

	public void setS_Off_wifi(boolean x) {
		this.s_off_wifi = x;
	}

	public void setS_Off_int_mdata(int x) {
		this.s_off_int_mdata = x;
	}

	public void setS_Off_int_wifi(int x) {
		this.s_off_int_wifi = x;
	}

	public boolean getTrigger() {
		return this.trigger;
	}
	
	public int getTriggerMode(){
		return this.trigger_mode;
	}

	public int getTiggerLevel() {
		return this.trig_level;
	}

	public boolean getS_Off_mdata() {
		return this.s_off_mdata;
	}

	public boolean getS_Off_wifi() {
		return this.s_off_wifi;
	}

	public int getS_Off_int_mdata() {
		return this.s_off_int_mdata;
	}

	public int getS_Off_int_wifi() {
		return this.s_off_int_wifi;
	}

	public void setHapticFeedback(boolean x) {
		this.hfback = x;
	}

	public void setAutoRotate(boolean x) {
		this.aRotate = x;
	}

	public void setRingMode(int x) {
		this.ringMode = x;
	}

	public void setScreenTimeout(String x) {
		this.screenTimeout = x;
	}

	public void setProfileName(String name) {
		this.ProfName = name;
	}

	public String getProfileName() {
		return this.ProfName;
	}

	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}

	public void setData(boolean data) {
		this.data = data;
	}

	public void setBluetooth(boolean btooth) {
		this.btooth = btooth;
	}

	public void setAmode(boolean amode) {
		this.amode = amode;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public void setBrightness(int bness) {
		this.bness = bness;
	}

	public boolean getHapticFeedback() {
		return this.hfback;
	}

	public boolean getAutoRotate() {
		return this.aRotate;
	}

	public int getRingMode() {
		return this.ringMode;
	}

	public String getScreenTimeout() {
		return this.screenTimeout;
	}

	public boolean getWifi() {
		return this.wifi;
	}

	public boolean getData() {
		return this.data;
	}

	public boolean getBluetooth() {
		return this.btooth;
	}

	public boolean getAmode() {
		return this.amode;
	}

	public boolean getSync() {
		return this.sync;
	}

	public int getBrightness() {
		return this.bness;
	}
}
