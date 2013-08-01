package info.ondrejcermak.configurationreader;

import java.net.SocketException;

import android.content.Context;

/**
 * Reader for the connectivity items.
 */
public interface ConnectivityReader {
	/**
	 * Over the air Adminstration.
	 */
	public static final int TYPE_MOBILE_FOTA = 10;

	/**
	 * IP Multimedia Subsystem
	 */
	public static final int TYPE_MOBILE_IMS = 11;

	/**
	 * Carrier Branded Services
	 */
	public static final int TYPE_MOBILE_CBS = 12;

	/**
	 * A Wi-Fi p2p connection. Only requesting processes will have access to the peers connected.
	 */
	public static final int TYPE_WIFI_P2P = 13;

	/**
	 * Gets the line-break separated list of connections.
	 *
	 * @return The line-break separated list of connections.
	 */
	public String getConnections();

	/**
	 * Gets the line-break separated list of network interfaces.
	 *
	 * @return The line-break separated list of network interfaces.
	 * @throws SocketException if the interfaces can't be read.
	 */
	public String getNetworkInterfaces() throws SocketException;
}
