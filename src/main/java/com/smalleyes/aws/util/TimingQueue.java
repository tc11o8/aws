package com.smalleyes.aws.util;

import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时集合
 * 
 */
public class TimingQueue<T extends Comparable<T>> {
	public static final Logger LOGGER = LoggerFactory.getLogger(TimingQueue.class);

	private ConcurrentSkipListSet<T> list = new ConcurrentSkipListSet<T>();

	public void addTask(T task) {
		list.add(task);
	}

	public T getFirstTask() {
		return list.pollFirst();
	}
	
	public int getSize() {
		return list.size();
	}

}
