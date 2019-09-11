package com.example.websocketlearn.comm;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.InputStream;
import java.util.Set;

/**
 * 二进制消息类型 websocket
 */
public abstract class BytesEndPoint extends BaseEndpoint implements MessageHandler.Whole<InputStream>{

    /**
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
