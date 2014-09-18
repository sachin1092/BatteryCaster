package saphion.stencil.batterywidget;

import saphion.utils.ActivityFuncs;
import saphion.utils.PreferenceHelper;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class WidPrefHelper {

	private int widget_text;
	private String font;
	private float fontsize;
	private boolean dis_clicks;
	private int widget_click;
	private float lineleft;
	private float lineback;
	private boolean wid_diff_color;
	private int Widgettheme;
	private SharedPreferences getPrefs;
	private float newlineback;
	int awID;
	Context mContext;
	private boolean hasChanged;

	public WidPrefHelper(Context mContext, int awID) {
		this.awID = awID;
		this.mContext = mContext;
		String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
		getPrefs = mContext.getSharedPreferences(file, Context.MODE_PRIVATE);
		widget_text = getPrefs.getInt(PreferenceHelper.WIDGET_TEXT, 0);
		font = getPrefs.getString(PreferenceHelper.WIDGET_FONT, "empty");
		fontsize = getPrefs.getFloat(PreferenceHelper.WIDGET_FONT_SIZE,
				(float) ActivityFuncs.SpToPx(mContext, 70f));
		dis_clicks = getPrefs
				.getBoolean(PreferenceHelper.DISABLE_CLICKS, false);
		widget_click = getPrefs.getInt(PreferenceHelper.WIDGET_ONCLICK, 0);
		lineleft = getPrefs.getFloat(PreferenceHelper.WIDGET_LINE_LEFT, ActivityFuncs.ReturnHeight(15, mContext));
		lineback = getPrefs.getFloat(PreferenceHelper.WIDGET_LINE_BACK, ActivityFuncs.ReturnHeight(5, mContext));
		wid_diff_color = getPrefs.getBoolean(PreferenceHelper.WIDGET_DIFF_BAT,
				false);
		Widgettheme = getPrefs.getInt(PreferenceHelper.WIDGET_THEMES, 0);
		newlineback = getPrefs.getFloat(PreferenceHelper.LINE_BACK, -99);
		hasChanged = false;
	}

	public float getLineBac() {

		return newlineback;
	}

	public void setPrefs(Context mContext, int awID) {
		String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
		getPrefs = mContext.getSharedPreferences(file, Context.MODE_PRIVATE);
	}

	public void setLineBac(float x) {
		if (x != newlineback)
			hasChanged = true;
		newlineback = x;
	}

	public void setWidgetText(int x) {
		if (x != widget_text)
			hasChanged = true;
		widget_text = x;
	}

	public void setFont(String font) {
		if (!font.equals(this.font))
			hasChanged = true;
		this.font = font;
	}

	public void setFontSize(float fzise) {
		if (fzise != this.fontsize)
			hasChanged = true;
		this.fontsize = fzise;
	}

	public void setDisableClick(boolean click) {
		if (click != dis_clicks)
			hasChanged = true;
		this.dis_clicks = click;
	}

	public void setWidgetClick(int x) {
		if (x != this.widget_click)
			hasChanged = true;
		this.widget_click = x;
	}

	public void setLineLeft(float x) {
		if (x != this.lineleft)
			hasChanged = true;
		this.lineleft = x;
	}

	public void setLineBack(float x) {
		if (x != this.lineback)
			hasChanged = true;
		this.lineback = x;
	}

	public void setWidDiffColor(boolean bool) {
		if (bool != this.wid_diff_color)
			hasChanged = true;
		this.wid_diff_color = bool;
	}

	public void setWidgetTheme(int x) {
		if (x != this.Widgettheme)
			hasChanged = true;
		this.Widgettheme = x;
	}

	public boolean hasChanged() {
		return this.hasChanged;
	}

	/**
	 * Getting data
	 */

	public int getWidgetText() {
		return widget_text;
	}

	public String getFont() {
		return this.font;
	}

	public float getFontSize() {
		return this.fontsize;
	}

	public boolean getDisableClick() {
		return this.dis_clicks;
	}

	public int getWidgetClick() {
		return this.widget_click;
	}

	public float getLineLeft() {
		return this.lineleft;
	}

	public float getLineBack() {
		return this.lineback;
	}

	public boolean getWidDiffColor() {
		return this.wid_diff_color;
	}

	public int getWidgetTheme() {
		return this.Widgettheme;
	}

	public void saveEverything(Context mContext, int awID) {
		String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
		getPrefs = mContext.getSharedPreferences(file, Context.MODE_PRIVATE);
		getPrefs.edit().putInt(PreferenceHelper.WIDGET_TEXT, widget_text)
				.commit();
		getPrefs.edit().putString(PreferenceHelper.WIDGET_FONT, font).commit();
		getPrefs.edit().putFloat(PreferenceHelper.WIDGET_FONT_SIZE, fontsize)
				.commit();
		getPrefs.edit().putBoolean(PreferenceHelper.DISABLE_CLICKS, dis_clicks)
				.commit();
		getPrefs.edit().putInt(PreferenceHelper.WIDGET_ONCLICK, widget_click)
				.commit();
		getPrefs.edit().putFloat(PreferenceHelper.WIDGET_LINE_LEFT, lineleft)
				.commit();
		getPrefs.edit().putFloat(PreferenceHelper.WIDGET_LINE_BACK, lineback)
				.commit();
		getPrefs.edit()
				.putBoolean(PreferenceHelper.WIDGET_DIFF_BAT, wid_diff_color)
				.commit();
		getPrefs.edit().putInt(PreferenceHelper.WIDGET_THEMES, Widgettheme)
				.commit();

		getPrefs.edit().putFloat(PreferenceHelper.LINE_BACK, newlineback)
				.commit();
	}

}
