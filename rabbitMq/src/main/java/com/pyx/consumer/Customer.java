package com.pyx.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/5 11:23
 */
public class Customer {
    private final static String QUEUE_NAME = "rabbitMQ.java";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ地址
        factory.setHost("127.0.0.1");
        factory.setUsername("piyingxu");
        factory.setPassword("123456");
        factory.setVirtualHost("/java/mq");
        factory.setPort(5672);
        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();
        //声明要关注的队列

        String QUEUE_NAME = "queue_pyx"; //队列名称
        String EXCHANGE_NAME = "exchange.direct.pyx";  //转发器名称
        String ROUTING_KEY = ""; //路由key


         /*
            绑定表示转发器与队列之间的关系。我们也可以简单的认为：队列对该转发器上的消息感兴趣。
            绑定可以附带一个额外的参数routingKey。为了与避免basicPublish方法（发布消息的方法）的参数混淆，我们准备把它称作绑定键（binding key）。下面展示如何使用绑定键（binding key）来创建一个绑定：
            channel.queueBind(queueName, EXCHANGE_NAME, "black");
            绑定键的意义依赖于转发器的类型。对于fanout类型，忽略此参数。
        */
        //channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        System.out.println("Customer Waiting Received messages");
        //DefaultConsumer类实现了Consumer接口，通过传入一个频道，

        // 同一时刻服务器只会发2条消息给消费者
        channel.basicQos(1);

        // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(consumerTag + "--Customer Received '" + message + "'--" + envelope.getRoutingKey() + "--" + JSON.toJSONString(properties));
                try {
                    //模拟处理任务消耗时间
                    Thread.sleep(2000);
                    //可以通过显示调用channel.basicAck(envelope.getDeliveryTag(), false);来告诉消息服务器来删除消息
                    channel.basicAck(envelope.getDeliveryTag(), true);

                    // requeue：重新入队列，true: 删除, false
                    //channel.basicReject(envelope.getDeliveryTag(), true); //当为true时候将会重新放入队列中，所有客户端都可以再次消费

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //自动回复队列应答 -- RabbitMQ中的消息确认机制
        //在订阅消息的时候可以指定应答模式,当自动应答等于true的时候，表示当消费者一收到消息就表示消费者收到了消息，消费者收到了消息就会立即从队列中删除。
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}
