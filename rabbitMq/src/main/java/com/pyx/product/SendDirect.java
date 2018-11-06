package com.pyx.product;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/6 13:37
 */
public class SendDirect {

    private final static String EXCHANGE_NAME = "direct_logs";

    private final static String ROUTINGKEY = "delete";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("piyingxu");
        factory.setPassword("123456");
        factory.setVirtualHost("/java/mq");
        factory.setPort(5672);//默认端口
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        for (int i = 1; i < 100; i++) {
            String message = "Hello World!--" + ROUTINGKEY + "--" + i;
            channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            Thread.sleep(3000);
        }

        channel.close();
        connection.close();
    }
}
