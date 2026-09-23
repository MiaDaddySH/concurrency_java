package com.sheng.threadmethods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 演示 Thread.yield() 的使用。
 * <p>
 * 要点：
 * <p>
 * 1. yield() 是静态方法，作用于当前正在执行的线程。
 * <p>
 * 2. 调用 yield() 表示当前线程愿意暂时让出 CPU，
 *    给其他处于 RUNNABLE 状态的线程一个运行机会。
 * <p>
 * 3. yield() 只是给线程调度器的提示，调度器可以忽略它。
 * <p>
 * 4. 调用 yield() 后，线程仍然是 RUNNABLE，
 *    不会进入 WAITING 或 TIMED_WAITING。
 * <p>
 * 5. yield() 不会释放已经持有的 synchronized 锁。
 * <p>
 * 6. 不能依赖 yield() 控制线程执行顺序。
 *    每次运行的日志顺序都可能不同。
 */
@Slf4j
public class ThreadYieldExample {

	public static void main(String[] args) throws InterruptedException {
		/*
		 * 让两个线程在尽可能接近的时间开始执行任务。
		 * 这个门闩只用于控制开始时间，与 yield() 本身无关。
		 */
		CountDownLatch startSignal = new CountDownLatch(1);

		Thread yieldingThread = new Thread(
				() -> runTask(startSignal, true),
				"yielding-thread"
		);

		Thread normalThread = new Thread(
				() -> runTask(startSignal, false),
				"normal-thread"
		);

		yieldingThread.start();
		normalThread.start();

		log.info("主线程发送开始信号");
		startSignal.countDown();

		yieldingThread.join();
		normalThread.join();

		log.info("两个线程都执行结束");
	}

	private static void runTask(
			CountDownLatch startSignal,
			boolean useYield
	) {
		try {
			startSignal.await();

			for (int round = 1; round <= 5; round++) {
				log.info(
						"{} 正在执行第 {} 轮",
						Thread.currentThread().getName(),
						round
				);

				doSomeWork();

				if (useYield) {
					log.info(
							"{} 调用 yield()，但不保证其他线程一定获得 CPU",
							Thread.currentThread().getName()
					);

					/*
					 * 当前线程向调度器表示：
					 * “我愿意暂时让出 CPU。”
					 *
					 * 但调度器可能：
					 * 1. 选择 normal-thread 运行；
					 * 2. 继续运行 yielding-thread；
					 * 3. 选择其他可运行线程。
					 */
					Thread.yield();
				}
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.warn(
					"{} 在等待开始信号时被中断",
					Thread.currentThread().getName()
			);
		}
	}

	/**
	 * 模拟一小段 CPU 计算任务。
	 */
	private static void doSomeWork() {
		long result = 0;

		for (int i = 0; i < 100_000; i++) {
			result += i;
		}

		// 防止示例中完全不使用计算结果。
		if (result < 0) {
			log.info("result={}", result);
		}
	}
}