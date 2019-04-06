package edu.xupt.ymm.about_file.core;

import java.io.RandomAccessFile;
import java.util.concurrent.ThreadPoolExecutor;

import edu.xupt.ymm.about_file.view.IResourceReceiveProgress;

/** 
 * @author 杨苗苗
 */

//发送一块文件的类
public class ResourceBlock implements Runnable {
	
	private int fileId;  //文件的编号
	private long offset; //文件的偏移量
	private int length; //每次发送文件的长度
	private byte[] content; //文件的内容
	private ReceivceFileSet receivceFileSet;
	private ThreadPoolExecutor threadPoolExecutor;
	private IResourceReceiveProgress receiveProgress;

	ResourceBlock(ReceivceFileSet receivceFileSet, 
			ThreadPoolExecutor threadPoolExecutor,
			IResourceReceiveProgress receiveProgress) {
		this.receivceFileSet = receivceFileSet;
		this.threadPoolExecutor = threadPoolExecutor;
		this.receiveProgress = receiveProgress;
	}

	int getFileId() {
		return fileId;
	}

	ResourceBlock setFileId(int fileId) {
		this.fileId = fileId;
		
		return this;
	}

	long getOffset() {
		return offset;
	}

	ResourceBlock setOffset(long offset) {
		this.offset = offset;
		
		return this;
	}

	int getLength() {
		return length;
	}

	ResourceBlock setLength(int length) {
		this.length = length;
		
		return this;
	}

	byte[] getContent() {
		return content;
	}

	ResourceBlock setContent(byte[] content) {
		this.content = content;
		
		return this;
	}
	
	//启动线程
	void startWriteOut() {
		threadPoolExecutor.execute(this);
	}
	void writeBlock(ReceivceFileModel receiveFile) throws Exception {
		if (receiveFile == null) {
			throw new Exception("文件号[" + fileId + "]不存在！");
		}
		String absolutePath = receiveFile.getAbsoluatePath();
		String filePath = receiveFile.getFilepath();
		RandomAccessFile raf = RandomAcessFileFactory.getRandomFile(absolutePath + filePath);
		
		raf.seek(offset);
		raf.write(content);
		System.out.println(offset + ":" + length);
		
		//raf.close();
		
		if (receiveProgress != null) {
			receiveProgress.receiveOneBlock(fileId, content.length);
		}
	}

	@Override
	public void run() {
		try {
			ReceivceFileModel rfm = receivceFileSet.getReceiveFile(fileId);
			writeBlock(rfm);
			rfm.getUnReceivedFileBlock().receivedBlock(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
