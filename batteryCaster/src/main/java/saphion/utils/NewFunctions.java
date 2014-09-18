package saphion.utils;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class NewFunctions {

	public static Bitmap createSquare(int Color1, int Color2, int Color3,
			int Color4, Context mContext) {
		Bitmap outBitmap = Bitmap.createBitmap(ReturnHeight(50, mContext),
				ReturnHeight(50, mContext), Config.ARGB_8888);
		Canvas c = new Canvas(outBitmap);

		c.drawBitmap(
				generatePatternBi(outBitmap.getWidth(), outBitmap.getHeight(),
						mContext), 0, 0, null);

		createRect(c, Color1, 0, 0, outBitmap.getWidth() / 2,
				outBitmap.getHeight() / 2);
		createRect(c, Color2, outBitmap.getWidth() / 2, 0,
				outBitmap.getWidth(), outBitmap.getHeight() / 2);
		createRect(c, Color3, 0, outBitmap.getHeight() / 2,
				outBitmap.getWidth() / 2, outBitmap.getHeight());
		createRect(c, Color4, outBitmap.getWidth() / 2,
				outBitmap.getHeight() / 2, outBitmap.getWidth(),
				outBitmap.getHeight());
		return outBitmap;
	}

	public static void createRect(Canvas c, int Color, int left, int top,
			int right, int bottom) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color);

		Rect rect = new Rect(left, top, right, bottom);
		c.drawRect(rect, paint);
	}

	/**
	 * Convert dp to px
	 * 
	 * @author Sachin
	 * @param i
	 * @param mContext
	 * @return
	 */

	public static int ReturnHeight(int i, Context mContext) {

		DisplayMetrics displayMetrics = mContext.getResources()
				.getDisplayMetrics();
		return (int) ((i * displayMetrics.density) + 0.5);

	}

	public static Drawable generatePatternBitmap(Context mContext) {

		int width = ReturnHeight(50, mContext);
		int height = ReturnHeight(50, mContext);

		Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);

		int mRectangleSize = (int) (5 * mContext.getResources()
				.getDisplayMetrics().density);
		int numRectanglesHorizontal = (int) Math.ceil((width / mRectangleSize));
		int numRectanglesVertical = (int) Math.ceil(height / mRectangleSize);

		Rect r = new Rect();
		boolean verticalStartWhite = true;
		for (int i = 0; i <= numRectanglesVertical; i++) {

			boolean isWhite = verticalStartWhite;
			for (int j = 0; j <= numRectanglesHorizontal; j++) {

				r.top = i * mRectangleSize;
				r.left = j * mRectangleSize;
				r.bottom = r.top + mRectangleSize;
				r.right = r.left + mRectangleSize;

				Paint mPaintWhite = new Paint();
				mPaintWhite.setColor(0xffffffff);
				Paint mPaintGray = new Paint();
				mPaintGray.setColor(0xffcbcbcb);

				canvas.drawRect(r, isWhite ? mPaintWhite : mPaintGray);

				isWhite = !isWhite;
			}

			verticalStartWhite = !verticalStartWhite;

		}

		Drawable d = new BitmapDrawable(mContext.getResources(), mBitmap);

		return d;

	}

	public static Bitmap generatePatternBi(int width, int height,
			Context mContext) {

		if (width <= 0 || height <= 0) {
			return null;
		}

		Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);

		int mRectangleSize = (int) (5 * mContext.getResources()
				.getDisplayMetrics().density);
		int numRectanglesHorizontal = (int) Math.ceil((width / mRectangleSize));
		int numRectanglesVertical = (int) Math.ceil(height / mRectangleSize);

		Rect r = new Rect();
		boolean verticalStartWhite = true;
		for (int i = 0; i <= numRectanglesVertical; i++) {

			boolean isWhite = verticalStartWhite;
			for (int j = 0; j <= numRectanglesHorizontal; j++) {

				r.top = i * mRectangleSize;
				r.left = j * mRectangleSize;
				r.bottom = r.top + mRectangleSize;
				r.right = r.left + mRectangleSize;

				Paint mPaintWhite = new Paint();
				mPaintWhite.setColor(0xffffffff);
				Paint mPaintGray = new Paint();
				mPaintGray.setColor(0xffcbcbcb);

				canvas.drawRect(r, isWhite ? mPaintWhite : mPaintGray);

				isWhite = !isWhite;
			}

			verticalStartWhite = !verticalStartWhite;

		}

		return mBitmap;

	}

	/**
	 * Creates a Bitmap with specified color and of 50dp size
	 * 
	 * @param color
	 * @param context
	 * @return Colored Bitmap
	 */
	public static Bitmap RetBitCol(int color, Context mContext) {

		Bitmap mBitmap = Bitmap.createBitmap(ReturnHeight(50, mContext),
				ReturnHeight(50, mContext), Config.ARGB_8888);
		Canvas c = new Canvas(mBitmap);
		c.drawColor(color);
		return mBitmap;
	}

	public static boolean hasCurrDay(int i) {
		int x = getCurrDay();
		return ((i & x) == x);
	}

	public static int getCurrDay() {
		switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			return 1;
		case Calendar.MONDAY:
			return 2;
		case Calendar.TUESDAY:
			return 4;
		case Calendar.WEDNESDAY:
			return 8;
		case Calendar.THURSDAY:
			return 16;
		case Calendar.FRIDAY:
			return 32;
		case Calendar.SATURDAY:
			return 64;
		default:
			return 0;
		}
	}

	public static boolean hasCharge(boolean isconnected, int charge) {
		int isConnected = isconnected ? 1 : 2;
		
		return ((isConnected & charge) == isConnected);
		//return false;
	}

}
