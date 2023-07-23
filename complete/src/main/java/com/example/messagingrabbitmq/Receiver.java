package com.example.messagingrabbitmq;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

	private CountDownLatch latch = new CountDownLatch(1);

	public void receiveMessage(String message) {
		System.out.println("Received <" + message + "> date: " + new Date());
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
