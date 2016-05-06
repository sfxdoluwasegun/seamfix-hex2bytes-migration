package com.sf.hx.util;

public interface HexRepairInterface {

	/**
	 * Implementation to Convert Hex image format to the regular Byte Array
	 * 
	 * @param bytes
	 * @return
	 */
	byte[] convertHexToByte(byte[] bytes);

	boolean isInTheRightFormat(byte[] bytes);

}