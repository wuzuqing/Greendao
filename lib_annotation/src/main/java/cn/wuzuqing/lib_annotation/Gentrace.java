package cn.wuzuqing.lib_annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.CONSTRUCTOR)
public @interface Gentrace {
    int value();
}
