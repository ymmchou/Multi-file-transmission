package edu.xupt.ymm.about_file.view;

import java.awt.Color;
import java.awt.Font;

public interface IResourceReceiveProgress {
	Font topicFont = new Font("微软雅黑", Font.BOLD, 30);
	Font normalFont = new Font("宋体", Font.PLAIN, 16);
	Font importantFont = new Font("黑体", Font.BOLD, 16);
	int normalFontSize = normalFont.getSize();
	int topicFontSize = topicFont.getSize() + 4;
	
	Color topicColor = new Color(5, 5, 209);
	Color titleColor = new Color(167, 82, 3);
	Color importantColor = new Color(255, 0, 0);
	
	int RECEIVE_PROGRESS_WIDTH = 400;
	int RECEIVE_PROGRESS_HEIGHT = 50;
	int PROGRESS_MIN_HEIGHT = 320;
	int PADDING = 5;
	
	long MIN_TIME_FOR_CUR_SPEED = 250;
	long MIN_TIME_FOR_TOT_SPEED = 500;
	
	void setSenderPlan(int receiveFileCount, long byteCount);
	void setSenderInfo(int senderCount);
	void startShowProgress();
	void acceptOneSender(String sender);
	void receiveNewFile(int fileId, String fileName, int fileLength);
	void receiveOneBlock(int fileId, int length);
	void finishedReceive();
}
