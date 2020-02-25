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
     * 输入对象model对应的 ModelInfo
     */
    String className() default "";

    /**
     * 模板位置，相对resource地址，如:classes:tmp/#{classTemplate}
     * 输入对象model对应的 ModelInfo
     * 模板中类名请与className保持一致
     */
    String classTemplate() default "";


    /**
     * 模板位置，相对resource地址，如:classes:tmp/#{resourceTemplate}
     * 输入对象model对应的 ModelInfo
     */
    String resourceTemplate() default "";


    /**
     * 模版输入数据对象，构造成ClassInfo输入
     */
    Class<?> model();

    /**
     * 其他关联对象
     */
    Class<?>[] others() default {};

    /**
     * 输出路径，默认当前工程classes/下，取relative路径
     * @return
     */
    String classRelativePath() default "";

    /**
     * 输出路径，默认当前工程classes/下，取relative路径
     * @return
     */
    String resourceRelativePath() default "";


    /**
     * className自动加载到META-INFO/#{metaInfoFileName}中
     * 如配置Spring工厂容器：META-INF/spring.factories，
     *      案例：metaInfoFileName = "spring.factories";
     * @return
     */
    String metaInfoFileName() default "";
}
