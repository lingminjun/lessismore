package com.lessismore.xauto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰类、参数、方法，主要用于描述
 */
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface XAutoNote {
    /* 描述 */
    String value() default "";

    /* 名字 */
    String name() default "";

    /* 是否必须 */
    boolean required() default false;
}
