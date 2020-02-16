package com.lessismore.xauto.copy;

public interface Convertible {
    default <T> T convertTo(Class<T> type) {
        CopierInterface<?,T> copier = CopierFactory.getCopier(this.getClass(), type);
        return ((CopierInterface<Object,T>)copier).copy(this);
    }
}
