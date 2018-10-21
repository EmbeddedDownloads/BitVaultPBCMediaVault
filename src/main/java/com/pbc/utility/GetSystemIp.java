package com.pbc.utility;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class GetSystemIp {
	private static final Logger logger = Logger.getLogger(GetSystemIp.class);

	private static String systemLocalIp = "192.168.11.120";

	public static void initializeIpLocal() {
		try {
			// final List<String> localIp = new ArrayList<>();
			// final Enumeration<NetworkInterface> n =
			// NetworkInterface.getNetworkInterfaces();
			// for (; n.hasMoreElements();) {
			// final NetworkInterface e = n.nextElement();
			// final Enumeration<InetAddress> a = e.getInetAddresses();
			// for (; a.hasMoreElements();) {
			// final InetAddress addr = a.nextElement();
			// localIp.add(addr.getHostAddress());
			// }
			// }
			// systemLocalIp = localIp.get(1);
			int count = 0;
			final StringBuilder ipAddress = new StringBuilder();

			final Process process = Runtime.getRuntime()
					.exec("wget -qO- http://instance-data/latest/meta-data/public-ipv4");

			final InputStream inputStream2 = process.getInputStream();
			while ((count = inputStream2.read()) != -1) {
				ipAddress.append((char) count);
			}
			logger.info("Calculated Local Ip address: " + ipAddress.toString());
			systemLocalIp = ipAddress.toString();
			logger.info("System Local Ip address: " + systemLocalIp);
		} catch (final Exception e) {

		}
	}

	public static String getSystemLocalIp() {
		return systemLocalIp;
	}

	public static void main(final String[] args) {
		GetSystemIp.initializeIpLocal();
	}

}
