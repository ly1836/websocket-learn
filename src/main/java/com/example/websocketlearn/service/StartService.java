package com.example.websocketlearn.service;

import com.example.websocketlearn.comm.ClientEndpointCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 启动类
 */
@Component
public class StartService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientEndpointCenter clientEndpointCenter;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        clientEndpointCenter.connectAndRegistEndpoint(applicationContext);
        //while (true){}
        try {
            System.out.println(System.in.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
