package info.ondrejcermak.configurationreader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Utility class for reading some configuration info.
 */
public class ConfigurationReader implements KernelVersionReader, ConnectivityReader, CpuInfoReader {
	private static final String FILENAME_PROC_VERSION = "/proc/version";
	private static final String FILENAME_PROC_CPUINFO = "/proc/cpuinfo";

  private Context mContext;

  /**
   * Creates new instance.
   *
   * @param context The context to read some values from.
   */
  public ConfigurationReader(Context context) {
    mContext = context.getApplicationContext();
  }

  @Override
	public String getKernelVersion() {
		return getFormattedKernelVersion();
	}

	@Override
	public String getConnections() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		StringBuilder connections = new StringBuilder();
		for (NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
			String typeName;
			switch (info.getType()) {
				case ConnectivityManager.TYPE_WIFI:
					typeName = "wifi";
					break;
				case ConnectivityManager.TYPE_WIMAX:
					typeName = "wimax";
					break;
				case ConnectivityManager.TYPE_BLUETOOTH:
					typeName = "bluetooth";
					break;
				case ConnectivityManager.TYPE_DUMMY:
					typeName = "dummy";
					break;
				case ConnectivityManager.TYPE_ETHERNET:
					typeName = "ethernet";
					break;
				case ConnectivityManager.TYPE_MOBILE:
					typeName = "mobile";
					break;
				case ConnectivityManager.TYPE_MOBILE_DUN:
					typeName = "mobile dun";
					break;
				case ConnectivityManager.TYPE_MOBILE_HIPRI:
					typeName = "mobile hipri";
					break;
				case ConnectivityManager.TYPE_MOBILE_MMS:
					typeName = "mobile mms";
					break;
				case ConnectivityManager.TYPE_MOBILE_SUPL:
					typeName = "mobile supl";
					break;
				case ConnectivityReader.TYPE_MOBILE_FOTA:
					typeName = "mobile fota";
					break;
				case ConnectivityReader.TYPE_MOBILE_CBS:
					typeName = "mobile cbs";
					break;
				case ConnectivityReader.TYPE_MOBILE_IMS:
					typeName = "mobile ims";
					break;
				case ConnectivityReader.TYPE_WIFI_P2P:
					typeName = "wifi p2p";
					break;
				default:
					typeName = "unknown";
			}
			connections.append(typeName);
			if (info.isConnected()) {
				connections.append(" [").append(mContext.getString(R.string.connected)).append("]");
			} else if (!info.isAvailable()) {
				connections.append(" [").append(mContext.getString(R.string.not_available)).append("]");
			}
			connections.append(",\n");
		}
		if (connections.length() > 2) {
			connections.delete(connections.length() - 2, connections.length());
		}
		return connections.toString();
	}

	@Override
	public String getNetworkInterfaces() throws SocketException {
		StringBuilder networks = new StringBuilder();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		if (networkInterfaces != null) {
			for (NetworkInterface network : Collections.list(networkInterfaces)) {
				networks.append(network.getDisplayName());
				networks.append(network.isLoopback() ? " loop" : "");
				networks.append(network.isVirtual() ? " virtual" : "");
				networks.append(network.isPointToPoint() ? " p2p" : "");
				networks.append(network.isUp() ? " <up>" : " <down>");
				appendIpAddresses(networks, network);
				appendHwAddress(networks, network);
				networks.append(",\n");
			}
			if (networks.length() > 2) {
				networks.delete(networks.length() - 2, networks.length());
			}
		}
		return networks.toString();
	}

	/**
	 * Appends list of ip addresses.
	 *
	 * @param networks The builder to append it to.
	 * @param network The network to get the ip addresses from.
	 */
	private void appendIpAddresses(StringBuilder networks, NetworkInterface network) {
		StringBuilder addresses = new StringBuilder();
		for (InterfaceAddress address : network.getInterfaceAddresses()) {
			boolean allZeroes = true;

			String formattedAddress = address.getAddress().getHostAddress();
			int suffix = formattedAddress.indexOf("%");
			if (suffix > 0) {
				formattedAddress = formattedAddress.substring(0, suffix);
			}
			formattedAddress += "/" + address.getNetworkPrefixLength();
			addresses.append(formattedAddress).append(", ");
		}
		if (addresses.length() > 2) {
			addresses.delete(addresses.length() - 2, addresses.length());
		}
		if (addresses.length() > 0) {
			networks.append(" (").append(addresses.toString().toLowerCase()).append(")");
		}
	}

	/**
	 * Appends hw address.
	 *
	 * @param networks The builder to append it to.
	 * @param network The network to get the hw address from.
	 * @throws SocketException if the hw address can't be read.
	 */
	private void appendHwAddress(StringBuilder networks, NetworkInterface network)
			throws SocketException {
		StringBuilder formattedAddress = new StringBuilder();
		if (network.getHardwareAddress() != null) {
			for (byte octet : network.getHardwareAddress()) {
				formattedAddress.append(String.format("%02X", octet)).append(":");
			}
			if (formattedAddress.length() > 1) {
				formattedAddress.delete(formattedAddress.length() - 1, formattedAddress.length());
			}
			networks.append(" [").append(formattedAddress.toString().toLowerCase()).append("]");
		}
	}

	private String getFormattedKernelVersion() {
		try {
			return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(),
					"IO Exception when getting kernel version for Device Info screen", e);
			return null;
		}
	}

	private String formatKernelVersion(String rawKernelVersion) {
		// Example (see tests for more):
		// Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
		//     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
		//     Thu Jun 28 11:02:39 PDT 2012

		final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
				"\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
				"(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
				"(#\\d+) " +              /* group 3: "#1" */
				"(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
				"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

		Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
		if (!m.matches()) {
			Log.e(getClass().getSimpleName(),
					"Regex did not match on /proc/version: " + rawKernelVersion);
			return "Unavailable";
		} else if (m.groupCount() < 4) {
			Log.e(getClass().getSimpleName(),
					"Regex match on /proc/version only returned " + m.groupCount() + " groups");
			return "Unavailable";
		}
		return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
				m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
				m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
	}

	@Override
	public String getProcessorType() {
    String cpuType = mContext.getString(R.string.unknown);
		try {
      String PROC_CPUINFO_PROCESSOR_TYPE_REGEX = "Processor\\s*:\\s*(.*)";
      Pattern cpuTypePattern = Pattern.compile(PROC_CPUINFO_PROCESSOR_TYPE_REGEX);

			List<String> cpuInfo = readFile(FILENAME_PROC_CPUINFO);
      for(String line: cpuInfo) {
        Matcher matcher = cpuTypePattern.matcher(line);
        if(matcher.matches()) {
          cpuType = matcher.group(1);
          break;
        }
      }
		} catch (IOException e) {
      // Do nothing.
		}

    return cpuType;
	}

  @Override
  public String getCores() {
    int cores = 0;
    try {
      String PROC_CPUINFO_CORES_REGEX = "processor\\s*:\\s*(\\d+)";
      Pattern coresPattern = Pattern.compile(PROC_CPUINFO_CORES_REGEX);

      List<String> cpuInfo = readFile(FILENAME_PROC_CPUINFO);
      for(String line: cpuInfo) {
        Matcher matcher = coresPattern.matcher(line);
        if(matcher.matches()) {
          cores++;
        }
      }
    } catch (IOException e) {
      // Do nothing.
    }
    return cores > 0 ? String.valueOf(cores) : mContext.getString(R.string.unknown);
  }

  @Override
  public String getFeatures() {
    String features = mContext.getString(R.string.unknown);
    try {
      String PROC_CPUINFO_FEATURES_REGEX = "Features\\s*:\\s*(.*)";
      Pattern featuresPattern = Pattern.compile(PROC_CPUINFO_FEATURES_REGEX);

      List<String> cpuInfo = readFile(FILENAME_PROC_CPUINFO);
      for(String line: cpuInfo) {
        Matcher matcher = featuresPattern.matcher(line);
        if(matcher.matches()) {
          features = matcher.group(1);
          break;
        }
      }
    } catch (IOException e) {
      // Do nothing.
    }

    return features;
  }

  /**
	 * Reads all lines from the specified file.
	 *
	 * @param filename The file to read from.
	 * @return The list of lines.
	 * @throws IOException if the file couldn't be read.
	 */
  private static List<String> readFile(String filename) throws IOException {
    List<String> lines = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
    try {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        lines.add(line);
      }
    } finally {
      reader.close();
    }
    return lines;
  }

	/**
	 * Reads a line from the specified file.
	 *
	 * @param filename The file to read from
	 * @return The first line, if any.
	 * @throws IOException if the file couldn't be read.
	 */
	private static String readLine(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
		try {
			return reader.readLine();
		} finally {
			reader.close();
		}
	}
}
