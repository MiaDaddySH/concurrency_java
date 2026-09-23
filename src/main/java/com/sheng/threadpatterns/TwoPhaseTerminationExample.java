package com.sheng.threadpatterns;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 演示两阶段终止模式（Two-Phase Termination）。
 *</p>
 * 第一阶段：请求终止
 *</p>
 * 其他线程调用 interrupt()，向工作线程发出终止请求。
 * interrupt() 不会强制杀死线程，只会设置中断标记，
 * 或者打断线程当前的 sleep、wait、join 等阻塞操作。
 *</p>
 * 第二阶段：清理并终止
 *</p>
 * 工作线程检测到中断请求后：
 * 1. 停止接受或执行新的任务；
 * 2. 完成必要的清理工作；
 * 3. 正常退出 run() 方法。
 *</p>
 * 这种模式的优点：
 * 1. 不会在线程执行到一半时强制终止；
 * 2. 线程有机会释放资源和保存必要状态；
 * 3. 终止逻辑由工作线程自己控制；
 * 4. 避免使用不安全的 Thread.stop()。
 */
@Slf4j
public class TwoPhaseTerminationExample {

	public static void main(String[] args)
			throws InterruptedException {

		MonitorService monitorService = new MonitorService();

		monitorService.start();

		// 让监控线程运行一段时间。
		TimeUnit.SECONDS.sleep(4);

		//第一阶段： main 向监控线程发出终止请求。
		monitorService.requestStop();

		//main 等待监控线程完成清理并结束。
		monitorService.awaitTermination();

		log.info("监控服务已经安全终止");
	}

	private static class MonitorService {
		private final Thread monitorThread = new Thread(this::runMonitor, "monitor-thread");
		public void start() {
			monitorThread.start();
		}
		/**
		 * 第一阶段：发出终止请求。
		 * interrupt() 不会强制终止 monitorThread，而是通知它应该准备结束。
		 */
		public void requestStop() {
			log.info("第一阶段：请求终止监控线程");
			monitorThread.interrupt();
		}
		// 等待监控线程完全结束。
		public void awaitTermination() throws InterruptedException {
			monitorThread.join();
		}
		private void runMonitor() {
			Thread current = Thread.currentThread();

			try {
				while (!current.isInterrupted()) {
					try {
						/*
						 * 模拟周期性监控。
						 * sleep() 期间收到 interrupt()， 会清除中断标记并抛出InterruptedException
						 */
						TimeUnit.SECONDS.sleep(1);
						collectSystemInformation();
					} catch (InterruptedException e) {
						log.info("监控线程在休眠期间收到终止请求");
						//重新设置中断标记，让 while 条件在下一次检查时能够发现终止请求。
						current.interrupt();
					}
				}
			} finally {
				// 第二阶段：在线程退出之前执行必要的清理工作。
				performCleanup();
			}
		}

		private void collectSystemInformation() {
			Runtime runtime = Runtime.getRuntime();
			long usedMemory = runtime.totalMemory() - runtime.freeMemory();
			log.info("正在监控系统，已使用内存：{} MB", usedMemory / 1024 / 1024);
		}

		private void performCleanup() {
			log.info("第二阶段：开始执行清理工作");
			/*
			 * 这里可以执行：
			 * 1. 关闭非关键资源；
			 * 2. 保存必要的临时状态；
			 * 3. 清空内部队列；
			 * 4. 记录线程结束信息。
			 *
			 * 清理操作应该尽量简短，并避免无限期阻塞。
			 */
			log.info("第二阶段：清理工作完成");
		}
	}
}