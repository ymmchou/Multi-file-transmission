package edu.xupt.ymm.about_file.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author 杨苗苗
 */

public class ReceivceFileSet {
	//以文件编号为键，文件数据类为值，组成一个集合
	private Map<Integer, ReceivceFileModel> filemap; 
	private long totalReceiveBytes; //总的发送字节数
	
	ReceivceFileSet() {
		filemap = new ConcurrentHashMap<>();
	}
	
	//获取文件的编号
	int getfileId(String fileName) {
		for(int fileId : filemap.keySet()){
			ReceivceFileModel reFileModel = filemap.get(fileId);
			if(reFileModel.equals(fileName)) {
				return fileId;
			}
		}
		return -1;
	}
	
	//获得没有发送成功的文件
	String getUnReceivedFileList() {
		StringBuffer res = new StringBuffer();
		
		for(int fileId : filemap.keySet()) {
			ReceivceFileModel rfm = filemap.get(fileId);
			res.append(fileId).append(':')
			.append(rfm.getAbsoluatePath())
			.append(rfm.getFilepath());
						
			List<BlockRecord> blockList = rfm.getUnReceivedFileBlock()
					.getBlockList();
			for(BlockRecord br : blockList) {
				res.append("\n\t").append(br);
			}
			res.append('\n');
		}
		return res.toString();
	}
	
	//添加一个接收的文件
	void addReceivefile(Integer fileId,ReceivceFileModel file) 
			throws ReceivefileidEistedExption {
		ReceivceFileModel orgfile = filemap.get(fileId);
		if(orgfile != null) {
			throw new ReceivefileidEistedExption("[文件编号"+ fileId
					+ "已存在！]");
		}
		totalReceiveBytes += file.getLength();
		filemap.put(fileId, file);
	}
	
	long getTotalReceiveBytes() {
		return totalReceiveBytes;
	}
	
	int getTotalReceiveFiles() {
		return filemap.size();
	}
	ReceivceFileModel getReceiveFile(Integer fileId) {
		
		return filemap.get(fileId);
	}
}
