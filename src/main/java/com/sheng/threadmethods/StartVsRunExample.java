package com.sheng.threadmethods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartVsRunExample {
	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(() ->
				log.info("执行 run() 的线程：{}",
						Thread.currentThread().getName()),
				"worker-thread"
		);

		log.info("直接调用 run()");
		thread.run();    // 普通方法调用：由 main 线程执行

		log.info("调用 start()");
		thread.start();  // 启动新线程，由 worker-thread 执行 run(); 同一个 Thread 对象的 start() 只能调用一次。

		thread.join();   // 等待 worker-thread 执行完毕
		log.info("示例结束");
	}
}
