package com.example.websocketlearn.comm;

//import com.cn.ccc.annotation.ExchangeClientEndpoint;

import com.example.websocketlearn.comm.annt.SiteClientEndpoint;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.websocket.*;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangzuku on 2018/5/23 0023. 交易所websocket客户端管理中心
 */
@Service
public class ClientEndpointCenter {
    private Logger logger = LoggerFactory.getLogger(ClientEndpointCenter.class);

    //@Value("${proxy.host}")
    private String proxyServerHost= "127.0.0.1";

    //@Value("${proxy.port}")
    private Integer proxyServerPort = 1080;

    private Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    private Map<String, String> sessionURIMap = new ConcurrentHashMap<>();

    private WebSocketContainer webSocketContainer = null;

    private ClientEndpointConfig clientEndpointConfig = null;

    @Autowired
    protected TaskExecutor taskExecutor;

    /**
     * 建立websocket连接，并注册session
     */
    public void connectAndRegistEndpoint(ApplicationContext applicationContext) {
        //setProxy(proxyServerHost, proxyServerPort);
        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(SiteClientEndpoint.class);
        Set<Map.Entry<String, Object>> entrySet = objectMap.entrySet();
        if (CollectionUtils.isNotEmpty(entrySet)) {
            for (Map.Entry<String, Object> entry : entrySet) {
                try {
                    Object object = entry.getValue();
                    boolean success = connectAndRegistEndpoint(object);
                    if (!success) {
                        logger.error("连接失败key:{}", entry.getKey());
                    }
                } catch (Exception e) {
                    logger.error("建立websocket连接失败:" + entry.getKey(), e);
                }
            }
        }
    }

    /**
     * 建立连接
     *
     * @param exchangeBaseEndpoint
     */
    public boolean connectAndRegistEndpoint(Object exchangeBaseEndpoint) {
        boolean success = true;
        try {
            createWebSocketContainer();
            createClientEndpointConfig();
            SiteClientEndpoint clientEndpoint = exchangeBaseEndpoint.getClass().getAnnotation(SiteClientEndpoint.class);
            BaseEndpoint baseEndpoint = (BaseEndpoint) exchangeBaseEndpoint;
            if (StringUtils.isEmpty(clientEndpoint.endpointurl())) {
                return true;
            }


            String url = clientEndpoint.endpointurl();
            URI uri = new URI(url);
            connect2websocketServer(exchangeBaseEndpoint, uri);
        } catch (Exception e) {
            success = false;
            logger.error("建立websocket连接异常:" + exchangeBaseEndpoint.getClass().getName(), e);
        }
        return success;
    }


    /**
     * 配置代理
     */
    private static void setProxy(String proxyIp, Integer port) {
        Properties prop = System.getProperties();
        prop.setProperty("http.proxyHost", proxyIp);
        prop.setProperty("http.proxyPort", "" + port);
        prop.setProperty("https.proxyHost", proxyIp);
        prop.setProperty("https.proxyPort", "" + port);
        prop.setProperty("ftp.proxyHost", proxyIp);
        prop.setProperty("ftp.proxyPort", "" + port);
        prop.setProperty("socksProxyHost", proxyIp);
        prop.setProperty("socksProxyPort", "" + port);
        prop.setProperty("org.apache.tomcat.websocket.IO_TIMEOUT_MS", "20000");
    }

    public static String uniteString(Object... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        if (parameters.length > 0) {
            for (Object temp : parameters) {
                stringBuilder.append(temp);
            }
        }
        return stringBuilder.toString();
    }

    public Session connect2websocketServer(Object object, URI uri) throws Exception {
        Endpoint endpoint = (Endpoint) object;
        Session session = webSocketContainer.connectToServer(endpoint, uri);
        String key = uniteString(object.getClass().getSimpleName(), "@", uri);
        sessionMap.put(session.getId(), session);
        sessionURIMap.put(session.getId(), key);
        BaseEndpoint baseEndpoint = (BaseEndpoint) object;

        if (object instanceof TextEndpoint) {
            TextEndpoint textEndpoint = (TextEndpoint) object;
            session.addMessageHandler(textEndpoint);
        } else if (object instanceof BytesEndPoint) {
            BytesEndPoint bytesEndPoint = (BytesEndPoint) object;
            session.addMessageHandler(bytesEndPoint);
        }

        logger.info("{}绑定session{}", object.getClass().getSimpleName(), uri);
        logger.info("建立websocket连接成功:" + object.getClass().getName());
        return session;
    }

    public void removeSession(Session session) {
        logger.info("{}移除session{}", session.getId(), sessionURIMap.get(session.getId()));
        sessionMap.remove(session.getId());
        sessionURIMap.remove(session.getId());
    }

    public WebSocketContainer createWebSocketContainer() {
        if (webSocketContainer == null) {
            webSocketContainer = ContainerProvider.getWebSocketContainer();
            webSocketContainer.setDefaultMaxTextMessageBufferSize(50 * 1024 * 1024);
            webSocketContainer.setDefaultMaxBinaryMessageBufferSize(50 * 1024 * 1024);
            webSocketContainer.setDefaultMaxSessionIdleTimeout(-1);
        }
        return webSocketContainer;
    }

    public ClientEndpointConfig createClientEndpointConfig() throws Exception {
        if (clientEndpointConfig == null) {
            SSLContext sslc;
            sslc = SSLContext.getInstance("TLSv1.2");
            TrustManager[] trustManagerArray = {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            sslc.init(new KeyManager[0], trustManagerArray, null);
            clientEndpointConfig = ClientEndpointConfig.Builder.create().build();
            clientEndpointConfig.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslc);
            clientEndpointConfig.getUserProperties().put("org.apache.tomcat.websocket.IO_TIMEOUT_MS", "20000");
        }
        return clientEndpointConfig;
    }

    public String getProxyServerHost() {
        return proxyServerHost;
    }

    public Integer getProxyServerPort() {
        return proxyServerPort;
    }
}
