package com.lessismore.xauto.copy;

import java.util.*;

public final class CopierFactory {

    private static Map<String, CopierInterface> copiers = new HashMap<>();
    private static CopierInterface defaultCopier = new DefaultCopier();

    private static String key(Class<?> source, Class<?> target) {
        return source.hashCode() + ":" + target.hashCode();
    }

    private static void register(Class<?> source, Class<?> target, CopierInterface copier) {
        copiers.put(key(source, target), copier);
        //System.out.println("register copier class:"+copier.getClass().getName());
    }

    public static void setDefaultCopier(CopierInterface copier) {
        defaultCopier = copier;
    }

    public static <S, T> CopierInterface<S,T> getCopier(Class<S> source, Class<T> target) {
        CopierInterface copier = copiers.get(key(source, target));
        if (copier != null) {
            return copier;
        }
        return defaultCopier != null ? defaultCopier : new DefaultCopier();
    }

    // 采用SPI方式加载所有的copiers
    static {
        ServiceLoader<CopierInterface> serviceLoader = ServiceLoader.load(CopierInterface.class);
        Iterator<CopierInterface> it = serviceLoader.iterator();

        while (it!=null && it.hasNext()) {
            CopierInterface copier = it.next();
            register(copier.getSourceType(),copier.getTargetType(), copier);

        }
    }

    // 采用SPI方式加载所有配置copiers
    static {
        ServiceLoader<ConverterConfiguration> serviceLoader = ServiceLoader.load(ConverterConfiguration.class);
        Iterator<ConverterConfiguration> it = serviceLoader.iterator();

        while (it!=null && it.hasNext()) {
            ConverterConfiguration configuration = it.next();
            List<CopierInterface> copiers = configuration.loadCopiers();
            if (copiers != null) {
                for (CopierInterface copier : copiers) {
                    register(copier.getSourceType(),copier.getTargetType(), copier);
                }
            }
        }
    }


    // 默认的处理方式
    private static class DefaultCopier extends Copier {
        DefaultCopier() {
            super(Object.class,Object.class);
        }
        @Override
        public Object copy(Object source, Class type, Object defaultValue) {
            return defaultValue;
        }
    }
}
