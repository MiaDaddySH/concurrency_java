package com.sheng.basic.makingTee;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

@Slf4j
public class MakingTee {

	public static void main(String[] args) {
		Thread t1 = new Thread(() -> {
			try {
				log.debug("洗水壶");
				sleep(1000);
				log.debug("烧水");
				sleep(5000);
			} catch (InterruptedException e){
				log.debug("InterruptedException");
			}
		}, "老王");

		Thread t2 = new Thread(() -> {
			try {
				log.debug("洗茶壶");
				sleep(1000);
				log.debug("拿茶叶");
				sleep(3000);
				t1.join();
				log.debug("泡茶");
			} catch (InterruptedException e){
				log.debug("InterruptedException");
			}
		}, "小王");
		t1.start();
		t2.start();
	}
}
