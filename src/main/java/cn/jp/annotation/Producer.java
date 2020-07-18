package cn.jp.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Producer {
    String ip() default "";

    int port() default 0;

    String interfaceName() default "";
}
