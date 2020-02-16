package com.lessismore.xauto.copy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Copier<S, T> implements CopierInterface<S, T> {

    private final Class<T> targetClass; // 转换目标类型
    private final Class<S> sourceClass; // 原始类型

    protected Copier(Class<S> sourceClass, Class<T> targetClass) {
        this.targetClass = targetClass;
        this.sourceClass = sourceClass;
    }

    protected Copier() {
        Type[] types = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments();
        this.targetClass = (Class<T>)types[1];
        this.sourceClass = (Class<S>)types[0];
    }


    public T copy(S source, Class<T> type, T defaultValue) {
        return defaultValue;
        //throw new RuntimeException("Not support " + source.getClass().getName() + " copy to " + type.getName());
    }

    public final <C extends Collection<T>> C copy(Collection<? extends S> sources, Class<T> type, Class<C> collectionType, C defaultValue) {
        if (sources == null) {
            return null;
        }
        List<T> list = sources.stream().map((e) -> copy(e, type, null)).collect(Collectors.toList());
        if (((Class)collectionType) == List.class || ((Class)collectionType) == ArrayList.class) {
            return (C)list;
        }
        return Converters.Collection.to(list, collectionType, type, defaultValue, null);
    }

    public final <K> Map<K, T> copy(Map<K, S> sources, Class<T> type, Map<K, T> defaultValue) {
        if (sources == null) {
            return defaultValue;
        }
        return sources.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> copy(e.getValue(), type, null)));
    }

    @Override
    public final T copy(S source) {
        return copy(source, getTargetType(), null);
    }

    @Override
    public final T copy(S source, T defaultValue) {
        return copy(source, getTargetType(), defaultValue);
    }

    @Override
    public final List<T> copy(Collection<? extends S> sources) {
        return copy(sources, getTargetType(), List.class, null);
    }

    @Override
    public final List<T> copy(Collection<? extends S> sources, List<T> defaultValue) {
        return copy(sources, getTargetType(), List.class, defaultValue);
    }

    @Override
    public final <C extends Collection<T>> C copy(Collection<? extends S> sources, Class<C> collectionType) {
        return copy(sources, getTargetType(), collectionType, null);
    }

    @Override
    public final <C extends Collection<T>> C copy(Collection<? extends S> sources, Class<C> collectionType, C defaultValue) {
        return copy(sources, getTargetType(), collectionType, defaultValue);
    }

    @Override
    public final <K> Map<K, T> copy(Map<K, S> sources) {
        return copy(sources, getTargetType(), null);
    }

    @Override
    public final <K> Map<K, T> copy(Map<K, S> sources, Map<K, T> defaultValue) {
        return copy(sources, getTargetType(), defaultValue);
    }

    @Override
    public final Class<T> getTargetType() {
        return targetClass;
    }

    @Override
    public final Class<S> getSourceType() {
        return sourceClass;
    }
}
