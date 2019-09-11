package com.example.websocketlearn.comm;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * 文本消息类型 websocket
 */
public abstract class TextEndpoint extends BaseEndpoint implements MessageHandler.Whole<String> {

    /**
     * 处理交易所推送过来的数据
     * 并做数据结构转换，转为统一数据结构
     *
     * @param input
     */
    public abstract void handleMessage(String input);

    @Override
    public void onMessage(String input) {
        handleMessage(input);
    }
}
