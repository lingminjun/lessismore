package com.lessismore.xauto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Getter
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface XAutoGetter {
}
