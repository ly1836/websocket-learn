package com.example.websocketlearn.comm.annt;


import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SiteClientEndpoint {
    public String endpointurl();
    public String name() default "";
}
