package info.ondrejcermak.configurationreader;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Configuration mConfiguration;

	private TextView mResolutionPx;
	private TextView mResolutionDp;
	private TextView mResolutionQualifier;
	private TextView mMinWidth;
	private TextView mDensity;
	private TextView mDpi;
	private TextView mSizeQualifier;
	private TextView mOrientation;

	private TextView mTouchscreen;
	private TextView mNavigation;

	private TextView mLocale;
	private TextView mFontScale;

	private TextView mImsi;
	private TextView mMcc;
	private TextView mMnc;

	private TextView mVersion;
	private TextView mApiLevel;
	private TextView mId;
	private TextView mBuildTags;
	private TextView mBootloader;
	private TextView mRadio;

	private TextView mManufacturer;
	private TextView mBrand;
	private TextView mModel;
	private TextView mProduct;
	private TextView mDesign;
	private TextView mBoard;
	private TextView mSerialNumber;
	private TextView mHardware;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mResolutionPx = (TextView) findViewById(R.id.text_resolution_px);
		mResolutionDp = (TextView) findViewById(R.id.text_resolution_dp);
		mResolutionQualifier = (TextView) findViewById(R.id.text_resolution_qualifier);
		mMinWidth = (TextView) findViewById(R.id.text_min_width);
		mDensity = (TextView) findViewById(R.id.text_density);
		mDpi = (TextView) findViewById(R.id.text_dpi);
		mSizeQualifier = (TextView) findViewById(R.id.text_size_qualifier);
		mOrientation = (TextView) findViewById(R.id.text_orientation);

		mTouchscreen = (TextView) findViewById(R.id.text_touchscreen);
		mNavigation = (TextView) findViewById(R.id.text_navigation);

		mLocale = (TextView) findViewById(R.id.text_locale);
		mFontScale = (TextView) findViewById(R.id.text_font_scale);

		mImsi = (TextView) findViewById(R.id.text_imsi);
		mMcc = (TextView) findViewById(R.id.text_mcc);
		mMnc = (TextView) findViewById(R.id.text_mnc);

		mVersion = (TextView) findViewById(R.id.text_version);
		mApiLevel = (TextView) findViewById(R.id.text_api_level);
		mId = (TextView) findViewById(R.id.text_id);
		mBuildTags = (TextView) findViewById(R.id.text_build_tags);
		mBootloader = (TextView) findViewById(R.id.text_bootloader);
		mRadio = (TextView) findViewById(R.id.text_radio);

		mManufacturer = (TextView) findViewById(R.id.text_manufacturer);
		mBrand = (TextView) findViewById(R.id.text_brand);
		mModel = (TextView) findViewById(R.id.text_model);
		mProduct = (TextView) findViewById(R.id.text_product);
		mDesign = (TextView) findViewById(R.id.text_design);
		mBoard = (TextView) findViewById(R.id.text_board);
		mSerialNumber = (TextView) findViewById(R.id.text_serial_number);
		mHardware = (TextView) findViewById(R.id.text_hardware);

		mConfiguration = getResources().getConfiguration();
		initDisplayFields();
		initNavigationFields();
		initUserPreferencesFields();
		initImsiFields();
		initAndroidSystemFields();
		initDeviceFields();
	}


	private void initDisplayFields() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mResolutionPx.setText(metrics.widthPixels + " x " + metrics.heightPixels + " px");
		mResolutionDp.setText((int) ((float) metrics.widthPixels / metrics.density) + " x " +
				(int) ((float) metrics.heightPixels / metrics.density) + " dp");
		mDensity.setText(metrics.density + " px/dp");
		mDpi.setText(
				metrics.densityDpi + " dpi; x = " + (int) metrics.xdpi + " dpi, y = " + (int) metrics.ydpi +
						" dpi");

		mResolutionQualifier
				.setText(mConfiguration.screenWidthDp + " x " + mConfiguration.screenHeightDp +
						" dp");
		mMinWidth.setText(mConfiguration.smallestScreenWidthDp + " dp");
		int screenSize = mConfiguration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		String sizeQualifier = "";
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			sizeQualifier = "small";
		} else if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			sizeQualifier = "normal";
		} else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			sizeQualifier = "large";
		} else if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			sizeQualifier = "xlarge";
		}
		mSizeQualifier.setText(sizeQualifier);

		String orientation = "";
		if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			orientation = "landscape";
		} else if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			orientation = "portrait";
		} else {
			orientation = "undefined";
		}
		mOrientation.setText(orientation);
	}

	private void initNavigationFields() {
		String touchscreen = "";
		if (mConfiguration.touchscreen == Configuration.TOUCHSCREEN_FINGER) {
			touchscreen = "finger";
		} else if (mConfiguration.touchscreen == Configuration.TOUCHSCREEN_NOTOUCH) {
			touchscreen = "no touch";
		} else {
			touchscreen = "undefined";
		}
		mTouchscreen.setText(touchscreen);

		String navigation = "";
		if (mConfiguration.navigation == Configuration.NAVIGATION_DPAD) {
			navigation = "dpad";
		} else if (mConfiguration.navigation == Configuration.NAVIGATION_NONAV) {
			navigation = "no navigation";
		} else if (mConfiguration.navigation == Configuration.NAVIGATION_TRACKBALL) {
			navigation = "trackball";
		} else if (mConfiguration.navigation == Configuration.NAVIGATION_WHEEL) {
			navigation = "wheel";
		} else {
			navigation = "undefined";
		}
		mNavigation.setText(navigation);
	}

	private void initUserPreferencesFields() {
		mLocale.setText(mConfiguration.locale.getDisplayName());
		mFontScale.setText(mConfiguration.fontScale + " x");
	}

	private void initImsiFields() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mImsi.setText(telephonyManager.getSubscriberId() == null ? "unknown" :
				telephonyManager.getSubscriberId());
		mMcc.setText(mConfiguration.mcc + "");
		mMnc.setText(mConfiguration.mnc + "");
	}

	private void initAndroidSystemFields() {
		mVersion.setText(Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + ", " + Build.DISPLAY);
		mApiLevel.setText(Build.VERSION.SDK_INT + "");
		mId.setText(Build.ID);
		mBuildTags.setText(Build.TAGS);
		mBootloader.setText(Build.BOOTLOADER);
		mRadio.setText(Build.getRadioVersion());
	}

	private void initDeviceFields() {
		mManufacturer.setText(Build.MANUFACTURER);
		mBrand.setText(Build.BRAND);
		mModel.setText(Build.MODEL);
		mProduct.setText(Build.PRODUCT);
		mDesign.setText(Build.DEVICE);
		mBoard.setText(Build.BOARD);
		mSerialNumber.setText(Build.SERIAL);
		mHardware.setText(Build.HARDWARE);
	}
}
