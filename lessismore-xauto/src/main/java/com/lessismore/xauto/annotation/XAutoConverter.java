package com.lessismore.xauto.annotation;

import java.lang.annotation.*;

/**
 * 标记类为三方转化类
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface XAutoConverter {
    /**
     * 原数据类型
     */
    Class<?> source();

    /**
     * 待转换目标类型
     */
    Class<?> target();
}
