package com.example.websocketlearn.comm;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;


public abstract class BaseEndpoint extends Endpoint {

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected TaskExecutor taskExecutor;


    @Autowired
    protected ClientEndpointCenter clientEndpointCenter;

    protected Session session;

    /**
     * websocket建立 可以加初始化业务代码
     */
    public abstract void connected(Session session);

    public String getProxyServerHost() {
        return clientEndpointCenter.getProxyServerHost();
    }

    public Integer getProxyServerPort() {
        return clientEndpointCenter.getProxyServerPort();
    }

    /**
     * 订阅房间
     *
     * @param session
     */
    public void sub(Session session) {

    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        //session.setMaxIdleTimeout(1000);
        connected(session);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        logger.error("websocket error");
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.error("websocket close,reason:{}", JSONObject.toJSONString(closeReason));

    }


}
