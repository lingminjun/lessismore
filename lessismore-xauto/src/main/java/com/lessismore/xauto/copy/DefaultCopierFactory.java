package com.lessismore.xauto.copy;

public interface DefaultCopierFactory {
    DefaultCopier getCopier(Class<?> source, Class<?> target);
}
