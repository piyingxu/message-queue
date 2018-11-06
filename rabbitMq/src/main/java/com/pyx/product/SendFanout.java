package com.pyx.product;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/6 13:37
 */
public class SendFanout {

    private final static String EXCHANGE_NAME = "log";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("piyingxu");
        factory.setPassword("123456");
        factory.setVirtualHost("/java/mq");
        factory.setPort(5672);//默认端口
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //发送消息到一个名为“logs”的exchange上，使用“fanout”方式发送，即广播消息，不需要使用queue，发送端不需要关心谁接收。
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        for (int i = 1; i < 100; i++) {
            String message = "Hello World!--" + i;
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes()); //routingKey不能为null，可以为""
            System.out.println(" [x] Sent '" + message + "'");
            Thread.sleep(3000);
        }

        channel.close();
        connection.close();
    }
}
