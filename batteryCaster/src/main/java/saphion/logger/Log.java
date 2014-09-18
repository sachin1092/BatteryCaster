package saphion.logger;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Log {

	static boolean DEBUG = true;
	static String TAG = "Stencil";

	public static void d(Object text) {
		if (DEBUG)
			android.util.Log.d(TAG, text.toString());
	}

	public static void Toast(Context c, String text, int length) {
		if (DEBUG)
			android.widget.Toast.makeText(c, text, length).show();
	}

	public static void CToast(Context c, String text) {
		Toast toast = Toast.makeText(c, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

}
