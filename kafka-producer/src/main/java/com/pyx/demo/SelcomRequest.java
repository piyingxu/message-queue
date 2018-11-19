package com.pyx.demo;

import java.sql.Timestamp;
import javax.annotation.Resource;

/**
 * @author yinqi
 * @date 2018/8/9
 */
public class SelcomRequest<T> {

    private String method;
    private Long timestamp;
    private T requestParams;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(T requestParams) {
        this.requestParams = requestParams;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
