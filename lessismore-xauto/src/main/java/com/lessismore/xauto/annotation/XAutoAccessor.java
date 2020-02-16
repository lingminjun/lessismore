package com.lessismore.xauto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Getter,Setter
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@XAutoGetter
@XAutoSetter
public @interface XAutoAccessor {
    String value() default "";
}
