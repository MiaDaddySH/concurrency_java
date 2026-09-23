package com.sheng.threadmethods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 演示 Thread 的中断机制。
 * <p>
 * interrupt()：
 *   向目标线程发出中断请求，不会强制终止线程。
 * <p>
 * isInterrupted()：
 *   查看某个线程的中断标记，不清除标记。
 * <p>
 * Thread.interrupted()：
 *   静态方法，查看当前线程的中断标记，并清除标记。
 */
@Slf4j
public class ThreadInterruptExample {
	public static void main(String[] args) throws InterruptedException {
		demonstrateInterruptFlag();
		demonstrateInterruptedSleep();
	}

	private static void demonstrateInterruptFlag() throws InterruptedException {
		Thread worker = new Thread(() -> {
			Thread current = Thread.currentThread();

			// 给当前线程设置中断标记。
			current.interrupt();

			// isInterrupted() 只读取标记，多次调用仍然得到 true。
			log.info("第一次 isInterrupted()：{}", current.isInterrupted());
			log.info("第二次 isInterrupted()：{}", current.isInterrupted());

			// interrupted() 读取并清除「当前线程」的中断标记。
			log.info("第一次 Thread.interrupted()：{}", Thread.interrupted());
			log.info("第二次 Thread.interrupted()：{}", Thread.interrupted());
			log.info("清除后的 isInterrupted()：{}", current.isInterrupted());
		}, "flag-worker");

		worker.start();
		worker.join();
	}

	private static void demonstrateInterruptedSleep() throws InterruptedException {
		CountDownLatch ready = new CountDownLatch(1);

		Thread worker = new Thread(() -> {
			try {
				log.info("准备休眠");
				ready.countDown();

				// 休眠期间收到中断，会抛出 InterruptedException。
				TimeUnit.SECONDS.sleep(10);
				log.info("正常休眠结束");

			} catch (InterruptedException e) {
				// 抛出 InterruptedException 时，中断标记会被清除。所以这里的isInterrupted就是false。
				log.info("捕获异常后 isInterrupted()：{}",
						Thread.currentThread().isInterrupted());

				// 当前方法无法向外抛出这个受检异常，因此恢复中断标记，
				// 让后续代码仍能知道这个线程收到过中断请求。
				Thread.currentThread().interrupt();
				log.info("恢复标记后 isInterrupted()：{}",
						Thread.currentThread().isInterrupted());
			}
		}, "sleep-worker");

		worker.start();
		ready.await(); // 确保子线程已经运行到准备休眠的位置。

		log.info("主线程向 sleep-worker 发出中断请求");
		worker.interrupt();
		worker.join();
	}
}
