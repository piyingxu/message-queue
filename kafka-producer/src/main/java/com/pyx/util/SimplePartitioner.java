package com.pyx.util;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * @author: yingxu.pi@transsnet.com
 * @date: 2018/11/14 15:07
 */
public class SimplePartitioner implements Partitioner {

    private static int num = 0;

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //此处可以按自己的规则指定把消息发送到指定的分区
        if (!"pyx_topic".equals(topic)) {
            num++;
            return num%3;
        } else {
            return 0;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
