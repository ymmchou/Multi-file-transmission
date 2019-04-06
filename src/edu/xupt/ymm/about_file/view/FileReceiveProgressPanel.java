package edu.xupt.ymm.about_file.view;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class FileReceiveProgressPanel extends JPanel {
	private static final long serialVersionUID = -5204525073717791610L;

	private JLabel jlblContext;
	private JProgressBar jpgbBar;
	private JLabel jlblFileNameCaption;
	private int count;
	private int currentCount;
	
	public FileReceiveProgressPanel(String caption, String context, int count) {
		this.count = count;
		this.currentCount = 0;
		this.setLayout(new GridLayout(2, 1));
		
		JPanel jpnlCaption = new JPanel();
		add(jpnlCaption);
		
		jlblFileNameCaption = new JLabel(caption);
		jlblFileNameCaption.setFont(IResourceReceiveProgress.normalFont);
		jpnlCaption.add(jlblFileNameCaption);
		
		jlblContext = new JLabel(context);
		jlblContext.setFont(IResourceReceiveProgress.normalFont);
		jpnlCaption.add(jlblContext);
		
		jpgbBar = new JProgressBar();
		jpgbBar.setFont(IResourceReceiveProgress.normalFont);
		jpgbBar.setMaximum(this.count);
		jpgbBar.setValue(currentCount);
		jpgbBar.setStringPainted(true);
		add(jpgbBar);
	}
	
	void setMaxValue(int count) {
		jpgbBar.setMaximum(count);
	}
	
	void setContext(String context) {
		jlblContext.setText(context);
	}
	
	String getContext() {
		return jlblContext.getText();
	}
	
	void setCaption(String caption) {
		jlblFileNameCaption.setText(caption);
	}
	
	boolean receiveOneDelta(int delta) {
		currentCount += delta;
		jpgbBar.setValue(currentCount);
		
		return currentCount >= count;
	}

}
