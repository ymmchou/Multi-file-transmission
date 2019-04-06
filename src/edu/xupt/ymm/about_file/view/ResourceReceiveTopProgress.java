package edu.xupt.ymm.about_file.view;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.xupt.ymm.about_file.until.ByteAndString;

public class ResourceReceiveTopProgress extends JDialog 
		implements IResourceReceiveProgress, Runnable {
	private static final long serialVersionUID = 5770797060849828896L;
	
	private Map<Integer, FileReceiveProgressPanel> fileReceiveMap;

	private Container container;
	private JLabel jlblTopic;
	private JLabel jlblReceivePlanFile;
	private JLabel jlblReceivePlanSender;
	private JLabel jlblReceiveAction;
	private JLabel jlblCurrSpeed;
	private JLabel jlblTotalSpeed;
	
	private FileReceiveProgressPanel frppSender;
	private FileReceiveProgressPanel frppFiles;
	
	private volatile int receiveFileCount;
	private volatile int currentReceiveFileCount;
	private volatile long startTime;
	private volatile long lastTime;
	private volatile long lastReceiveBytes;
	private volatile long currentReceiveBytes;
	private volatile boolean goon;
	private volatile Object lock;
	
	public ResourceReceiveTopProgress(Frame owner, String topic) {
		super(owner, topic, true);
		fileReceiveMap = new ConcurrentHashMap<>();
		lock = new Object();
		
		container = getContentPane();
		container.setLayout(new GridLayout(0, 1));
		setSize(RECEIVE_PROGRESS_WIDTH, PROGRESS_MIN_HEIGHT);
		setLocationRelativeTo(owner);
		
		jlblTopic = new JLabel(topic, JLabel.CENTER);
		jlblTopic.setFont(topicFont);
		jlblTopic.setForeground(topicColor);
		container.add(jlblTopic);
		
		// 接收计划
		JPanel jpnlReceivePlan = new JPanel(new GridLayout(3, 1));
		container.add(jpnlReceivePlan);
		
		JLabel jlblReceivePlanTitle = new JLabel("本次接收计划", JLabel.CENTER);
		jlblReceivePlanTitle.setFont(normalFont);
		jlblReceivePlanTitle.setForeground(titleColor);
		jpnlReceivePlan.add(jlblReceivePlanTitle);
		
		jlblReceivePlanFile = new JLabel("本次共接收F个文件，共B字节。", JLabel.LEFT);
		jlblReceivePlanFile.setFont(normalFont);
		jpnlReceivePlan.add(jlblReceivePlanFile);
		
		jlblReceivePlanSender = new JLabel("共S发送端。", JLabel.LEFT);
		jlblReceivePlanSender.setFont(normalFont);
		jpnlReceivePlan.add(jlblReceivePlanSender);
		// 发送端进度
		frppSender = new FileReceiveProgressPanel("发送端：", "", 1);
		container.add(frppSender);
		// 当前进行的接收动作
		jlblReceiveAction = new JLabel("尚未确定接收任务", 0);
		jlblReceiveAction.setFont(importantFont);
		jlblReceiveAction.setForeground(importantColor);
		container.add(jlblReceiveAction);
		// 文件接收进度
		frppFiles = new FileReceiveProgressPanel("接收文件：", "0/1", 1);
		container.add(frppFiles);
		
		JPanel jpnlSpeed = new JPanel(new GridLayout(1, 2));
		container.add(jpnlSpeed);

		jlblCurrSpeed = new JLabel("字节/秒");
		jlblCurrSpeed.setFont(normalFont);
		jpnlSpeed.add(jlblCurrSpeed);

		jlblTotalSpeed = new JLabel("字节/秒");
		jlblTotalSpeed.setFont(normalFont);
		jpnlSpeed.add(jlblTotalSpeed);
	}
	
	@Override
	public void acceptOneSender(String sender) {
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
			synchronized (lock) {
				lock.notify();
			}
		}
		frppSender.receiveOneDelta(1);
		frppSender.setContext(frppSender.getContext() + " " + sender);
		jlblReceiveAction.setText("接入一个发送者：" + sender);
	}

	@Override
	public void setSenderPlan(int receiveFileCount, long byteCount) {
		String planContext = jlblReceivePlanFile.getText();
		planContext = planContext.replace("F", String.valueOf(receiveFileCount));
		planContext = planContext.replace("B", String.valueOf(byteCount));
		jlblReceivePlanFile.setText(planContext);
		
		this.receiveFileCount = receiveFileCount;
		this.currentReceiveFileCount = 0;
		frppFiles.setContext(currentReceiveFileCount + "/" + receiveFileCount);
		
		jlblReceiveAction.setText("已确定发送任务计划！");
	}

	@Override
	public void setSenderInfo(int senderCount) {
		String planContext = jlblReceivePlanSender.getText();
		planContext = planContext.replace("S", String.valueOf(senderCount));
		jlblReceivePlanSender.setText(planContext);
		frppFiles.setMaxValue(receiveFileCount);
	}

	@Override
	synchronized public void receiveNewFile(int fileId, String fileName, int fileLength) {
		FileReceiveProgressPanel frpp = fileReceiveMap.get(fileId);
		if (frpp == null) {
			frpp = new FileReceiveProgressPanel("接收", fileName, fileLength);
			fileReceiveMap.put(fileId, frpp);
			setSize(RECEIVE_PROGRESS_WIDTH, getHeight() + RECEIVE_PROGRESS_HEIGHT);
			container.add(frpp);
			currentReceiveFileCount++;
			frppFiles.setContext(currentReceiveFileCount + "/" + receiveFileCount);
			frppFiles.receiveOneDelta(1);
		}
	}

	@Override
	synchronized public void receiveOneBlock(int fileId, int length) {
		FileReceiveProgressPanel frpp = fileReceiveMap.get(fileId);
		if (frpp.receiveOneDelta(length)) {
			container.remove(frpp);
			fileReceiveMap.remove(fileId, frpp);
			setSize(RECEIVE_PROGRESS_WIDTH, getHeight() - RECEIVE_PROGRESS_HEIGHT);
		}
		getReceiveSpeed(length);
	}
	
	private void getReceiveSpeed(int length) {
		currentReceiveBytes += length;
		long currentTime = System.currentTimeMillis();
		if (lastTime == 0) {
			lastTime = currentTime;
			jlblReceiveAction.setText("开始接收文件……");
			return;
		}
		// 计算接收速度
		long deltaTime = currentTime - lastTime;
		if (deltaTime > MIN_TIME_FOR_CUR_SPEED) {
			// 计算间隔时间
			long deltaByte = currentReceiveBytes - lastReceiveBytes;
			long curSpeed = deltaByte * 1000 / deltaTime;
			
			jlblCurrSpeed.setText("瞬时速度： " 
					+ ByteAndString.bytesToKMG(curSpeed)
					+ "B/秒");
			lastTime = currentTime;
			lastReceiveBytes = currentReceiveBytes;
		}
	}

	@Override
	public void finishedReceive() {
		this.dispose();
		goon = false;
	}

	@Override
	public void startShowProgress() {
		synchronized (lock) {
			try {
				new Thread(this).start();
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setVisible(true);
	}

	@Override
	public void run() {
		synchronized (lock) {
			try {
				lock.notify();
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (goon) {
			long currentTime = System.currentTimeMillis();
			long totalSpeed = currentReceiveBytes * 1000 / 
					(currentTime - startTime);
			jlblTotalSpeed.setText("平均速度：" 
					+ ByteAndString.bytesToKMG(totalSpeed) 
					+ "B/秒");
			synchronized (Class.class) {
				try {
					Class.class.wait(MIN_TIME_FOR_TOT_SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
