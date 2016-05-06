package com.sf.hx.util;

import org.apache.log4j.Logger;

import com.sf.hx.slave.Worker;

class HexToByte {

	/**
	 * Implementation to do the Conversion from Hex to Byte[] If byte is already
	 * in the right format, it returns it like that
	 * 
	 * @param bytes
	 * @return
	 * @Author Ezewuzi Okafor, Jaohar Added the try and catch and made it return
	 *         null if
	 */
	protected static byte[] getCorrectEscapeBytes(byte[] bytes) {

		byte[] returnBytes = null;

		try {
			String str = new String(bytes);

			if ((str.length() % 2) != 1) {
				throw new IllegalArgumentException("Invalid byte array argument. The length should be odd");
			}

			str = str.substring(1);

			returnBytes = new byte[1 + (str.length() / 2)];

			int y = -1;

			returnBytes[0] = (byte) y;

			for (int x = 0; x < str.length(); x = x + 2) {
				String hexPair = str.charAt(x) + "" + str.charAt(x + 1);
				int someVal = Integer.parseInt(hexPair, 16);
				returnBytes[1 + (x / 2)] = (byte) someVal;
			}
		} catch (Exception e) {
			returnBytes = bytes;
			Logger.getLogger(Worker.class).error("Could not convert Byte, Byte already in the right format ");
		}

		return returnBytes;
	}

}
