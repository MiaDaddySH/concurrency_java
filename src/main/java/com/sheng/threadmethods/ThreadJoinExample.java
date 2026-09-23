package com.sheng.threadmethods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 演示 join() 如何等待另一个线程结束。
 *
 * 要点：
 * 1. 谁调用 join()，谁就进入等待；目标线程继续运行。
 * 2. join() 一直等待，直到目标线程结束。
 * 3. join(毫秒数) 最多等待约指定的时间；返回时目标线程可能仍在运行。
 * 4. 如果目标线程已经结束，join() 会立即返回。
 */
@Slf4j
public class ThreadJoinExample {
	public static void main(String[] args) throws InterruptedException {
		Thread worker = new Thread(() -> {
			try {
				log.info("子线程开始工作");
				TimeUnit.MILLISECONDS.sleep(1500);
				log.info("子线程完成工作");
			} catch (InterruptedException e) {
				Thread.currentThread()
				      .interrupt();
				log.warn("子线程被中断");
			}
		}, "worker-thread");

		worker.start();

		log.info("主线程先做自己的工作");

		// main 线程最多等待 worker 约 300 毫秒。
		worker.join(300);
		log.info("限时等待结束；子线程仍在运行：{}", worker.isAlive());

		// main 线程继续等待，直到 worker 结束。
		worker.join();
		log.info("完整等待结束；子线程仍在运行：{}", worker.isAlive());

		log.info("主线程继续执行后续代码");
	}
}
