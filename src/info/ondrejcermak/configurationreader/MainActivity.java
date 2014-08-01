package info.ondrejcermak.configurationreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
  public static final int DELAY_REFRESH_CURRENT_CPU_FREQ = 2000;
  private static final int MAX_UNDERSCORES_LENGTH = 50;
  private static final int MESSAGE_REFRESH_CURRENT_CPU_FREQ = 1;
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == MESSAGE_REFRESH_CURRENT_CPU_FREQ) {
        mHandler.removeMessages(MESSAGE_REFRESH_CURRENT_CPU_FREQ);
        mCpuCurFreq.setText(mCpuInfoReader.getProcessorCurrentFrequency());
        sendEmptyMessageDelayed(MESSAGE_REFRESH_CURRENT_CPU_FREQ, DELAY_REFRESH_CURRENT_CPU_FREQ);
      }
    }
  };
  private KernelVersionReader mKernelVersionReader;
  private ConnectivityReader mConnectivityReader;
  private CpuInfoReader mCpuInfoReader;
  private Configuration mConfiguration;
  private TextView mDeviceName;
  private TextView mResolutionPx;
  private TextView mResolutionDp;
  private TextView mResolutionUsable;
  private TextView mMinWidth;
  private TextView mDensity;
  private TextView mDpi;
  private TextView mSizeQualifier;
  private TextView mOrientation;
  private TextView mCpuType;
  private TextView mCpuCores;
  private TextView mCpuFeatures;
  private TextView mCpuManufacturer;
  private TextView mCpuArchitecture;
  private TextView mCpuRevision;
  private TextView mCpuMinFreq;
  private TextView mCpuMaxFreq;
  private TextView mCpuCurFreq;
  private TextView mTouchscreen;
  private TextView mNavigation;
  private TextView mKeyboard;
  private TextView mLocale;
  private TextView mFontScale;
  private TextView mImsi;
  private TextView mMcc;
  private TextView mMnc;
  private TextView mTelephony;
  private TextView mConnections;
  private TextView mNetworkInterfaces;
  private TextView mVersion;
  private TextView mApiLevel;
  private TextView mId;
  private TextView mKernel;
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
  private String mUknownText;
  private String mYesText;
  private String mNoText;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ConfigurationReader configurationReader = new ConfigurationReader(this);
    mKernelVersionReader = configurationReader;
    mConnectivityReader = configurationReader;
    mCpuInfoReader = configurationReader;
    mUknownText = getString(R.string.unknown);
    mYesText = getString(R.string.yes);
    mNoText = getString(R.string.no);

    mDeviceName = (TextView) findViewById(R.id.text_device_name);

    mResolutionPx = (TextView) findViewById(R.id.text_resolution_px);
    mResolutionDp = (TextView) findViewById(R.id.text_resolution_dp);
    mResolutionUsable = (TextView) findViewById(R.id.text_resolution_usable);
    mMinWidth = (TextView) findViewById(R.id.text_min_width);
    mDensity = (TextView) findViewById(R.id.text_density);
    mDpi = (TextView) findViewById(R.id.text_dpi);
    mSizeQualifier = (TextView) findViewById(R.id.text_size_qualifier);
    mOrientation = (TextView) findViewById(R.id.text_orientation);

    mCpuType = (TextView) findViewById(R.id.cpu_type);
    mCpuCores = (TextView) findViewById(R.id.cpu_cores);
    mCpuFeatures = (TextView) findViewById(R.id.cpu_features);
    mCpuManufacturer = (TextView) findViewById(R.id.cpu_manufacturer);
    mCpuArchitecture = (TextView) findViewById(R.id.cpu_architecture);
    mCpuRevision = (TextView) findViewById(R.id.cpu_revision);
    mCpuMinFreq = (TextView) findViewById(R.id.cpu_min_freq);
    mCpuMaxFreq = (TextView) findViewById(R.id.cpu_max_freq);
    mCpuCurFreq = (TextView) findViewById(R.id.cpu_cur_freq);

    mTouchscreen = (TextView) findViewById(R.id.text_touchscreen);
    mNavigation = (TextView) findViewById(R.id.text_navigation);
    mKeyboard = (TextView) findViewById(R.id.text_keyboard);

    mLocale = (TextView) findViewById(R.id.text_locale);
    mFontScale = (TextView) findViewById(R.id.text_font_scale);

    mImsi = (TextView) findViewById(R.id.text_imsi);
    mMcc = (TextView) findViewById(R.id.text_mcc);
    mMnc = (TextView) findViewById(R.id.text_mnc);

    mTelephony = (TextView) findViewById(R.id.text_telephony);
    mConnections = (TextView) findViewById(R.id.text_connections);
    mNetworkInterfaces = (TextView) findViewById(R.id.text_network_interfaces);

    mVersion = (TextView) findViewById(R.id.text_version);
    mApiLevel = (TextView) findViewById(R.id.text_api_level);
    mId = (TextView) findViewById(R.id.text_id);
    mKernel = (TextView) findViewById(R.id.text_kernel);
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
    mDeviceName.setText(Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL);
    initDisplayFields();
    initNavigationFields();
    initUserPreferencesFields();
    initImsiFields();
    initConnectivityFields();
    initAndroidSystemFields();
    initDeviceFields();
    initCpuFields();
  }

  @Override
  protected void onResume() {
    super.onPause();
    mHandler.sendEmptyMessage(MESSAGE_REFRESH_CURRENT_CPU_FREQ);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mHandler.removeMessages(MESSAGE_REFRESH_CURRENT_CPU_FREQ);
  }

  @SuppressLint("NewApi")
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    if (Build.VERSION.SDK_INT >= 14) {
      ShareActionProvider shareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share)
          .getActionProvider();
      shareActionProvider.setShareIntent(getShareIntent());
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_share:
        Intent shareIntent = getShareIntent();
        Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.menu_share));
        startActivity(chooserIntent);
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
    int widthDp = (int) ((float) metrics.widthPixels / metrics.density);
    int heightDp = (int) ((float) metrics.heightPixels / metrics.density);
    mResolutionDp.setText(widthDp + " x " + heightDp + " dp");
    mDensity.setText(metrics.density + " px/dp");
    mDpi.setText(metrics.densityDpi + " dpi; x = " + (int) metrics.xdpi + " dpi, " +
        "y = " + (int) metrics.ydpi +
        " dpi");
    mResolutionUsable.setText(getUsableResolution());
    mMinWidth.setText(getSmallestWidth(widthDp, heightDp));
    int screenSize = mConfiguration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    String sizeQualifier = "";
    if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
      sizeQualifier = getString(R.string.screensize_small);
    } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
      sizeQualifier = getString(R.string.screensize_normal);
    } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      sizeQualifier = getString(R.string.screensize_large);
    } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
      sizeQualifier = getString(R.string.screensize_xlarge);
    }
    mSizeQualifier.setText(sizeQualifier);

    String orientation = "";
    if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      orientation = getString(R.string.orientation_landscape);
    } else if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
      orientation = getString(R.string.oritentation_portrait);
    } else {
      orientation = mUknownText;
    }
    mOrientation.setText(orientation);
  }

  /**
   * Gets the usable resolution. On API < 13 it's the whole screen resolution.
   *
   * @return The usable resolution.
   */
  @SuppressLint("NewApi")
  private String getUsableResolution() {
    String usableResolution;
    if (Build.VERSION.SDK_INT >= 13) {
      usableResolution = mConfiguration.screenWidthDp + " x " + mConfiguration.screenHeightDp +
          " dp";
    } else {
      usableResolution = mResolutionDp.getText().toString();
    }
    return usableResolution;
  }

  /**
   * Gets the smallest width.
   *
   * @return The smallest width.
   */
  @SuppressLint("NewApi")
  private String getSmallestWidth(int widthDp, int heightDp) {
    String minWidth;
    if (Build.VERSION.SDK_INT >= 13) {
      minWidth = mConfiguration.smallestScreenWidthDp + " dp";
    } else {
      minWidth = mUknownText;
    }
    return minWidth;
  }

  private void initNavigationFields() {
    String touchscreen = "";
    if (mConfiguration.touchscreen == Configuration.TOUCHSCREEN_FINGER) {
      touchscreen = getString(R.string.touchscreen_finger);
    } else if (mConfiguration.touchscreen == Configuration.TOUCHSCREEN_NOTOUCH) {
      touchscreen = getString(R.string.touchscreen_no_touch);
    } else {
      touchscreen = mUknownText;
    }
    mTouchscreen.setText(touchscreen);

    String navigation = "";
    if (mConfiguration.navigation == Configuration.NAVIGATION_DPAD) {
      navigation = getString(R.string.navigation_dpad);
    } else if (mConfiguration.navigation == Configuration.NAVIGATION_NONAV) {
      navigation = getString(R.string.navigation_no_navigation);
    } else if (mConfiguration.navigation == Configuration.NAVIGATION_TRACKBALL) {
      navigation = getString(R.string.navigation_trackball);
    } else if (mConfiguration.navigation == Configuration.NAVIGATION_WHEEL) {
      navigation = getString(R.string.navigation_wheel);
    } else {
      navigation = mUknownText;
    }
    mNavigation.setText(navigation);

    String keyboard = "";
    if (mConfiguration.keyboard == Configuration.KEYBOARD_12KEY) {
      keyboard = getString(R.string.keyboard_12_keys);
    } else if (mConfiguration.keyboard == Configuration.KEYBOARD_QWERTY) {
      keyboard = getString(R.string.keyboard_querty);
    } else if (mConfiguration.keyboard == Configuration.KEYBOARD_NOKEYS) {
      keyboard = getString(R.string.keyboard_no_keyboard);
    } else {
      keyboard = mUknownText;
    }
    mKeyboard.setText(keyboard);
  }

  private void initUserPreferencesFields() {
    mLocale.setText(mConfiguration.locale.getDisplayName());
    mFontScale.setText(mConfiguration.fontScale + " x");
  }

  private void initImsiFields() {
    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    mImsi.setText(telephonyManager.getSubscriberId() == null ? mUknownText :
        telephonyManager.getSubscriberId());
    mMcc.setText(mConfiguration.mcc + "");
    mMnc.setText(mConfiguration.mnc + "");
  }

  private void initConnectivityFields() {
    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    mTelephony.setText(getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ?
            mYesText : mNoText);
    mConnections.setText(mConnectivityReader.getConnections());
    try {
      mNetworkInterfaces.setText(mConnectivityReader.getNetworkInterfaces());
    } catch (SocketException e) {
      Log.e(getClass().getSimpleName(), "Can't enumerate network interfaces.", e);
    }
  }

  private void initAndroidSystemFields() {
    mVersion.setText(Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + ", " + Build.DISPLAY);
    mApiLevel.setText(Build.VERSION.SDK_INT + "");
    mId.setText(Build.ID);
    String kernelVersion = mKernelVersionReader.getKernelVersion();

    mKernel.setText(kernelVersion == null ? mUknownText : kernelVersion);
    mBuildTags.setText(Build.TAGS);
    mBootloader.setText(Build.BOOTLOADER);
    mRadio.setText(getRadioVersion());
    mHost.setText(Build.HOST);
    mUser.setText(Build.USER);
  }

  /**
   * Gets the radio version.
   *
   * @return The radio version.
   */
  @SuppressLint("NewApi")
  private String getRadioVersion() {
    String radioVersion;
    if (Build.VERSION.SDK_INT >= 14) {
      radioVersion = TextUtils.isEmpty(Build.getRadioVersion()) ? mUknownText :
          Build.getRadioVersion();
    } else {
      radioVersion = Build.RADIO;
    }
    return radioVersion;
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

  private void initCpuFields() {
    mCpuType.setText(mCpuInfoReader.getProcessorType());
    mCpuCores.setText(mCpuInfoReader.getProcessorCores());
    mCpuFeatures.setText(mCpuInfoReader.getProcessorFeatures());
    mCpuManufacturer.setText(mCpuInfoReader.getProcessorManufacturer());
    mCpuArchitecture.setText(mCpuInfoReader.getProcessorArchitecture());
    mCpuRevision.setText(mCpuInfoReader.getProcessorRevision());
    mCpuMinFreq.setText(mCpuInfoReader.getProcessorMinFrequency());
    mCpuMaxFreq.setText(mCpuInfoReader.getProcessorMaxFrequency());
    mCpuCurFreq.setText(mCpuInfoReader.getProcessorCurrentFrequency());
  }

  private Intent getShareIntent() {
    StringBuilder text = createConfigurationText();

    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");

    String subject = getString(R.string.configuration_title,
        Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL);
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.putExtra(Intent.EXTRA_TITLE, subject);
    intent.putExtra(Intent.EXTRA_TEXT, text.toString());
    return intent;
  }

  private void saveToFile() {
    String state = Environment.getExternalStorageState();
    if (state.equals(Environment.MEDIA_MOUNTED)) {
      String name = "config_" + Build.MANUFACTURER.toUpperCase() + "_" + Build.MODEL + ".txt";
      name = name.replaceAll(" ", "_");
      File file = new File(Environment.getExternalStorageDirectory(), name);
      try {
        FileWriter writer = new FileWriter(file);
        writer.write(createConfigurationText().toString());
        writer.flush();
        writer.close();
        Toast.makeText(this, getString(R.string.configuration_saved, name), Toast.LENGTH_SHORT)
            .show();
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

    longestLineLength = Math.min(longestLineLength, MAX_UNDERSCORES_LENGTH);

    StringBuilder text = new StringBuilder();
    String header = getString(R.string.configuration_title,
        Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL);
    text.append(header);
    text.append("\n");
    for (int j = 0; j < header.length(); j++) {
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
        text.append(((TextView) row.getChildAt(1)).getText().toString().replaceAll("\n", " "));
        text.append("\n");
      }
    }
    return text;
  }
}
