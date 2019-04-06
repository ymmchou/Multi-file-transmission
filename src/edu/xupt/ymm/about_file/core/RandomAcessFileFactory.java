package edu.xupt.ymm.about_file.core;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RandomAcessFileFactory {
	private static Map<String, ThreadLocal<RandomAccessFile>> threadLocalRaffile;
	
	static {
		threadLocalRaffile = new ConcurrentHashMap<>();
	}
	
	public static RandomAccessFile getRandomFile(String path) {
		if(!threadLocalRaffile.containsKey(path)) {
			ThreadLocal<RandomAccessFile> randomFile =
					new ThreadLocal<RandomAccessFile>() {

						@Override
						protected RandomAccessFile initialValue() {
							RandomAccessFile randomFile  = null;
							try {
								 randomFile = new RandomAccessFile(path, "rws");
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							
							return randomFile;
						}
			};
			threadLocalRaffile.put(path, randomFile);
		}
		return threadLocalRaffile.get(path).get();
	}
}
