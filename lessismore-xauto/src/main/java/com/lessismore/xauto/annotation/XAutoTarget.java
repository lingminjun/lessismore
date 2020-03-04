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
public @interface XAutoTarget {
    /**
     * 待转换目标类型
     */
    Class<?> target();

    /**
     * mapping细节变化
     * @return
     */
    XAutoMapping[] mapping() default {};
}
