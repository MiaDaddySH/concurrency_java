package com.sheng.basic.threadmethods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用同一个线程演示六种状态：
 * <p>
 * NEW
 * → RUNNABLE
 * → BLOCKED
 * → RUNNABLE
 * → WAITING
 * → RUNNABLE
 * → TIMED_WAITING
 * → RUNNABLE
 * → TERMINATED
 * <p>
 * 注意：
 * 1. getState() 得到的是线程在某一瞬间的状态。
 * 2. BLOCKED 专门表示等待 synchronized 的 monitor 锁。
 * 3. WAITING 表示没有时间限制的等待。
 * 4. TIMED_WAITING 表示带有时间限制的等待。
 */
@Slf4j
public class ThreadStateExample {

	private static final Object MONITOR = new Object();

	public static void main(String[] args) throws InterruptedException {
		/*
		 * 控制 worker 什么时候结束 WAITING。
		 * worker 调用 await() 后进入 WAITING；main 调用 countDown() 后将其唤醒。
		 */
		CountDownLatch releaseWaiting = new CountDownLatch(1);
		// 控制 worker 什么时候离开自旋， 并尝试获得 MONITOR 锁。
		AtomicBoolean tryToAcquireLock = new AtomicBoolean(false);

		Thread worker = new Thread(() -> {
			try {
				//自旋期间，worker 一直在执行代码，因此保持 RUNNABLE。
				while (!tryToAcquireLock.get()) {
					Thread.onSpinWait();
				}

				/*
				 * main 此时持有 MONITOR 锁。
				 * worker 尝试进入 synchronized 时变成 BLOCKED。
				 */
				synchronized (MONITOR) {
					log.info("worker 获得 MONITOR 锁");
				}
				// 计数器目前是 1，await() 会让 worker无限期等待，因此进入 WAITING。
				releaseWaiting.await();
				//带时间的休眠使 worker 进入 TIMED_WAITING。
				TimeUnit.SECONDS.sleep(3);
				log.info("worker 完成所有任务");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("worker 被中断");
			}
		}, "state-worker");

		// 已创建但尚未启动：NEW
		logState("创建后", worker);
		worker.start();
		// worker 正在执行自旋代码：RUNNABLE
		waitForState(worker, Thread.State.RUNNABLE);
		logState("调用 start() 后", worker);
		/*
		 * main 先取得 MONITOR 锁，再通知 worker 尝试获取它。
		 * 因为锁被 main 持有，所以 worker 进入 BLOCKED。
		 */
		synchronized (MONITOR) {
			tryToAcquireLock.set(true);

			waitForState(worker, Thread.State.BLOCKED);
			logState("等待 synchronized 锁时", worker);
		}
		/*
		 * main 释放锁后，worker 获得锁并继续执行。
		 * 随后调用 releaseWaiting.await()，进入 WAITING。
		 */
		waitForState(worker, Thread.State.WAITING);
		logState("调用 await() 后", worker);

		// main 将计数器从 1 减为 0，唤醒 worker。
		releaseWaiting.countDown();

		// worker 被唤醒后调用 sleep(3)，因此进入 TIMED_WAITING。
		waitForState(worker, Thread.State.TIMED_WAITING);
		logState("调用 sleep() 后", worker);

		// main 等待 worker 的 run() 完全结束。
		worker.join();

		// run() 已结束：TERMINATED
		logState("run() 执行结束后", worker);
	}

	//轮询线程状态，直到观察到预期状态。
	private static void waitForState(
			Thread thread,
			Thread.State expectedState
	) throws InterruptedException {

		long deadline = System.nanoTime()
		                + TimeUnit.SECONDS.toNanos(2);

		while (thread.getState() != expectedState) {
			if (System.nanoTime() >= deadline) {
				throw new IllegalStateException(
						"未观察到预期状态：" + expectedState
						+ "，当前状态：" + thread.getState()
				);
			}

			TimeUnit.MILLISECONDS.sleep(10);
		}
	}

	private static void logState(
			String description,
			Thread thread
	) {
		log.info(
				"{}：线程名={}，状态={}",
				description,
				thread.getName(),
				thread.getState()
		);
	}
}