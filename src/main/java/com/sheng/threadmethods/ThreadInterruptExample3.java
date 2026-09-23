package com.sheng.threadmethods;

import lombok.extern.slf4j.Slf4j;

/**
 * 打断正在运行的线程，即调用线程的interrupt()方法，不会抛出InterruptedException异常，
 * 也不会改变线程的中断状态，只是设置线程的中断状态为true。
 */

@Slf4j
public class ThreadInterruptExample3 {
	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(() -> {
			while (true) {
				log.debug("running");
				boolean isInterrupted = Thread.currentThread().isInterrupted();
				if (isInterrupted) {
					log.debug("被打断了，退出循环");
					break;
				}
			}
		}, "t1");

		t1.start();

		Thread.sleep(1000);
		log.debug("interrupting t1");
		t1.interrupt();
	}
}
