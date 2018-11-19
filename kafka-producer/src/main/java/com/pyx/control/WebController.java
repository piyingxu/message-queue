package com.pyx.control;

import com.alibaba.fastjson.JSON;
import com.pyx.demo.SelcomRequest;
import com.pyx.util.MyProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

@Api(description = "控制器", tags = "TEST")
@RequestMapping("selcom/test")
@RestController
public class WebController {

	private Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private MyProducer producer;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @ApiOperation("1、发送消息")
	@RequestMapping(value = "/sendMsg", method = RequestMethod.GET)
	public boolean sendMsg(@RequestParam("topic") String topic, @RequestParam("msg") String msg) {
        producer.sendMsg(topic, msg);
        return true;
	}

    @ApiOperation("2、发送消息(Spring)")
    @RequestMapping(value = "/asyncSend", method = RequestMethod.GET)
    public void asyncSend(@RequestParam("topic") String topic, @RequestParam("msg") String msg) {
        try {
            logger.info("开始发送消息");
            ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, msg);
            future.get(2, TimeUnit.SECONDS); //在规定的时间内没有拿到结果则抛出超时异常
            future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onSuccess(SendResult<Integer, String> result) {
                    logger.info("发送kafka成功,msg={}", msg);
                }
                @Override
                public void onFailure(Throwable ex) {
                    logger.error("发送kafka失败,msg={}", msg);
                }
            });
        } catch (Exception e) {
            logger.error("发送消息出现异常", e);
        }
    }

    @PostMapping("/post")
    public String test(@RequestBody SelcomRequest req) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(req));
        System.out.println("dsds");

        try {
            throw new SocketTimeoutException();
        } catch (SocketTimeoutException e) {

        }


        return "ooxx";
    }


}

