package com.smalleyes.aws.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceThread implements Runnable {

	protected static final Logger log = LoggerFactory.getLogger(ServiceThread.class);

	protected final Thread thread;
	private volatile boolean stopped = false;

	public ServiceThread() {
		this.thread = new Thread(this, this.getServiceName());
	}

	public abstract String getServiceName();

	@PostConstruct
	public void start() {
		init();
		this.thread.start();
	}

	protected void init() {
	}

	public void stop() {
		this.stopped = true;
	}

	public void startAgain() {
		this.stopped = false;
	}

	public boolean isStopped() {
		return stopped;
	}
}
