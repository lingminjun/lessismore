package com.lessismore.xauto.copy;

public interface DynamicCopier {
    Object copy(Object source, Class target, Object defaultValue);
}
