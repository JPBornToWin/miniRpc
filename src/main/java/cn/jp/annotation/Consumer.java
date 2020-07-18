package cn.jp.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Consumer {
    String ip() default "";

    int port() default 0;
}
