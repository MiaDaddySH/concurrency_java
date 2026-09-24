package com.sheng.basic.samplesForLock;
//第一步 创建资源类，定义属性和操作方法
class Ticket{
    private int number = 30;
    public synchronized void sale(){
        if(number>0){
            System.out.println(Thread.currentThread().getName()+"卖出第"+(number--)+"票，剩余："+number);
        }
    }
}
//第二步 创建多个线程，调用资源类的方法
public class TicketSale {
	public static void main(String[] args) {
		Ticket ticket = new Ticket();
		//创建三个线程来模拟三个卖票窗口，来同时卖票。
		for (int i = 1; i < 4; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 40; i++) {
						ticket.sale();
					}
				}
			}, "窗口"+i).start();
		}
	}
}
