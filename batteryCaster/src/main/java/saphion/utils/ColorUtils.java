package saphion.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ColorUtils {

	public static Bitmap repleceIntervalColor(Bitmap bitmap, /*
															 * int redStart, int
															 * redEnd, int
															 * greenStart, int
															 * greenEnd, int
															 * blueStart, int
															 * blueEnd,
															 */
			int replaceThisColor, int colorNew) {
		if (bitmap != null) {
			int picw = bitmap.getWidth();
			int pich = bitmap.getHeight();
			int[] pix = new int[picw * pich];
			bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
			for (int y = 0; y < pich; y++) {
				for (int x = 0; x < picw; x++) {
					int index = y * picw + x;
					/*
					 * if (((Color.red(pix[index]) >= redStart) && (Color
					 * .red(pix[index]) <= redEnd)) && ((Color.green(pix[index])
					 * >= greenStart) && (Color .green(pix[index]) <= greenEnd))
					 * && ((Color.blue(pix[index]) >= blueStart) && (Color
					 * .blue(pix[index]) <= blueEnd))) {
					 */
					if (pix[index] == replaceThisColor) {
						pix[index] = colorNew;
					}
				}
			}
			Bitmap bm = Bitmap.createBitmap(pix, picw, pich,
					Bitmap.Config.ARGB_8888);
			return bm;
		}
		return null;
	}

	public static Bitmap repleceIntervalColor(int id,/*
													 * int redStart, int redEnd,
													 * int greenStart, int
													 * greenEnd, int blueStart,
													 * int blueEnd
													 */int oldColor,
			int colorNew, Context mContext) {
		return repleceIntervalColor(
				BitmapFactory.decodeResource(mContext.getResources(), id),
				oldColor, colorNew);
	}

}
