package edu.xupt.ymm.about_file.core;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

import edu.xupt.ymm.about_file.view.IResourceReceiveProgress;
import edu.xupt.ymm.about_file.view.ResourceReceiveTopProgress;

public class TestReceiveProgress {
	private JFrame jfrmMainView;
	private Container container;
	
	private JButton jbtnOk;
	private ReceivceFileSet fileSet;
	
	public TestReceiveProgress() {
		init();
		dealAction();
	}
	
	void init() {
		jfrmMainView = new JFrame("关于进度条");
		jfrmMainView.setSize(500, 400);
		jfrmMainView.setLocationRelativeTo(null);
		jfrmMainView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container = jfrmMainView.getContentPane();
		container.setLayout(null);
		
		jbtnOk = new JButton("开始");
		jbtnOk.setFont(new Font("宋体", Font.PLAIN, 14));
		jbtnOk.setBounds(300, 300, 65, 40);
		container.add(jbtnOk);
		String absolutePath = "D:\\";
		
			String filePath = "133.mp4";
		try {
			int length = 406805780;
			fileSet = new ReceivceFileSet();
			ReceivceFileModel file = new ReceivceFileModel(
					filePath, length, absolutePath);
			fileSet.addReceivefile(1, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void dealAction() {
		jbtnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IResourceReceiveProgress rrp = new ResourceReceiveTopProgress(
						jfrmMainView, " 接收文件 ");
				ResourceReceiverServer rrs = new ResourceReceiverServer(54120,rrp);
				rrs.setReceivceFileSet(fileSet);
				rrs.setSendcount(1);
				try {
					rrs.setupReceiverServer();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public void showView() {
		jfrmMainView.setVisible(true);
	}
	
	void closeView() {
		jfrmMainView.dispose();
	}
	
}
