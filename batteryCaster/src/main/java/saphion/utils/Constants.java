package saphion.utils;

public class Constants {
    public static final String LOG = "BCaster";
    public static final boolean DEBUG = false;
    public static String VERSION;

    public static final String EXTRA_WIDGET_IDS = "widgetIds";
    public static final String SETTINGS_WIDGET_FILE = "widgetPrefs_";

    public static final int DOCK_STATE_UNKNOWN = 0;
    public static final int DOCK_STATE_UNDOCKED = 1;
    public static final int DOCK_STATE_CHARGING = 2;
    public static final int DOCK_STATE_DOCKED = 3;
    public static final int DOCK_STATE_DISCHARGING = 4;

    public static final String SETTING_VERSION = "version";
    public static final int SETTING_VERSION_CURRENT = 4;
    public static final String SETTING_ALWAYS_SHOW_DOCK = "alwaysShowDock";
    public static final boolean SETTING_ALWAYS_SHOW_DOCK_DEFAULT = true;

    public static final int TEXT_POS_INVISIBLE = 0;
    public static final int TEXT_POS_TOP = 1;
    public static final int TEXT_POS_MIDDLE = 2;
    public static final int TEXT_POS_BOTTOM = 3;
    public static final int TEXT_POS_ABOVE = 4;
    public static final int TEXT_POS_BELOW = 5;
    public static final String SETTING_TEXT_POS = "textPosition";
    public static final int SETTING_TEXT_POS_DEFAULT = TEXT_POS_MIDDLE;

    public static final String SETTING_TEXT_SIZE = "textSize";
    public static final int SETTING_TEXT_SIZE_DEFAULT = 14;
    public static final String SETTING_SHOW_NOT_DOCKED = "showNotDockedMessage";
    public static final boolean SETTING_SHOW_NOT_DOCKED_DEFAULT = true;

    public static final int BATTERY_SELECTION_BOTH = 3;
    public static final int BATTERY_SELECTION_MAIN = 1;
    public static final int BATTERY_SELECTION_DOCK = 2;
    public static final String SETTING_SHOW_SELECTION = "batterySelection";
    public static final int SETTING_SHOW_SELECTION_DEFAULT = BATTERY_SELECTION_BOTH;

    public static final int TEXT_COLOR_WHITE = 0;
    public static final int TEXT_COLOR_BLACK = 1;
    public static final String SETTING_TEXT_COLOR = "textColor";
    public static final int SETTING_TEXT_COLOR_DEFAULT = TEXT_COLOR_WHITE;

    public static final int MARGIN_NONE = 0;
    public static final int MARGIN_TOP = 1;
    public static final int MARGIN_BOTTOM = 2;
    public static final int MARGIN_BOTH = 3;
    public static final String SETTING_MARGIN = "marginLocation";
    public static final int SETTING_MARGIN_DEFAULT = MARGIN_NONE;

    public static final String SETTING_SHOW_LABEL = "showBatteryLabel";
    public static final boolean SETTING_SHOW_LABEL_DEFAULT = false;
    public static final String SETTING_SHOW_OLD_DOCK = "showOldDockStatus";
    public static final boolean SETTING_SHOW_OLD_DOCK_DEFAULT = true;
    public static final int TEMP_UNIT_CELSIUS = 0;
    public static final int TEMP_UNIT_FAHRENHEIT = 1;
    public static final String SETTING_TEMP_UNITS = "tempUnitsInt";
    public static final int SETTING_TEMP_UNITS_DEFAULT = TEMP_UNIT_CELSIUS;

    public static final String SETTING_SWAP_BATTERIES = "swapBatteries";
    public static final boolean SETTING_SWAP_BATTERIES_DEFAULT = false;
    public static final String SETTING_THEME = "theme";
    public static final String SETTING_THEME_DEFAULT = "Default";
    public static final String SETTING_JUST_SWAPPED = "justSwapped";
    public static final boolean SETTING_JUST_SWAPPED_DEFAULT = false;

    public static final String SETTINGS_FILE = "appSettings";
    public static final String SETTING_NOTIFICATION_ICON = "showNotificationIcon";
    public static final boolean SETTING_NOTIFICATION_ICON_DEFAULT = true;
    public static final String SETTING_DEFAULT_DAYS_TAB = "defaultDaysTab";
    public static final int SETTING_DEFAULT_DAYS_TAB_DEFAULT = 3;
    
    public static final String FONT_DOSIS = "Dosis-Book.ttf";
    public static final String FONT_DOSIS_BOLD = "Dosis-SemiBold.ttf";
    public static final String FONT_CNL = "cnlbold.ttf";
    public static final String FONT_ROBOTO_COND = "RobotoCondensed-Regular.ttf";
    public static final String FONT_ROBOTO = "roboto.ttf";
    public static final String FONT_ROBOTO_MED = "Roboto-Medium.ttf";

    public static final String FONT_USING = FONT_DOSIS;
    public static final String FONT_USING_BOLD = FONT_DOSIS_BOLD;
	

}
