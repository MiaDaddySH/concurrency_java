package com.sheng.basic.threadmethods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 演示守护线程（daemon thread）的特点。
 * <p>
 * 要点：
 * <p>
 * 1. Java 线程分为用户线程和守护线程。
 * <p>
 * 2. JVM 会等待所有用户线程执行结束，
 *    但不会因为还有守护线程运行而继续存活。
 * <p>
 * 3. 当所有用户线程都结束，只剩守护线程时，
 *    JVM 可以直接退出。
 * <p>
 * 4. setDaemon(true) 必须在 start() 之前调用，
 *    否则会抛出 IllegalThreadStateException。
 * <p>
 * 5. 守护线程被 JVM 终止时，不保证 finally 代码块执行。
 *    因此不应该用守护线程执行必须完成的数据保存、
 *    文件写入或数据库事务等重要任务。
 * <p>
 * 6. 守护线程并不代表低优先级线程。
 *    daemon 属性和线程优先级是两个不同的概念。
 */
@Slf4j
public class DaemonThreadExample {

	public static void main(String[] args) {
		Thread mainThread = Thread.currentThread();

		log.info(
				"{} 是否是守护线程：{}",
				mainThread.getName(),
				mainThread.isDaemon()
		);

		Thread daemonWorker = new Thread(() -> {
			int round = 1;

			try {
				while (true) {
					log.info(
							"守护线程正在执行第 {} 轮任务",
							round++
					);

					TimeUnit.MILLISECONDS.sleep(500);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("守护线程被中断");
			} finally {
				/*
				 * 当 JVM 因为只剩守护线程而退出时，
				 * 不保证这个 finally 代码块能够执行。
				 */
				log.info("守护线程执行清理操作");
			}
		}, "daemon-worker");

		Thread userWorker = new Thread(() -> {
			try {
				log.info("用户线程开始执行任务");

				TimeUnit.SECONDS.sleep(2);

				log.info("用户线程完成任务");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("用户线程被中断");
			}
		}, "user-worker");

		/*
		 * 必须在 start() 之前将线程设置为守护线程。
		 */
		daemonWorker.setDaemon(true);

		log.info(
				"{} 是否是守护线程：{}",
				daemonWorker.getName(),
				daemonWorker.isDaemon()
		);

		log.info(
				"{} 是否是守护线程：{}",
				userWorker.getName(),
				userWorker.isDaemon()
		);

		daemonWorker.start();
		userWorker.start();

		/*
		 * main 不等待 daemonWorker。
		 *
		 * main 结束后，userWorker 仍然是用户线程，
		 * 所以 JVM 会继续运行。
		 *
		 * 当 userWorker 也结束后，JVM 中只剩 daemonWorker，
		 * JVM 就会退出，不会等待 daemonWorker 的无限循环结束。
		 */
		log.info("main 线程执行结束");
	}
}