package com.lessismore.xauto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定mapping关系
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface XAutoMapping {
    /**
     * 目标属性名：target.keyField
     */
    String field();

    /**
     * 原始属性名：source.fromField，
     */
    String from();

    /**
     * 原始属性值转化表达式：
     *      如: new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(#{fromValue})
     *      如: #{fromValue} != null
     *
     * 注意：1、表达式为java代码片段，引入类型用全称，
     *      2、输入参数为#{fromValue}，由from取得的值来
     */
    String expression() default "";
}
