package com.example.websocketlearn.endpoint.douyu;

import com.example.websocketlearn.comm.BytesEndPoint;
import com.example.websocketlearn.comm.annt.SiteClientEndpoint;
import com.example.websocketlearn.comm.util.ZipUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.websocket.ClientEndpoint;
import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

@Service
@ClientEndpoint
@SiteClientEndpoint(endpointurl = "wss://danmuproxy.douyu.com:8504/")
public class danmu extends BytesEndPoint {
    @Override
    public void handleMessage(InputStream inputStream, Session session) {
        try {
            String flag = "txt@=";
            String result = read(inputStream);
            int startIndex = result.indexOf(flag);
            if(startIndex != -1){
                int endIndex = result.indexOf("/", startIndex);
                logger.info("result:{}", result.substring(startIndex + flag.length(),endIndex));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void connected(Session session) {
        try {

            //发送连接直播间请求
            session.getBasicRemote().sendBinary(sendConncet());

            //发送加入房间请求
            session.getBasicRemote().sendBinary(sendJoin());

            //定时发送心跳包
            new Thread(()->{
                while (true){
                    try {
                        session.getBasicRemote().sendBinary(sendHeartbeat());
                        Thread.sleep(10000);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }).start();

            //session.getBasicRemote().sendText(str);
        } catch (IOException e) {
            logger.error("异常 {}", e);
        }
    }

    /**
     * 发送连接直播间请求
     * @return
     */
    private ByteBuffer sendConncet(){
        String str = "type@=loginreq/roomid@=85894/dfl@=sn@AA=106@ASss@AA=1@Ssn@AA=107@ASss@AA=1@Ssn@AA=108@ASss@AA=1@Ssn@AA=105@ASss@AA=1@Ssn@AA=110@ASss@AA=1/username@=qq_Rwu6wZe6/uid@=7324866/ver@=20190610/aver@=218101901/ct@=0";
        String s = str2HexStr(str);
        s = "da000000da000000b1020000" + s + "2f00";

        byte[] bytes = hexToByteArray(s);
        return ByteBuffer.wrap(bytes);
    }

    /**
     * 发送加入房间请求
     * @return
     */
    private ByteBuffer sendJoin(){
        String str = "type@=joingroup/rid@=85894/gid@=1/";
        String s = str2HexStr(str);
        s = "2b0000002b000000b1020000" + s + "00";

        byte[] bytes = hexToByteArray(s);
        return ByteBuffer.wrap(bytes);
    }

    /**
     * 发送加入房间请求
     * @return
     */
    ByteBuffer sendHeartbeat(){
        String str = "type@=mrkl";
        String s = str2HexStr(str);
        s = "1400000014000000b1020000" + s + "2f00";

        byte[] bytes = hexToByteArray(s);
        return ByteBuffer.wrap(bytes);
    }


    private String read(InputStream inputStream) throws IOException {

        StringBuilder sb = new StringBuilder();
        String line;

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String str = sb.toString();
        return str;
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str
     * @return
     */
    private static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs) {
            bit = (b & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = b & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    private static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }


    private static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }
}
