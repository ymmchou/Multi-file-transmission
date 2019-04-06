package edu.xupt.ymm.about_file.core;

import java.util.ArrayList;
import java.util.List;

public class TestSender {

	public static void main(String[] args) {
		String absolutePath = "E:\\";
		try {
			String filePath;
			int length;
			ReceivceFileSet fileSet = new ReceivceFileSet();
			ReceivceFileModel file;
			List<SendFileModel> fileList = new ArrayList<>();
			
			filePath = "test.mp4";
			length = 212329669;
			file = new ReceivceFileModel(
					filePath, length, absolutePath);
			fileSet.addReceivefile(1, file);
			
			long offset = 0;
			while (length > 0) {
				int len = length > ResourceSender.BUFFER_SIZE
						? ResourceSender.BUFFER_SIZE : length;
				SendFileModel sendFile = new SendFileModel()
						.setAbsoluatePath(absolutePath)
						.setFilePath(filePath)
						.setOffset(offset)
						.setLength(len);
				fileList.add(sendFile);
				length -= len;
				offset += len;
			}
			
			ResourceSenderCenter senderCenter = new ResourceSenderCenter(
					"192.168.1.186", 54120);
			senderCenter.setSendFileSet(fileSet);
			senderCenter.setSendlist(fileList);
			senderCenter.startSend();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
