package edu.xupt.ymm.about_file.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import edu.xupt.ymm.about_file.until.ByteAndString;
import edu.xupt.ymm.about_file.view.IResourceReceiveProgress;

/**
 * @author 杨苗苗
 */
//接收方
public class ResourceReceiver implements Runnable {
	
	private Socket sender;
	private DataInputStream dis;
	private ResourceReceiverServer receiverServer; 
	private ThreadPoolExecutor threadPoolExecutor;
	private IResourceReceiveProgress receiveProgress;
	static final ThreadLocal<Integer> threadFileId 
		= new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return -1;
		}
	};
	
	private volatile Object lock;
	public static final int BUFFER_SIZE = 1 << 15;
	
	ResourceReceiver(ResourceReceiverServer receiverServer , 
			Socket sender ,ThreadPoolExecutor threadPoolExecutor,
			IResourceReceiveProgress receiveProgress)
				throws IOException {
		this.receiverServer = receiverServer;
		this.sender = sender;
		this.threadPoolExecutor = threadPoolExecutor;
		this.receiveProgress = receiveProgress;
		dis = new DataInputStream(sender.getInputStream());
		lock = new Object();
		
		synchronized (lock) {
			try {
				this.threadPoolExecutor.execute(this);
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		try {
			if(dis != null)
				dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			dis = null;
		}
		try {
			if(sender != null && !sender.isClosed())
				sender.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			sender = null;
		}
	}
	
	@Override
	public void run() {
		boolean finished = false;
		synchronized (lock) {
			lock.notify();
		}
		
		while(!finished) {
			try {
				finished = recevierOneBlock();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				finished = true;
			}
		}
		close();
		
	}

	private boolean recevierOneBlock() throws IOException {
		byte[] header = receiveBytes(16);
		int fileId = ByteAndString.getIntAt(header, 0);
		long offset = ByteAndString.getLongAt(header, 4);
		int length = ByteAndString.getIntAt(header, 12);
		
		if(fileId == -1) {
			return true;
		}
		if (receiveProgress != null) {
			int oldFileId = threadFileId.get();
			if (oldFileId != fileId) {
				threadFileId.set(fileId);
				ReceivceFileModel rfm = receiverServer
						.getReceivceFileSet()
						.getReceiveFile(fileId);
				String fileName = rfm.getFilepath();
				int fileLength = (int) rfm.getLength();
				receiveProgress.receiveNewFile(fileId, fileName, fileLength);
			}
		}
		byte[] buffer = receiveBytes(length);
		ResourceBlock resourceBlock = new ResourceBlock(
				receiverServer.getReceivceFileSet(),
				this.threadPoolExecutor, 
				receiveProgress);
		resourceBlock.setFileId(fileId);
		resourceBlock.setOffset(offset);
		resourceBlock.setLength(length);
		resourceBlock.setContent(buffer);
		resourceBlock.startWriteOut(); //真正的接收的写入
		
		return false;
	}
	
	private byte[] receiveBytes(int length) throws IOException {
		byte[] buffer = new byte[length];
		int realReceiveLength = 0;
		int offset = 0;
		
		while(length > 0) {
			//接收头部
			realReceiveLength = dis.read(buffer, offset, length);
			offset += realReceiveLength;
			length -= realReceiveLength;
		}
		System.out.println("buffer" + buffer);
		return buffer;
	}
}
