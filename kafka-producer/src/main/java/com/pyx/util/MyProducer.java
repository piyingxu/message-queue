package com.pyx.util;

import com.pyx.control.WebController;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/14 10:31
 */
@Component
public class MyProducer {

    private Logger logger = LoggerFactory.getLogger(WebController.class);

    private KafkaProducer<String, String> producer;

    private Properties properties = new Properties();

    public MyProducer() {
        //bootstrap.servers是Kafka集群的IP地址，如果Broker数量超过1个，则使用逗号分隔，如"192.168.1.110:9092,192.168.1.110:9092"。
        // 其中，192.168.1.110是我的其中一台虚拟机的bootstrap.servers是Kafka集群的IP地址，如果Broker数量超过1个，
        // 则使用逗号分隔，如"192.168.1.110:9092,192.168.1.110:9092"。其中，192.168.1.110是我的其中一台虚拟机的
        properties.put("bootstrap.servers", "127.0.0.1:9093");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        //序列化类型。 Kafka消息是以键值对的形式发送到Kafka集群的，其中Key是可选的，Value可以是任意类型。但是在Message被发送到Kafka集群之前，Producer需要把不同类型的消
        //息序列化为二进制类型。本例是发送文本消息到Kafka集群，所以使用的是StringSerializer。
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //指定分区策略
        properties.put("partitioner.class", "com.pyx.util.SimplePartitioner");

        producer = new KafkaProducer<String, String>(properties);
    }

    public void sendMsg(String topic, String msgStr) {
        try {
            logger.info("Send:{}", msgStr);
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, msgStr);
            /*
            因此，实际上调用send方法并不能保证消息被成功写入到kafka。为了实现同步的发送消息，并监控每条消息是否发送成功，
            需要对每次调用send方法后返回的Future对象调用get方法。（get方法的调用会导致当前线程block，直到发送结果返回，
            不管是成功还是失败），当get方法抛出一个错误时，说明数据没有被正确写入，此时需要处理这个错误。
            */
            //RecordMetadata ret = producer.send(record).get();
            // logger.info("ret:{}", ret);

            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                    logger.info("message send to partition={}, offset={} " , metadata.partition(), metadata.offset());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
