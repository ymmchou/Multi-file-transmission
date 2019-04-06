package edu.xupt.ymm.about_file.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import edu.xupt.ymm.about_file.view.IResourceReceiveProgress;

/**
 * @author 杨苗苗
 */

//接收方服务器
public class ResourceReceiverServer implements Runnable {
	private ServerSocket serverSocket;
	private int port;
	private volatile int sendcount;  //总的发送数量
	private boolean continueWaittingSender; //是否在继续等待发送
	private ReceivceFileSet receivceFileSet;  //发送文件的集合
	private ThreadPoolExecutor threadPool;
	private IResourceReceiveProgress receiveProgress;
	
	public ResourceReceiverServer() {
		this(0,null);
	}

	ResourceReceiverServer(int port,IResourceReceiveProgress receiveProgress) {
		threadPool = new ThreadPoolExecutor(50, 100, 500, 
				TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());
		this.port = port;
		this.receiveProgress = receiveProgress;
	}

	void setPort(int port) {
		this.port = port;
	}
	
	public void setSenderCount(int sendcount) {
		this.sendcount = sendcount;
		if (receiveProgress != null) {
			receiveProgress.setSenderInfo(sendcount);
		}
	}
	
	public ResourceReceiverServer setReceivceFileSet(ReceivceFileSet receivceFileSet) {
		this.receivceFileSet = receivceFileSet;
		
		return this;
	}

	
	public ResourceReceiverServer setSendcount(int sendcount) {
		this.sendcount = sendcount;
		
		return this;
	}

	ReceivceFileSet getReceivceFileSet() {
		return receivceFileSet;
	}
	
	public ResourceReceiverServer setReceiveFileSet(ReceivceFileSet receiveFileSet) {
		this.receivceFileSet = receiveFileSet;
		if (receiveProgress != null) {
			receiveProgress.setSenderPlan(
				receiveFileSet.getTotalReceiveFiles(), 
				receiveFileSet.getTotalReceiveBytes());
		}
		return this;
	}

	//启动服务器（接收方）
	public void setupReceiverServer() throws IOException {
		serverSocket = new ServerSocket(port);
		continueWaittingSender = true;
		Thread thread = new Thread(this, "fileRecevierServer");
		synchronized (ResourceReceiverServer.class) {
			thread.start();
			try {
				ResourceReceiverServer.class.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (receiveProgress != null) {
			receiveProgress.startShowProgress();
		}
	}

	@Override
	public void run() {
		int currentSenderCount = 0;
		synchronized (ResourceReceiverServer.class) {
			ResourceReceiverServer.class.notify();
		}
		
		if (receiveProgress != null) {
			boolean receiveProgressIsShow = false; 
			while (!receiveProgressIsShow) {
				receiveProgressIsShow = ((JDialog) receiveProgress).isActive();
			}
		}
		
		while(continueWaittingSender && currentSenderCount < sendcount) {
			try {
				Socket sender = serverSocket.accept();
				System.out.println("999999999999");
				
				if (receiveProgress != null) {
					String senderInfo = sender
							.getInetAddress()
							.getHostName();
					receiveProgress.acceptOneSender(senderInfo);
				}
				//new ResourceReceiver(this,sender,threadPool);
				new ResourceReceiver(this, sender, this.threadPool,
						receiveProgress);
				currentSenderCount++;
			} catch (IOException e) {
				
				e.printStackTrace();
				continueWaittingSender = false;
			}
			boolean threadRealFinished = threadPool.getActiveCount() <= 0;
			while(!threadRealFinished) {
				threadRealFinished = threadPool.getActiveCount() <= 0;
			}
			threadPool.shutdown();
			if(receiveProgress != null) {
				receiveProgress.finishedReceive();
			}
			System.out.println(System.currentTimeMillis());
			String unReceivedFileList = 
					receivceFileSet.getUnReceivedFileList();
			
			System.out.println(unReceivedFileList);
		}
	}
}
