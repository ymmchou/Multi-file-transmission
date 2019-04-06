package edu.xupt.ymm.about_file.core;

import java.net.Socket;
import java.util.List;

/**
 * 
 * @author 杨苗苗
 *
 */
public class ResourceSenderCenter {
	private String receiveIp;
	private int receiveport;
	private Socket socket;
	private ReceivceFileSet sendFileSet;
	private List<SendFileModel> sendlist;
	
	public ResourceSenderCenter(String receiveIp, int receiveport) {
		this.receiveIp = receiveIp;
		this.receiveport = receiveport;
	}

	public void setSendFileSet(ReceivceFileSet sendFileSet) {
		this.sendFileSet = sendFileSet;
	}

	public void setSendlist(List<SendFileModel> sendlist) {
		this.sendlist = sendlist;
	}

	//开始发送
	public void startSend() throws Exception {
		if(sendFileSet == null || sendlist == null) {
			return;
		}
		socket = new Socket(receiveIp, receiveport);
		new ResourceSender(socket, sendFileSet, sendlist);
	}
}
