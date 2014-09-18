package saphion.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

	public final static String NOTIFICATION_ENABLE = "NotiEnab";
	public final static String PREV_BAT_TIME = "pBatTime";
	public final static String PREV_BAT_LEVEL = "prevlevel";
	public final static String DISABLE_CLICKS = "disableClicks";
	public final static String BAT_DISCHARGE = "batdis";
	public final static String BAT_CHARGE = "batcharge";
	public final static String WIDGET_ONCLICK = "widgetclick";
	public final static String WIDGET_TEXT = "widgettext";
	public final static String WIDGET_LINE_BACK = "lineback";
	public final static String WIDGET_LINE_LEFT = "lineleft";
	public final static String THEME_PREF = "theme";
	public final static String WIDGET_FONT = "fontBatteryWidget";
	public final static String WIDGET_FONT_SIZE = "batfontsize";
	public final static String WIDGET_DIFF_BAT = "diffbattery";
	public final static String WIDGET_THEMES = "widgettheme";
	public static final String SETTINGS_WIDGET_FILE = "widget_pref_";
	public static final String EXTRA_WIDGET_IDS = "widget_ids";
	public static final String NOTI_THEME = "noti_theme";
	public static final String MAIN_TEMP = "main_temp_unit";
	public static final String LAST_CHARGED = "last_charged";
	public static final String FIRST_TIME = "first_time";
	public static final String BAT_TIME = "bt";
	public static final String BAT_VALS = "bv";
	public static final String SILENCE = "silence_after";
	
	public static final String PLAY_WHEN_SILENT = "playwhensilent";
	public static final String LINE_BACK = "newlineback";
	public static final String POSITION = "pos";
	public static final String SHOW_CHART = "showchart";
	public static final String SHOW_WIFI = "showwifi";
	public static final String SHOW_MDATA = "showmdata";
	public static final String SHOW_AMODE = "showamode";
	public static final String SHOW_SYNC = "showsync";
	public static final String SHOW_BNESS = "showbness";
	public static final String SHOW_PSWITCHER = "showpswitcher";
	public static final String SHOW_BTOOTH = "showbtooth";
	public static final String SHOW_AROTATE = "showarotate";
	public static final String NOTI_MAINTEXT = "notimaintext";
	public static final String NOTI_SUBTEXT = "notisubtext";
	public static final String NOTI_PRIORITY = "noti_priority";
	public static final String NOTI_ONCLICK = "noti_onclick";
	public static final String PROF1 = "spin_1";
	public static final String PROF2 = "spin_2";
	public static final String ALERT_NOTIFICATION = "alert_notification";
	public static final String SHOW_NOTIFICATION = "show_notification";
	public static final String SHOW_POPUP = "show_popup";
	

	public static final String KEY_ONE_PERCENT_HACK = "onePerHack";
	/**
	 * From what I understand, charge_counter can go somewhat over 100; I'm
	 * guessing at 115 being an appropriate cutoff point.
	 */
	public static final int CHARGE_COUNTER_LEGIT_MAX = 115;
	public static final String CURR_RING = "cRing";
	public static final String POSITIONS = "posExtra";
	public static final String FIRST_TIME_HELP = "fth";
	
	public static final String STAT_CONNECTED_COUNT = "stat_connected_count";
	public static final String STAT_DISCONNECTED_COUNT = "stat_diconnected_count";
	public static final String STAT_CHARGING_AVGTIME = "stat_charging_avgtime";
	public static final String STAT_DISCHARGING_AVGTIME = "stat_discharging_avgtime";
	public static final String STAT_CONNECTED_LAST_TIME = "stat_connected_last_time";
	public static final String STAT_DISCONNECTED_LAST_TIME = "stat_disconnected_last_time";
	public static final String STAT_CONNECTED_LAST_LEVEL = "stat_connected_last_level";
	public static final String STAT_DISCONNECTED_LAST_LEVEL = "stat_disconnected_last_level";
	
	//new alarm notification
	public static final String PREFERENCE_ONLYNOTIFICATION = "pref_onlynotification";
	public static final String SHOW_TORCH = "show_torch";
	public static final String SHOW_WIFIHOTSPOT = "show_wifihotspot";
	public static final String HAS_FLASH = "has_flash";
	public static final String HAS_HOTSPOT = "has_hotspot";

	public static class Pref_Helper {

		static int theme;
		static String font;
		static float lineleft;
		static float lineback;
		static int widget_text;
		static boolean dis_clicks;
		static int widget_click;
		static float fontsize;
		static boolean wid_diff_color;

		public Pref_Helper(int theme, String font, float lineleft,
				float lineback, int widget_text, boolean dis_clicks,
				int widget_click, float fontsize, boolean wid_diff_color) {

			Pref_Helper.theme = theme;
			Pref_Helper.font = font;
			Pref_Helper.lineleft = lineleft;
			Pref_Helper.lineback = lineback;
			Pref_Helper.widget_text = widget_text;
			Pref_Helper.dis_clicks = dis_clicks;
			Pref_Helper.widget_click = widget_click;
			Pref_Helper.fontsize = fontsize;
			Pref_Helper.wid_diff_color = wid_diff_color;
		}

		public static void savePrefs(Context context, int awID) {

			String file = SETTINGS_WIDGET_FILE + awID;
			SharedPreferences getPrefs = context.getSharedPreferences(file,
					Context.MODE_PRIVATE);
			getPrefs.edit().putInt(WIDGET_THEMES, theme).commit();
			getPrefs.edit().putString(WIDGET_FONT, font).commit();
			getPrefs.edit().putFloat(WIDGET_LINE_LEFT, lineleft).commit();
			getPrefs.edit().putFloat(WIDGET_LINE_BACK, lineback).commit();
			getPrefs.edit().putInt(WIDGET_TEXT, widget_text).commit();
			getPrefs.edit().putBoolean(DISABLE_CLICKS, Pref_Helper.dis_clicks)
					.commit();
			getPrefs.edit().putInt(WIDGET_ONCLICK, widget_click).commit();
			getPrefs.edit().putFloat(WIDGET_FONT_SIZE, fontsize).commit();
			getPrefs.edit().putBoolean(WIDGET_DIFF_BAT, wid_diff_color)
					.commit();

		}

	}

}
