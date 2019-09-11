package com.example.websocketlearn.endpoint.douyu;

import com.example.websocketlearn.comm.TextEndpoint;
import com.example.websocketlearn.comm.annt.SiteClientEndpoint;
import org.springframework.stereotype.Service;

import javax.websocket.ClientEndpoint;
import javax.websocket.Session;
import java.io.IOException;

/**
 * 这是一个例子
 * 可以在此包下新建类，按不通消息类型(文本和二进制)继承TextEndpoint或BytesEndPoint类
 * 重写handleMessage方法来接收消息
 * 重写connected方法来处理建立连接时的请求,
 */
@Service
@ClientEndpoint
@SiteClientEndpoint(endpointurl = "wss://echo.websocket.org/")
public class TextExample extends TextEndpoint {

    /**
     * 建立连接时想服务器发送消息
     * @param session
     */
    @Override
    public void connected(Session session) {
        try {
            session.getBasicRemote().sendText("WebSocket rocks");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文本消息接收
     * @param input
     */
    @Override
    public void handleMessage(String input) {
        logger.info("result:{}",input);
    }
}
