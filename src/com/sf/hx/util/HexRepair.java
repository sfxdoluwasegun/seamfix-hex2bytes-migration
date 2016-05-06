package com.sf.hx.util;

import org.apache.log4j.Logger;

public abstract class HexRepair implements HexRepairInterface {

	Logger logger = Logger.getLogger(HexRepair.class);

	/*
	 * convert the byte[] Image from Hex to Escape The byte[] would be returned
	 * in the right format, if byte[] already in the right format, it would be
	 * returned untouched
	 * 
	 * @Param byte[]
	 * 
	 * @see com.sf.hx.util.HexRepairInterface#convertHexToByte(byte[])
	 */
	@Override
	public byte[] convertHexToByte(byte[] bytes) {

		try {
			return HexToByte.getCorrectEscapeBytes(bytes);
		} catch (Exception e) {
			logger.debug("An Error Occurred while converting", e);
			return null;
		}
	}

	/*
	 * Checks if the byte[] image is in the right format, if its in the right
	 * format, true will be returned, if its not, false will be returned.
	 * 
	 * @Param byte[]
	 * 
	 * @see com.sf.hx.util.HexRepairInterface#convertHexToByte(byte[])
	 */
	@Override
	public boolean isInTheRightFormat(byte[] bytes) {

		try {
			byte[] originalByte = bytes;
			byte[] newByte = HexToByte.getCorrectEscapeBytes(bytes);

			if (originalByte == newByte) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.debug("An Error Occurred while converting", e);
			return false;
		}
	}

}
