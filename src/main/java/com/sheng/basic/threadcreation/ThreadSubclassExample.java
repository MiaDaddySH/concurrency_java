package com.sheng.basic.threadcreation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadSubclassExample {
	public static void main(String[] args) {
		MyThread myThread = new MyThread();
		myThread.start();
		myThread.setName("My Thread");

		log.info("Main thread is running");
	}

	static class MyThread extends Thread {
		@Override
		public void run() {
			log.info("{} is running", Thread.currentThread().getName());
		}
	}
}
