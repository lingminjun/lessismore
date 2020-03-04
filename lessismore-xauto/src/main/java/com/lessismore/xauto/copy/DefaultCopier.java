package com.lessismore.xauto.copy;

public abstract class DefaultCopier extends Copier {
    public DefaultCopier(Class<?> sourceClass, Class<?> targetClass) {
        super(sourceClass,targetClass);
    }
}
