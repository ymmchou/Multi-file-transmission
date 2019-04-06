package edu.xupt.ymm.about_file.core;

import java.util.LinkedList;
import java.util.List;

public class UnReceivedFileBlock {
	private List<BlockRecord> blockList;

	UnReceivedFileBlock(long fileLength) {
		blockList = new LinkedList<>();
		blockList.add(new BlockRecord(0, fileLength));
	}
	//获得一块发送的文件
	private int getTheBlock(long curOffset) throws Exception {
		int index = 0;
		
		for (index = 0; index < blockList.size(); index++) {
			BlockRecord org = blockList.get(index);
			if (org.getOffset() + org.getLength() >= curOffset) {
				return index;
			}
		}
		
		throw new Exception("块编号未找到:" + curOffset);
	}
	
	synchronized void receivedBlock(ResourceBlock block) throws Exception {
		long curOffset = block.getOffset();
		long curLength = block.getLength();
		int orgBlockIndex = getTheBlock(curOffset);
		BlockRecord orgBlock = blockList.get(orgBlockIndex);
		long orgOffset = orgBlock.getOffset();
		long orgLength = orgBlock.getLength();
		
		long leftOffset = orgOffset;
		long leftLength = curOffset - orgOffset;
		
		long rightOffset = curOffset + curLength;
		long rightLength = orgOffset + orgLength - curOffset - curLength;
		
		blockList.remove(orgBlockIndex);
		if (rightLength > 0) {
			blockList.add(orgBlockIndex, 
					new BlockRecord(rightOffset, rightLength));
		}
		if (leftLength > 0) {
			blockList.add(orgBlockIndex, 
					new BlockRecord(leftOffset, leftLength));
		}
	}

	boolean isReceivedOver() {
		System.out.println(blockList.isEmpty());
		return blockList.isEmpty();
	}
	
	List<BlockRecord> getBlockList() {
		System.out.println("UN" + blockList);
		return blockList;
	}
	
	
}
