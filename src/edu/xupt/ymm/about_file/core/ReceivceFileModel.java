package edu.xupt.ymm.about_file.core;

public class ReceivceFileModel {
	
	private String filepath;  //文件的相对路径
	private long length;  //文件的长度
	private String absoluatePath; //文件的绝对路径
	private UnReceivedFileBlock unReceivedFileBlock; //未发送的文件块
	
	ReceivceFileModel() {
	}

	ReceivceFileModel(String filepath, long length, String absoluatePath) {
		this.filepath = filepath;
		this.length = length;
		this.absoluatePath = absoluatePath;
		this.unReceivedFileBlock = new UnReceivedFileBlock(this.length);
	}

	UnReceivedFileBlock getUnReceivedFileBlock() {
		return unReceivedFileBlock;
	}

	String getFilepath() {
		return filepath;
	}

	long getLength() {
		return length;
	}

	String getAbsoluatePath() {
		return absoluatePath;
	}

	void setAbsoluatePath(String absoluatePath) {
		this.absoluatePath = absoluatePath;
	}
	
	public boolean equals(String fileName) {
		return filepath.equals(fileName);
	}
}
