package com.pyx.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/6 13:38
 */
public class RecvDirect {

    private final static String EXCHANGE_NAME = "direct_logs";

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

        //该语句得到一个随机名称的Queue，该queue的类型为non-durable、exclusive、auto-delete的，将该queue绑定到上面的exchange上接收消息。
        String queueName = channel.queueDeclare().getQueue();
        //从该exchange上只接收routing key指定的消息。
        String routingKey = "delete";
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(consumerTag + "--Customer Received '" + message + "'--" + envelope.getRoutingKey() + "--" + JSON.toJSONString(properties)  + "--" + queueName);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //自动回消息的时候可以指定应答模式,当自动应答等于true的时候，表示当消费者一收到消息就表示消费者收到了消息，消费者收到了消息就会立即从队列中删除。
        channel.basicConsume(queueName, true, consumer);
    }
}
