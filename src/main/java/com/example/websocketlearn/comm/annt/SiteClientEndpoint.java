package com.example.websocketlearn.comm.annt;


import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SiteClientEndpoint {
    //websocket 服务端url
    public String endpointurl();
    //自定义名称
    public String name() default "";
}
