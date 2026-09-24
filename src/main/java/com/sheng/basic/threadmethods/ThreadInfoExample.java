package com.sheng.basic.threadmethods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadInfoExample {
	public static void main(String[] args) throws InterruptedException {
		logCurrentThread();

		Thread worker = new Thread(ThreadInfoExample::logCurrentThread);
		worker.setName("worker-thread"); // 在启动前设置线程名

		log.info("准备启动线程：name={}, id={}", worker.getName(), worker.getId());

		worker.start();
		worker.join();
	}

	private static void logCurrentThread() {
		Thread current = Thread.currentThread();
		log.info("当前线程: name={}, id={}", current.getName(), current.getId());
	}
}
