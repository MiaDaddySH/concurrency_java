package com.sheng.basic.threadcreation;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j
public class CallableExample {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		MyCallable myCallable = new MyCallable();

		// Thread accepts a Runnable, but Callable does not implement Runnable.
		// Wrap the Callable in a FutureTask to run it in a Thread and retrieve its result.
		FutureTask<Integer> futureTask = new FutureTask<>(myCallable);
		Thread thread = new Thread(futureTask, "My Callable");
		thread.start();

		log.info("{} thread returns {}", thread.getName(), futureTask.get());
		log.info("Main thread is running");
	}

	static class MyCallable implements Callable<Integer> {
		@Override
		public Integer call() {
			log.info("{} is running", Thread.currentThread().getName());
			return 128;
		}
	}
}
