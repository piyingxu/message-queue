package com.pyx.product;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/6 13:37
 */
public class SendSimple {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("piyingxu");
        factory.setPassword("123456");
        factory.setVirtualHost("/java/mq");
        factory.setPort(5672);//默认端口
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //定义一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        for (int i = 1; i < 100; i++) {
            String message = "Hello World!--" + i;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            Thread.sleep(2000);
        }

        channel.close();
        connection.close();
    }
}
