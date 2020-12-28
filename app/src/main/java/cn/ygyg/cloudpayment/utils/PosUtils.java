package cn.ygyg.cloudpayment.utils;

public class PosUtils {
	public static String bytesToHexString(byte[] bytes, int offset, int len) {
		if (bytes == null) {
			return "null!";
		}

		StringBuilder ret = new StringBuilder(2 * len);

		for (int i = 0; i < len; i++) {
			int b = 0xF & bytes[(offset + i)] >> 4;
			ret.append("0123456789abcdef".charAt(b));
			b = 0xF & bytes[(offset + i)];
			ret.append("0123456789abcdef".charAt(b));
		}

		return ret.toString();
	}

	public static String bytesToHexString(byte[] bytes, int len) {
		return bytes == null ? "null!" : bytesToHexString(bytes, 0, len);
	}

	public static String bytesToHexString(byte[] bytes) {
		return bytes == null ? "null!" : bytesToHexString(bytes, bytes.length);
	}

	public static void delayms(int ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
			}
		}
	}

	public static byte[] hexStringToBytes(String s) {
		if (s == null) {
			return null;
		}

		int sz = s.length();
		try {
			byte[] ret = new byte[sz / 2];
			for (int i = 0; i < sz; i += 2) {
				ret[(i / 2)] = ((byte) (hexCharToInt(s.charAt(i)) << 4 | hexCharToInt(s
						.charAt(i + 1))));
			}

			return ret;
		} catch (RuntimeException re) {
		}
		return null;
	}

	public static int hexCharToInt(char c) {
		if ((c >= '0') && (c <= '9'))
			return c - '0';
		if ((c >= 'A') && (c <= 'F'))
			return c - 'A' + 10;
		if ((c >= 'a') && (c <= 'f')) {
			return c - 'a' + 10;
		}
		throw new RuntimeException("invalid hex char '" + c + "'");
	}

	public static byte[] stringToBcd(String src) {
		return stringToBcd(src, src != null ? src.length() : 0);
	}

	public static byte[] stringToBcd(String src, int numlen) {
		if (numlen % 2 != 0) {
			numlen++;
		}

		while (src.length() < numlen) {
			src = "0" + src;
		}

		byte[] bStr = new byte[src.length() / 2];
		char[] cs = src.toCharArray();
		int i = 0;
		int iNum = 0;

		for (i = 0; i < cs.length; i += 2) {
			int iTemp = 0;
			if ((cs[i] >= '0') && (cs[i] <= '9')) {
				iTemp = cs[i] - '0' << 4;
			} else {
				if ((cs[i] >= 'a') && (cs[i] <= 'f')) {
					int tmp126_124 = i;
					char[] tmp126_123 = cs;
					tmp126_123[tmp126_124] = ((char) (tmp126_123[tmp126_124] - ' '));
				}

				iTemp = cs[i] - '0' - 7 << 4;
			}

			if ((cs[(i + 1)] >= '0') && (cs[(i + 1)] <= '9')) {
				iTemp += cs[(i + 1)] - '0';
			} else {
				if ((cs[(i + 1)] >= 'a') && (cs[(i + 1)] <= 'f')) {
					int tmp213_212 = (i + 1);
					char[] tmp213_208 = cs;
					tmp213_208[tmp213_212] = ((char) (tmp213_208[tmp213_212] - ' '));
				}

				iTemp += cs[(i + 1)] - '0' - 7;
			}

			bStr[iNum] = ((byte) iTemp);
			iNum++;
		}

		return bStr;
	}

	public static String bcdToString(byte[] bcdNum) {
		return bcdToString(bcdNum, 0, bcdNum != null ? bcdNum.length : 0);
	}

	public static String bcdToString(byte[] bcdNum, int offset, int len) {
		if ((len <= 0) || (offset < 0) || (bcdNum == null)) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(Integer.toHexString((bcdNum[(i + offset)] & 0xF0) >> 4));
			sb.append(Integer.toHexString(bcdNum[(i + offset)] & 0xF));
		}

		return sb.toString();
	}

	public static byte[] intToBytesBe(int intValue) {
		byte[] bytes = new byte[4];

		for (int i = 0; i < bytes.length; i++) {
			bytes[(bytes.length - i - 1)] = ((byte) (intValue >> i * 8 & 0xFF));
		}

		return bytes;
	}
}
