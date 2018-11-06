package com.pyx.product;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/5 11:22
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ相关信息
        factory.setHost("127.0.0.1");
        factory.setUsername("piyingxu");
        factory.setPassword("123456");
        factory.setVirtualHost("/java/mq");
        factory.setPort(5672);//默认端口
        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();

        String QUEUE_NAME = "queue_pyx"; //队列名称
        String EXCHANGE_NAME = "exchange.direct.pyx";  //转发器名称

        // 声明转发器的类型
        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.DIRECT,false);

        //  声明一个队列
        /*
         String queue： 队列名称
         boolean durable：是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会读取该数据库
         boolean exclusive： 是否排外的，有两个作用，一：当连接关闭时connection.close()该队列是否会自动删除；二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，没有任何问题，
                             如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，如果强制访问会报异常：
                             com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=405, reply-text=RESOURCE_LOCKED -
                             cannot obtain exclusive access to locked queue 'queue_name' in vhost '/', class-id=50, method-id=20)
                             一般等于true的话用于一个队列只能有一个消费者来消费的场景
         boolean autoDelete：是否自动删除，当最后一个消费者断开连接之后队列是否自动被删除，可以通过RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         Map<String, Object> arguments
            Message TTL(x-message-ttl)：设置队列中的所有消息的生存周期(统一为整个队列的所有消息设置生命周期), 也可以在发布消息的时候单独为某个消息指定剩余生存时间,单位毫秒, 类似于redis中的ttl，生存时间到了，消息会被从队里中删除，注意是消息被删除，而不是队列被删除， 特性Features=TTL, 单独为某条消息设置过期时间AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder().expiration(“6000”);
            Auto Expire(x-expires): 当队列在指定的时间没有被访问(consume, basicGet, queueDeclare…)就会被删除,Features=Exp
            Max Length(x-max-length): 限定队列的消息的最大值长度，超过指定长度将会把最早的几条删除掉， 类似于mongodb中的固定集合，例如保存最新的100条消息, Feature=Lim
            Max Length Bytes(x-max-length-bytes): 限定队列最大占用的空间大小， 一般受限于内存、磁盘的大小, Features=Lim B
            Maximum priority(x-max-priority)：优先级队列，声明队列时先定义最大优先级值(定义最大值一般不要太大)，在发布消息的时候指定该消息的优先级， 优先级更高（数值更大的）的消息先被消费,
            Lazy mode(x-queue-mode=lazy)： Lazy Queues: 先将消息保存到磁盘上，不放在内存中，当消费者开始消费的时候才加载到内存中
            Master locator(x-queue-master-locator)
            DLX，Dead-Letter-Exchange 当消息在一个队列中变成死信后，它能被重新publish到另一个Exchange，这个Exchange就是DLX。消息变成死信一向有以下几种情况：
                消息被拒绝（basic.reject or basic.nack）并且requeue=false
                消息TTL过期
                队列达到最大长度
         */
        Map<String, Object> paramMap = new HashMap<String, Object>();
        //paramMap.put("x-message-ttl", 5000);


        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //发送消息到队列中
        for (int i = 1; i < 100; i++) {
            String ROUTING_KEY = "key_pyx_" + i; //路由key
              /*
                绑定表示转发器与队列之间的关系。我们也可以简单的认为：队列对该转发器上的消息感兴趣。
                绑定可以附带一个额外的参数routingKey。为了与避免basicPublish方法（发布消息的方法）的参数混淆，我们准备把它称作绑定键（binding key）。下面展示如何使用绑定键（binding key）来创建一个绑定：
                channel.queueBind(queueName, EXCHANGE_NAME, "black");
                绑定键的意义依赖于转发器的类型。对于fanout类型，忽略此参数。
            */
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            String message = "Hello RabbitMQ:" + i;
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
            System.out.println("Producer Send +'" + message + "'");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
