package info.ondrejcermak.configurationreader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Printer;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends Activity {
	private Configuration mConfiguration;
	private ShareActionProvider mShareActionProvider;

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
	private TextView mKeyboard;

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
	private TextView mHost;
	private TextView mUser;

	private TextView mManufacturer;
	private TextView mBrand;
	private TextView mModel;
	private TextView mProduct;
	private TextView mDesign;
	private TextView mBoard;
	private TextView mSerialNumber;
	private TextView mHardware;
	private TextView mInstructionSet;

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
		mKeyboard = (TextView) findViewById(R.id.text_keyboard);

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
		mHost = (TextView) findViewById(R.id.text_host);
		mUser = (TextView) findViewById(R.id.text_user);

		mManufacturer = (TextView) findViewById(R.id.text_manufacturer);
		mBrand = (TextView) findViewById(R.id.text_brand);
		mModel = (TextView) findViewById(R.id.text_model);
		mProduct = (TextView) findViewById(R.id.text_product);
		mDesign = (TextView) findViewById(R.id.text_design);
		mBoard = (TextView) findViewById(R.id.text_board);
		mSerialNumber = (TextView) findViewById(R.id.text_serial_number);
		mHardware = (TextView) findViewById(R.id.text_hardware);
		mInstructionSet = (TextView) findViewById(R.id.text_instruction_set);

		mConfiguration = getResources().getConfiguration();
		initDisplayFields();
		initNavigationFields();
		initUserPreferencesFields();
		initImsiFields();
		initAndroidSystemFields();
		initDeviceFields();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share)
				.getActionProvider();
		mShareActionProvider.setShareIntent(getShareIntent());
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_share:
				getShareIntent();
				return true;
			case R.id.menu_save:
				saveToFile();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void initDisplayFields() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mResolutionPx.setText(metrics.widthPixels + " x " + metrics.heightPixels + " px");
		mResolutionDp.setText((int) ((float) metrics.widthPixels / metrics.density) + " x " +
				(int) ((float) metrics.heightPixels / metrics.density) + " dp");
		mDensity.setText(metrics.density + " px/dp");
		mDpi.setText(metrics.densityDpi + " dpi; x = " + (int) metrics.xdpi + " dpi, " +
				"y = " + (int) metrics.ydpi +
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

		String keyboard = "";
		if (mConfiguration.keyboard == Configuration.KEYBOARD_12KEY) {
			keyboard = "12 keys";
		} else if (mConfiguration.keyboard == Configuration.KEYBOARD_QWERTY) {
			keyboard = "querty";
		} else if (mConfiguration.keyboard == Configuration.KEYBOARD_NOKEYS) {
			keyboard = "no keyboard";
		} else {
			keyboard = "undefined";
		}
		mKeyboard.setText(keyboard);
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
		mRadio
				.setText(TextUtils.isEmpty(Build.getRadioVersion()) ? "unknown" : Build.getRadioVersion
						());
		mHost.setText(Build.HOST);
		mUser.setText(Build.USER);
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
		mInstructionSet.setText(Build.CPU_ABI + ", " + Build.CPU_ABI2);
	}

	private Intent getShareIntent() {
		StringBuilder text = createConfigurationText();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");

		String subject = "Configuration of " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL;
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TITLE, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text.toString());
		return intent;
	}

	private void saveToFile()  {
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)) {
			String name = "config_" + Build.MANUFACTURER.toUpperCase() + "_" + Build.MODEL + ".txt";
			name.replaceAll(" ", "_");
			File file = new File(Environment.getExternalStorageDirectory(), name);
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(createConfigurationText().toString());
				writer.flush();
				writer.close();
				Toast.makeText(this, getString(R.string.configuration_saved, name), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Log.e("ConfigReader", "Can't write to file.", e);
				Toast.makeText(this, R.string.error_saving_file, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, R.string.error_storage_not_available, Toast.LENGTH_SHORT).show();
		}
	}

	private StringBuilder createConfigurationText() {
		TableLayout table = (TableLayout) findViewById(R.id.table);
		int longestLineLength = 0;
		int longestLabelLength = 0;
		for (int i = 0; i < table.getChildCount(); i++) {
			TableRow row = (TableRow) table.getChildAt(i);
			if (row.getChildCount() == 2) {
				int labelLength = ((TextView) row.getChildAt(0)).getText().length();
				if (labelLength > longestLabelLength) {
					longestLabelLength = labelLength;
				}

				int lineLength = labelLength + ((TextView) row.getChildAt(1)).getText().length() + 2;
				if (lineLength > longestLineLength) {
					longestLineLength = lineLength;
				}
			}
		}

		StringBuilder text = new StringBuilder();
		String header = "Configuration of " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL;
		text.append(header);
		text.append("\n");
		for (int j = 0; j < longestLineLength; j++) {
			text.append("=");
		}
		text.append("\n\n");

		for (int i = 0; i < table.getChildCount(); i++) {
			TableRow row = (TableRow) table.getChildAt(i);
			if (row.getChildCount() == 1) {
				if (i > 0) {
					text.append("\n");
				}
				text.append(((TextView) row.getChildAt(0)).getText());
				text.append("\n");
				for (int j = 0; j < longestLineLength; j++) {
					text.append("-");
				}
				text.append("\n");
			} else {
				CharSequence label = ((TextView) row.getChildAt(0)).getText();
				text.append(label);
				int labelLength = label.length();
				for (int j = 0; j <= longestLabelLength - labelLength; j++) {
					text.append(" ");
				}
				text.append(((TextView) row.getChildAt(1)).getText());
				text.append("\n");
			}
		}
		return text;
	}
}
