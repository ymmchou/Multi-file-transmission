package edu.xupt.ymm.about_file.until;

public class ByteAndString {
	public static final String hex = "0123456789ABCDEF";	
	
	public static String bytesToKMG(long speed) {
		StringBuffer str = new StringBuffer();
		
		if (speed < 1024) {
			return String.valueOf(speed);
		}
		if (speed < (1 << 20)) {
			str.append(speed >> 10)
			.append('.')
			.append(String.valueOf((speed & 0x03FF) + 1000).substring(1))
			.append('K');
			return str.toString();
		}
		if (speed < (1 << 30)) {
			str.append(speed >> 20)
			.append('.')
			.append(String.valueOf(((speed >> 10) & 0x03FF) + 1000).substring(1))
			.append('M');
			return str.toString();
		}
		if (speed < (1 << 40)) {
			str.append(speed >> 30)
			.append('.')
			.append(String.valueOf(((speed >> 20) & 0x03FF) + 1000).substring(1))
			.append('G');
			return str.toString();
		}
		
		return String.valueOf(speed);
	}
	
	public static String toHex(byte[] buffer) {
		StringBuffer result = new StringBuffer();
		
		for (int i = 0; i < buffer.length; i++) {
			byte bv = buffer[i];
			result.append(i == 0 ? "" : ' ')
			.append(hex.charAt((bv >> 4) & 0x0F))
			.append(hex.charAt(bv & 0x0F));
		}
		
		return result.toString();
	}
	
	public static void setIntAt(byte[] buffer, int offset, int value) {
		buffer[offset + 0] = (byte) ((value >> 24) & 0x00FF);
		buffer[offset + 1] = (byte) ((value >> 16) & 0x00FF);
		buffer[offset + 2] = (byte) ((value >> 8) & 0x00FF);
		buffer[offset + 3] = (byte) (value & 0x00FF);
	}
	
	public static void setLongAt(byte[] buffer, int offset, long value) {
		buffer[offset + 0] = (byte) ((value >> 56) & 0x00FF);
		buffer[offset + 1] = (byte) ((value >> 48) & 0x00FF);
		buffer[offset + 2] = (byte) ((value >> 40) & 0x00FF);
		buffer[offset + 3] = (byte) ((value >> 32) & 0x00FF);
		buffer[offset + 4] = (byte) ((value >> 24) & 0x00FF);
		buffer[offset + 5] = (byte) ((value >> 16) & 0x00FF);
		buffer[offset + 6] = (byte) ((value >> 8) & 0x00FF);
		buffer[offset + 7] = (byte) (value & 0x00FF);
	}
	
	public static int getIntAt(byte[] buffer, int offset) {
		int value = 0;
		
		value |= (buffer[offset + 0] << 24) & 0xFF000000;
		value |= (buffer[offset + 1] << 16) & 0x00FF0000;
		value |= (buffer[offset + 2] << 8) & 0x0000FF00;
		value |= (buffer[offset + 3]) & 0x000000FF;
		
		return value;
	}
	
	public static long getLongAt(byte[] buffer, int offset) {
		long value = 0;
		
		value |= (long) (buffer[offset + 0] << 56) & 0xFF00000000000000L;
		value |= (long) (buffer[offset + 1] << 48) & 0x00FF000000000000L;
		value |= (long) (buffer[offset + 2] << 40) & 0x0000FF0000000000L;
		value |= (long) (buffer[offset + 3] << 32) & 0x000000FF00000000L;
		value |= (long) (buffer[offset + 4] << 24) & 0x00000000FF000000L;
		value |= (long) (buffer[offset + 5] << 16) & 0x0000000000FF0000L;
		value |= (long) (buffer[offset + 6] << 8) & 0x000000000000FF00L;
		value |= (long) (buffer[offset + 7]) & 0x00000000000000FFL;
		
		return value;
	}
	
}
