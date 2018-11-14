package cn.wuzuqing.lib_annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Index {
    String name() default "";

    boolean nullable() default true;

    int maxLength() default 0;
}
