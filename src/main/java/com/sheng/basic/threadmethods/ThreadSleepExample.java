package com.sheng.basic.threadmethods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 两种写法在这里都让当前执行的子线程休眠约 1 秒。
 * 区别主要在单位的表达：
 * Thread.sleep(1000) 的参数是毫秒，
 * TimeUnit.SECONDS.sleep(1) 明确写出了“秒”。
 * 还要注意，Thread.sleep() 是静态方法。
 * 即使写成 worker.sleep(1000)，休眠的也是调用它的当前线程，所以应始终写成 Thread.sleep(1000)。
 */

@Slf4j
public class ThreadSleepExample {
	public static void main(String[] args) throws InterruptedException {
		Thread worker = new Thread(() -> {
			try {
				log.info("开始：{}", Thread.currentThread().getName());

				// Sleep for 1,000 milliseconds.
				Thread.sleep(1000);
				log.info("Thread.sleep(1000) 结束");

				// Sleep for 1 second.
				TimeUnit.SECONDS.sleep(1);
				log.info("TimeUnit.SECONDS.sleep(1) 结束");

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("子线程在休眠时被中断");
			}
		}, "worker-thread");

		worker.start();
		log.info("主线程没有被子线程的 sleep 暂停");

		worker.join();
		log.info("子线程已结束");
	}
}
