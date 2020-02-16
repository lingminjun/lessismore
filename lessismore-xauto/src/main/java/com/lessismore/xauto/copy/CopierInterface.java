package com.lessismore.xauto.copy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CopierInterface<S, T> {

    /**
     * 对象转换，copy到另一个对象，非完全深层拷贝（属性对象类型一致不发生拷贝）
     */
    T copy(S source);
    T copy(S source, T defaultValue);

    List<T> copy(Collection<? extends S> sources);
    List<T> copy(Collection<? extends S> sources, List<T> defaultValue);

    <C extends Collection<T>> C copy(Collection<? extends S> sources, Class<C> collectionType);
    <C extends Collection<T>> C copy(Collection<? extends S> sources, Class<C> collectionType, C defaultValue);

    <K> Map<K, T> copy(Map<K, S> sources);
    <K> Map<K, T> copy(Map<K, S> sources, Map<K, T> defaultValue);

    Class<T> getTargetType();

    Class<S> getSourceType();
}
