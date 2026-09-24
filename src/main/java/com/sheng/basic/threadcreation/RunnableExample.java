package com.sheng.basic.threadcreation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableExample {
	public static void main(String[] args) {
		Runnable myRunnable = new MyRunnable();
		Thread thread = new Thread(myRunnable, "My Runnable");
		thread.start();

		log.info("Main thread is running");
	}

	static class MyRunnable implements Runnable {
		@Override
		public void run() {
			log.info("{}: is running", Thread.currentThread().getName());
		}
	}
}
