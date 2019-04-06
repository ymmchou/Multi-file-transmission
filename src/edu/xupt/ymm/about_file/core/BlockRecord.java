package edu.xupt.ymm.about_file.core;

public class BlockRecord {
	private long offset;
	private long length;
	
	BlockRecord(long offset, long length) {
		this.offset = offset;
		this.length = length;
	}

	long getOffset() {
		return offset;
	}

	long getLength() {
		return length;
	}

	@Override
	public String toString() {
		return offset + " : " + length;
	}

}
