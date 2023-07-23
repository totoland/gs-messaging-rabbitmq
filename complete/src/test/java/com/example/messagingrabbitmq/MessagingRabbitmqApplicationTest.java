/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.messagingrabbitmq;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.AMQP;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class MessagingRabbitmqApplicationTest {

	@MockBean
	private Runner runner;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	Queue queue() {
		return new Queue(MessagingRabbitmqApplication.queueName, false);
	}

	@Bean
	CustomExchange delayExchange() {
		Map<String, Object> args = new HashMap<>();
		args.put("x-delayed-type", "direct");
		return new CustomExchange(MessagingRabbitmqApplication.topicExchangeName, MessagingRabbitmqApplication.topicExchangeName, true, false, args);
	}

	@Bean
	Binding binding(Queue queue, CustomExchange delayExchange) {
		return BindingBuilder.bind(queue).to(delayExchange).with("foo.#").noargs();
	}

	@Test
	public void test() throws Exception {
		try {
			rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.topicExchangeName,
					"foo.#",(Object) ("Hello from RabbitMQ Delay Send when: " + new Date()), new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setDelay(1000);
					return message;
				}
			});
		}
		catch (AmqpConnectException e) {
			// ignore - rabbit is not running
		}

		System.out.println("Done!!");
	}

}
