package saphion.fragment.alarm.alert;

public class Intents {
	/**
	 * Broadcasted when an alarm fires.
	 */
	public static final String ALARM_ALERT_ACTION = "saphion.batterycaster.ALARM_ALERT";

	/**
	 * Broadcasted when alarm is dismissed.
	 */
	public static final String ALARM_DISMISS_ACTION = "saphion.batterycaster.ALARM_DISMISS";

	/**
	 * Broadcasted when alarm is scheduled
	 */
	public static final String ACTION_ALARM_SCHEDULED = "saphion.batterycaster.ACTION_ALARM_SCHEDULED";

	/**
	 * Broadcasted when alarm is scheduled
	 */
	public static final String ACTION_ALARMS_UNSCHEDULED = "saphion.batterycaster.ACTION_ALARMS_UNSCHEDULED";

	/**
	 * Key of the AlarmCore attached as a parceble extra
	 */
	public static final String EXTRA_ID = "saphion.intent.extra.alarm";

	/**
	 * Battery Intent when Screen is off for Alarm and logging values
	 */
	public static final String MYBAT_INTENT = "saphion.batterycaster.MYBATINTENT";

	/**
	 * Intent for starting Action Mode in Power Fragment
	 */
	public static final String ActionModeIntent = "saphion.batterycaster.ACTIONMODE";
	
	/**
	 * Profile Switcher Intent
	 */
	public static final String SWITCHER_INTENT = "saphion.batterycaster.SWITCHER";
	
	/**
	 * Intent for updating nb
	 */
	public static final String SWITCHER_NOTI = "saphion.batterycaster.SWITCHER_NOTI";
	
	/**
	 * Intent for switching radio buttons
	 */
	public static final String SELECTOR_INTENT = "saphion.batterycaster.SELECTOR_INTENT";
	
	/**
	 * Intent for finishing help
	 */
	public static final String FINISH_INTENT = "saphion.batterycaster.FINISH_INTENT";
	/**
	 * Torch of and on intents
	 */
	public static final String TORCH_ON = "saphion.batterycaster.TORCH_ON";
	public static final String TORCH_OFF = "saphion.batterycaster.TORCH_OFF";
}
