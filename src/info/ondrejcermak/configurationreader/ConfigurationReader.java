package info.ondrejcermak.configurationreader;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for reading some configuration info.
 */
public class ConfigurationReader implements KernelVersionReader {
	private static final String FILENAME_PROC_VERSION = "/proc/version";

	@Override
	public String getKernelVersion() {
		return getFormattedKernelVersion();
	}

	private String getFormattedKernelVersion() {
		try {
			return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(),
					"IO Exception when getting kernel version for Device Info screen",
					e);
			return null;
		}
	}

	private String formatKernelVersion(String rawKernelVersion) {
		// Example (see tests for more):
		// Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
		//     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
		//     Thu Jun 28 11:02:39 PDT 2012

		final String PROC_VERSION_REGEX =
				"Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
						"\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
						"(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
						"(#\\d+) " +              /* group 3: "#1" */
						"(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
						"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

		Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
		if (!m.matches()) {
			Log.e(getClass().getSimpleName(), "Regex did not match on /proc/version: " + rawKernelVersion);
			return "Unavailable";
		} else if (m.groupCount() < 4) {
			Log.e(getClass().getSimpleName(), "Regex match on /proc/version only returned " + m.groupCount()
					+ " groups");
			return "Unavailable";
		}
		return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
				m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
				m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
	}

	/**
	 * Reads a line from the specified file.
	 *
	 * @param filename the file to read from
	 * @return the first line, if any.
	 * @throws IOException if the file couldn't be read
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
