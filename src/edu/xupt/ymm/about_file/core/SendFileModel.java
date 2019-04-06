package edu.xupt.ymm.about_file.core;
/**
 * 
 * @author 杨苗苗
 *
 */
public class SendFileModel {
	
	private String filePath;   //文件的路径
	private String absoluatePath; //文件的绝对路径
	private long offset; //文件的偏移量
	private int length; //文件的长度
	
	public SendFileModel() {
	}

	String getFilePath() {
		return filePath;
	}

	SendFileModel setFilePath(String filePath) {
		this.filePath = filePath;
		
		return this;
	}

	String getAbsoluatePath() {
		return absoluatePath;
	}

	SendFileModel setAbsoluatePath(String absoluatePath) {
		this.absoluatePath = absoluatePath;
		
		return this;
	}

	long getOffset() {
		return offset;
	}

	SendFileModel setOffset(long offset) {
		this.offset = offset;
		
		return this;
	}

	int getLength() {
		return length;
	}

	SendFileModel setLength(int length) {
		this.length = length;
		
		return this;
	}
}
