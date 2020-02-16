package com.lessismore.xauto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记类为三方转化类
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface XAutoTemplate {
    /**
     * 生产的类名字，必须全称，包括包名，如：com.lessismore.xauto.AutoService，可支持el表达式
     * 输入对象model对应的ClassInfo
     */
    String className();

    /**
     * 模板位置，相对resource地址，如:META-INF/tmp/xxxx
     * 输入对象model对应的ClassInfo
     * 模板类名请与name保持一致
     */
    String template();

    /**
     * 模板文件扩展名
     * @return
     */
    String suffix() default "ftl";

    /**
     * 模版输入数据对象，构造成ClassInfo输入
     */
    Class<?> model();

    /**
     * 输出路径，默认当前工程class下
     * @return
     */
    String out() default "";
}
