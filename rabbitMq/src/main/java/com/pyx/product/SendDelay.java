package com.pyx.product;

import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/16 11:26
 */
public class SendDelay {

    private final static String QUEUE_NAME = "delay";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("piyingxu");
        factory.setPassword("123456");
        factory.setVirtualHost("/java/mq");
        factory.setPort(5672);//默认端口
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //方式一：该队列的所有消息统一设置过期时间
        //Map<String, Object> paramMap = new HashMap<String, Object>();
        //paramMap.put("x-message-ttl", 10000);
        //定义一个队列
        //channel.queueDeclare(QUEUE_NAME, false, false, false, paramMap);

        //方式二: 指定某条消息的过期时间
        AMQP.BasicProperties properties =  new AMQP.BasicProperties.Builder().expiration("5000").build();

        /*
        死信交换机DLX
        队列中的消息在以下三种情况下会变成死信
        （1）消息被拒绝（basic.reject 或者 basic.nack），并且requeue=false;
        （2）消息的过期时间到期了；
        （3）队列长度限制超过了。
        当队列中的消息成为死信以后，如果队列设置了DLX那么消息会被发送到DLX。通过x-dead-letter-exchange设置DLX，通过这个x-dead-letter-routing-key设置消息发送到DLX所用的routing-key，如果不设置默认使用消息本身的routing-key;
        */
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("x-dead-letter-exchange", "test_delay"); //这个exchange预先自定义好，可以是fanout、dirct、topic任意类型（就是一个普通的exchange）
        //paramMap.put("x-dead-letter-routing-key", "delay_routingKey");//死信会放到这个路由关键字下对应的队列，如果是fanout则不用设置

        channel.queueDeclare(QUEUE_NAME, false, false, false, paramMap);

        String message = "Hello delay !";
        channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();

    }
}
