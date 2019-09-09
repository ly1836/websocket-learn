package com.example.websocketlearn.comm;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by jiangzuku on 2018/5/28 0028.
 * 文本消息类型 websocket
 */
public abstract class TextEndpoint extends BaseEndpoint implements MessageHandler.Whole<String> {

    /**
     * 处理交易所推送过来的数据
     * 并做数据结构转换，转为统一数据结构
     *
     * @param input
     * @param session
     */
    public void handleMessage(String input, Session session) {

    }

    @Override
    public void onMessage(String input) {
        logger.info("input:{}", input);
    }


    private String getGZipResult(String input) {
        if (input == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed;
        String result = null;
        try {
            compressed = new sun.misc.BASE64Decoder().decodeBuffer(input);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            result = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }
}
