package com.lessismore.xauto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要做转换,目标类型集合，自动生成转化类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@XAutoAccessor
public @interface XAutoConvert {
    /**
     * 所有支持的类
     * @return
     */
    XAutoTarget[] value();
}
