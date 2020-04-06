package cn.zhiu.framework.base.api.core.util;


/**
 * The type Byte utils.
 *
 * @author zhuzz
 * @time 2019 /04/02 15:04:32
 */
public class ByteUtils {

	private static final int two = 2;

	/**
	 * 将int型转换为4位字节数组
	 * 运算规则：高位到低位
	 *
	 * @param i the
	 *
	 * @return byte [ ]
	 *
	 * @author zhuzz
	 * @time 2019 /04/15 17:03:36
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		// 由高位到低位
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	/**
	 * 将4位byte数组转换成int型
	 * 运算规则：高位到低位
	 *
	 * @param bytes the bytes
	 *
	 * @return int
	 *
	 * @author zhuzz
	 * @time 2019 /04/15 17:03:53
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	public static String toHexString(byte[] block) {
		return arrayShortHexString(block);
	}

	public static String arrayHexString(byte[] src, String delim) {
		if (delim == null) {
			delim = "";
		}
		if (src == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length; i++) {
			byte byteNumber = src[i];
			sb.append(hexString(byteNumber));
			sb.append(delim);
		}
		String toPrint = sb.toString();
		int start = toPrint.length() - delim.length();
		if (delim.equals(toPrint.substring(start, toPrint.length()))) {
			toPrint = toPrint.substring(0, start);
		}
		return toPrint;
	}

	public static String arrayShortHexString(byte[] src) {
		return arrayHexString(src, null).replace("0x", "");
	}

	private static String hexString(byte byteNumber) {
		int toStr = byteNumber;
		if (byteNumber < 0) {
			toStr = byteNumber + 256;
		}
		String byteStr = Integer.toHexString(toStr);
		if (byteStr.length() == 1) {
			byteStr = "0" + byteStr;
		}
		return "0x" + byteStr.toUpperCase();
	}

	public static byte[] hexStringToBytes(String hexStr) throws Exception {
		int length = hexStr.length();
		if (length % two != 0) {
			throw new Exception(hexStr + "不是16进制数");
		}
		hexStr = hexStr.toUpperCase();
		byte[] outArray = new byte[length / two];
		for (int i = 0; i < length; i += two) {
			char li = hexStr.charAt(i);
			char lo = hexStr.charAt(i + 1);
			if (li < '0' || li > 'F' || lo < '0' || lo > 'F') {
				throw new Exception(null, null);
			}
			outArray[i / two] = (byte) Integer.parseInt(
					String.valueOf(new char[] { li, lo }), 16);
		}
		return outArray;
	}

	public static byte[] contactArray(byte[] src1, byte[] src2) throws Exception {
		if (src1 == null || src1 == null) {
			throw new Exception("contactArray,数据编码失败");
		}
		byte[] dest = new byte[src1.length + src2.length];
		System.arraycopy(src1, 0, dest, 0, src1.length);
		System.arraycopy(src2, 0, dest, src1.length, src2.length);
		return dest;
	}

}
