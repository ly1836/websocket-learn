package com.example.websocketlearn.comm;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by jiangzuku on 2018/5/28 0028.
 * 二进制消息类型 websocket
 */
public abstract class BytesEndPoint extends BaseEndpoint implements MessageHandler.Whole<InputStream>{

    /**
     * 处理交易所推送过来的数据
     * 并做数据结构转换，转为统一数据结构
     *
     * @param inputStream
     * @param session
     */
    public abstract void handleMessage(InputStream inputStream, Session session);


    @Override
    public void onMessage(InputStream inputStream) {
        handleMessage(inputStream, session);
    }

}
