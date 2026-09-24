package com.sheng.basic.threadmethods;

import lombok.extern.slf4j.Slf4j;

/**
 * 打断处于阻塞的线程，也就是调用了sleep()、wait()或join()方法的线程。
 * 它们被打断后打断标志会重置。
 */
@Slf4j
public class ThreadInterruptExample2 {
	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(() -> {
			log.debug("sleep ......");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.debug("interrupted {}", e.getMessage());
			}
		}, "work thread");

		thread.start();

		Thread.sleep(1000);
		log.debug("interrupt thread");
		thread.interrupt();
		log.debug("thread interrupted flag: {}", thread.isInterrupted());
	}
}
