package com.sheng.basic.samplesForLock;

import java.util.concurrent.locks.ReentrantLock;

//第一步 创建资源类，定义属性和操作方法
class TicketWithLock{
	private int number = 30;
	private final ReentrantLock lock = new ReentrantLock();
	public void sale() {
		lock.lock();
		try {
			if (number > 0) {
				System.out.println(Thread.currentThread()
				                         .getName() + "卖出第" + (number--) + "票，剩余：" + number);
			}
		}finally {
			lock.unlock();
		}
	}
}
//第二步 创建多个线程，调用资源类的方法
public class TicketSaleWithLock {
	public static void main(String[] args) {
		TicketWithLock ticket = new TicketWithLock();
		//创建三个线程来模拟三个卖票窗口，来同时卖票。
		for (int j = 1; j < 4; j++) {
			new Thread(() -> {
				for (int i = 0; i < 40; i++) {
					ticket.sale();
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}, "窗口"+j).start();
		}
	}
}
