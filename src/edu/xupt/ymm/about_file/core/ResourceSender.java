package edu.xupt.ymm.about_file.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.List;

import edu.xupt.ymm.about_file.until.ByteAndString;
/**
 * @author 杨苗苗
 */
public class ResourceSender implements Runnable {
	public static final int BUFFER_SIZE = 1 << 15;
	
	private Socket socket;
	private DataOutputStream dos;
	private List<SendFileModel> senderlist;  //发送文件的列表
	private ReceivceFileSet receivceFileSet;  //文件的集合
	
	ResourceSender(Socket socket,ReceivceFileSet receivceFileSet,
			 List<SendFileModel> senderlist) throws IOException {
		this.socket = socket;
		this.receivceFileSet = receivceFileSet;
		this.senderlist = senderlist;
		dos = new DataOutputStream(socket.getOutputStream());
		new Thread(this, "ResourceSender").start();
	}

	void close() {
		try {
			if (dos != null) {
				dos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dos = null;
		}
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket = null;
		}
	}
	
	@Override
	public void run() {
		if(senderlist == null) {
			return;
		}
		
		byte[] header = new byte[16]; //头部
		byte[] buffer = new byte[BUFFER_SIZE]; //一个线程只有一份，发送方只有一份
		//分成两部分：1、文件头部（int类型的文件编号、long类型的偏移量、int类型的长度）
		//		  2、文件的内部内容
		for(SendFileModel sendfile : senderlist) {
			String filepath = sendfile.getFilePath();
			String absoluatepath = sendfile.getAbsoluatePath();
			
			int fileId = receivceFileSet.getfileId(filepath);
			long offset = sendfile.getOffset();
			int length = sendfile.getLength();
			
			//把要发送的东西转化成二进制
			ByteAndString.setIntAt(header, 0, fileId);
			ByteAndString.setLongAt(header, 4, offset);
			ByteAndString.setIntAt(header, 12, length);
			
			try {
				dos.write(header); //发送头部
				dos.flush();
				
				//打开这个文件
				RandomAccessFile raf = new RandomAccessFile(absoluatepath + filepath ,"r");
				raf.seek(offset);//跳转到偏移量的位子
				
				int len = 0;
				int readRealLen = 0;
				while(length > 0) {
					//三步判断为了防止最后一次发送的长度不是规定的长度
					readRealLen = length >= BUFFER_SIZE ? BUFFER_SIZE : length;
					//读多少，一块一块的读，所以顺序没问题TCP协议
					len = raf.read(buffer, 0, readRealLen);
					//根据上面读的写，读多少写多少，代码中只执行一次
					System.out.println("len" + len);
					dos.write(buffer, 0, len);
					length -= len;
				}
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
				close();
				break;
			}
		}
		
		//最后再发送一次
		ByteAndString.setIntAt(header, 0, -1);
		ByteAndString.setLongAt(header, 4, 0);
		ByteAndString.setIntAt(header, 12, 0);
		
		try {
			if(dos != null) {
				dos.write(header);
				dos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		close();
	}

}
