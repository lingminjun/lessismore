package com.lessismore.xauto.copy;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 支持的所有隐式转换
 */
public final class Converters {

    public static class Type {

        // void
        public static final java.lang.String TYPE_VOID = "void";

        // 基础类型
        public static final java.lang.String TYPE_int = int.class.getName();
        public static final java.lang.String TYPE_short = short.class.getName();
        public static final java.lang.String TYPE_long = long.class.getName();
        public static final java.lang.String TYPE_float = float.class.getName();
        public static final java.lang.String TYPE_double = double.class.getName();
        public static final java.lang.String TYPE_char = char.class.getName();
        public static final java.lang.String TYPE_bool = boolean.class.getName();
        public static final java.lang.String TYPE_byte = byte.class.getName();

        // 包装类型
        public static final java.lang.String TYPE_INT = Integer.class.getName();
        public static final java.lang.String TYPE_SHORT = java.lang.Short.class.getName();
        public static final java.lang.String TYPE_LONG = java.lang.Long.class.getName();
        public static final java.lang.String TYPE_FLOAT = java.lang.Float.class.getName();
        public static final java.lang.String TYPE_DOUBLE = java.lang.Double.class.getName();
        public static final java.lang.String TYPE_CHAR = Character.class.getName();
        public static final java.lang.String TYPE_BOOL = Boolean.class.getName();
        public static final java.lang.String TYPE_BYTE = Byte.class.getName();

        // 字符串
        public static final java.lang.String TYPE_STRING = java.lang.String.class.getName();

        // 基础类
        public static final java.lang.String TYPE_OBJECT = Object.class.getName();

        // list
        public static final java.lang.String TYPE_COLLECTION = java.util.Collection.class.getName();
        public static final java.lang.String TYPE_LIST = List.class.getName();
        public static final java.lang.String TYPE_ARRAY_LIST = ArrayList.class.getName();
        public static final java.lang.String TYPE_STACK = Stack.class.getName();
        public static final java.lang.String TYPE_VECTOR = Vector.class.getName();
        public static final java.lang.String TYPE_SET = Set.class.getName();
        public static final java.lang.String TYPE_HASH_SET = HashSet.class.getName();

        // map
        public static final java.lang.String TYPE_MAP = Map.class.getName();
        public static final java.lang.String TYPE_HASH_MAP = HashMap.class.getName();

        // 基础类型数组
        public static final java.lang.String TYPE_ARRAY_int = int[].class.getName();
        public static final java.lang.String TYPE_ARRAY_short = short[].class.getName();
        public static final java.lang.String TYPE_ARRAY_long = long[].class.getName();
        public static final java.lang.String TYPE_ARRAY_float = float[].class.getName();
        public static final java.lang.String TYPE_ARRAY_double = double[].class.getName();
        public static final java.lang.String TYPE_ARRAY_char = char[].class.getName();
        public static final java.lang.String TYPE_ARRAY_bool = boolean[].class.getName();
        public static final java.lang.String TYPE_ARRAY_byte = byte[].class.getName();

        // 包装类型数组
        public static final java.lang.String TYPE_ARRAY_INT = Integer[].class.getName();
        public static final java.lang.String TYPE_ARRAY_SHORT = java.lang.Short[].class.getName();
        public static final java.lang.String TYPE_ARRAY_LONG = java.lang.Long[].class.getName();
        public static final java.lang.String TYPE_ARRAY_FLOAT = java.lang.Float[].class.getName();
        public static final java.lang.String TYPE_ARRAY_DOUBLE = java.lang.Double[].class.getName();
        public static final java.lang.String TYPE_ARRAY_CHAR = Character[].class.getName();
        public static final java.lang.String TYPE_ARRAY_BOOL = Boolean[].class.getName();
        public static final java.lang.String TYPE_ARRAY_BYTE = Byte[].class.getName();

        // 字符串数组
        public static final java.lang.String TYPE_ARRAY_STRING = java.lang.String[].class.getName();

        // 基础类数组
        public static final java.lang.String TYPE_ARRAY_OBJECT = Object[].class.getName();

        public static boolean isArray(java.lang.String type) {
            return type.startsWith("[");
        }

        public static boolean isCollection(java.lang.String type) {
            return type.startsWith(TYPE_COLLECTION)
                    || type.startsWith(TYPE_LIST)
                    || type.startsWith(TYPE_ARRAY_LIST)
                    || type.startsWith(TYPE_STACK)
                    || type.startsWith(TYPE_VECTOR)
                    || type.startsWith(TYPE_SET)
                    || type.startsWith(TYPE_HASH_SET);
        }

        public static boolean isMap(java.lang.String type) {
            return type.startsWith(TYPE_MAP)
                    || type.startsWith(TYPE_HASH_MAP);
        }

        public static java.lang.String getFirstElementType(java.lang.String type) {
            java.lang.String[] types = getElementTypes(type);
            if (types == null || types.length == 0) {
                return type;
            }
            return types[0];
        }

        public static java.lang.String[] getElementTypes(java.lang.String type) {
            if (type.startsWith("[")) {
                if (type.endsWith(";")) {
                    return new java.lang.String[] {type.substring(1, type.length() - 1)};
                } else {
                    return new java.lang.String[] {type.substring(1)};
                }
            } else if (type.endsWith(">")) {
                int idx = type.indexOf("<");
                java.lang.String subType = type.substring(idx + 1, type.length() - 1);
                //Map<String,Map<String,List<String>>>
                List<java.lang.String> elt = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                int i = 0;
                int deep = 0;
                while (i < subType.length()) {
                    char c = subType.charAt(i++);
                    if (c == '<') {
                        deep++;
                    } else if (c == '>') {
                        deep--;
                    } else if (c == ',' && deep == 0) {
                        elt.add(builder.toString().trim());
                        builder.delete(0,builder.length());
                        continue;
                    }
                    builder.append(c);
                }
                // 最后的类型
                if (builder.length() > 0) {
                    elt.add(builder.toString().trim());
                }
                return elt.toArray(new java.lang.String[0]);
            }

            return new java.lang.String[] {type};
        }
    }

    public static class Obj {
        // 通用的转换函数
        public static <T> T to(Object source, Class<T> type, Class<?> elementType, T defaultValue, Object elementDefaultVaule) {
            if (source == null || type == null) {
                return defaultValue;
            }

            // 注意不实现拷贝，子类
            if (type.isAssignableFrom(source.getClass()) && elementType == null) {
                return (T)source;
            }

            if (type.isArray()) {
                if (defaultValue == null || defaultValue.getClass().isArray()) {
                    return (T) Array.to(source, (Class<Object[]>)type, (Class<Object>)elementType, (Object[]) defaultValue, elementDefaultVaule);
                } else {
                    return defaultValue;
                }
            } else if (type.isEnum()) {
                if (defaultValue == null || defaultValue.getClass().isEnum()) {
                    return (T) Enum.to(source, (Class<java.lang.Enum>) type, (java.lang.Enum) defaultValue);
                } else {
                    return defaultValue;
                }
            } else if (java.util.Collection.class.isAssignableFrom(type)) {
                return (T)Collection.to(source, (Class<java.util.Collection>)type, (Class<Object>) elementType, (java.util.Collection)defaultValue, elementDefaultVaule);
            } else if (type == Integer.class || type == int.class) {
                return (T)INT.to(source, (Integer)defaultValue);
            } else if (type == Short.class || type == short.class) {
                return (T)SHORT.to(source, (Short)defaultValue);
            } else if (type == Long.class || type == long.class) {
                return (T)LONG.to(source, (java.lang.Long)defaultValue);
            } else if (type == Float.class || type == float.class) {
                return (T)FLOAT.to(source, (Float)defaultValue);
            } else if (type == Double.class || type == double.class) {
                return (T)DOUBLE.to(source, (Double)defaultValue);
            } else if (type == Boolean.class || type == boolean.class) {
                return (T)BOOL.to(source, (Boolean)defaultValue);
            } else if (type == Byte.class || type == byte.class) {
                return (T)BYTE.to(source, (Byte)defaultValue);
            } else if (type == Character.class || type == char.class) {
                return (T)CHAR.to(source, (Character) defaultValue);
            } else if (type == String.class) {
                return (T)Str.to(source, (String) defaultValue);
            } else if (java.util.Map.class.isAssignableFrom(type)) {// 暂时不支持
                return defaultValue;
            } else {
                CopierInterface<Object,T> copier = CopierFactory.getCopier((Class<Object>) source.getClass(), type);
                return copier.copy(source, defaultValue);
            }
        }
    }



    // 负责容器转换
    public static class Collection {

        public static <E, T extends java.util.Collection<E>> T to(Object source, Class<T> type, Class<E> elementType, T defaultValue, E elementDefaultVaule) {
            if (source == null || type == null || elementType == null) {
                return defaultValue;
            }

            if (source.getClass().isArray()) {
                int size = java.lang.reflect.Array.getLength(source);
                java.util.Collection collection = null;
                if (Stack.class.isAssignableFrom(type)) {
                    collection = new Stack();
                } else if (Vector.class.isAssignableFrom(type)) {
                    collection = new Vector();
                } else if (List.class.isAssignableFrom(type)) {// 直接忽略LinkedList
                    collection = new ArrayList();
                } else if (Set.class.isAssignableFrom(type)) {
                    collection = new HashSet();
                } else {
                    return defaultValue;
                }
                for (int i = 0; i < size; i++) {
                    Object o = java.lang.reflect.Array.get(source, i);
                    collection.add(Obj.to(o, elementType, null, elementDefaultVaule, null));
                }
                return (T)collection;
            } else if (source instanceof java.util.Collection) {
                java.util.Collection collection = null;
                if (Stack.class.isAssignableFrom(type)) {
                    collection = new Stack();
                } else if (Vector.class.isAssignableFrom(type)) {
                    collection = new Vector();
                } else if (List.class.isAssignableFrom(type)) {// 直接忽略LinkedList
                    collection = new ArrayList();
                } else if (Set.class.isAssignableFrom(type)) {
                    collection = new HashSet();
                } else {
                    return defaultValue;
                }
                java.util.Collection finalCollection = collection;
                ((java.util.Collection) source).forEach(e -> {
                    finalCollection.add(Obj.to(e, elementType, null, elementDefaultVaule, null));
                });
                return (T)collection;
            }

            return defaultValue;
        }
    }

    // 负责容器转换
    public static class Map {
        public static <K,T,S> java.util.Map<K, T> copy(java.util.Map<K, S> sources, Class<T> type, java.util.Map<K, T> defaultValue, T elementDefaultVaule) {
            if (sources == null) {
                return defaultValue;
            }
            return sources.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> Obj.to(e.getValue(), type, null, elementDefaultVaule, null)));
        }
    }


    public static class Array {

        // 容器转数组
        public static <E> E[] to(Object sources, Class<E[]> type, Class<E> elementType, E[] defaultValue, E elementDefaultVaule) {
            if (sources == null) {
                return defaultValue;
            }

            if (elementType != null && elementType.isPrimitive() && elementDefaultVaule == null) {
                // 必须给定值，否则null point，数组元素是值类型
                elementDefaultVaule =  Obj.to(Integer.valueOf(0), elementType, null, null, null);
            }

            // 只支持数组和列表
            if (sources.getClass().isArray()) {
                int size = java.lang.reflect.Array.getLength(sources);
                Object array = java.lang.reflect.Array.newInstance(type, size);
                for (int i = 0; i < size; i++) {
                    Object o = java.lang.reflect.Array.get(sources, i);
                    java.lang.reflect.Array.set(array, i, Obj.to(o, elementType, null, elementDefaultVaule, null));
                }
                return (E[])array;
            } else if (java.util.Collection.class.isAssignableFrom(sources.getClass())) {
                int size = java.lang.reflect.Array.getLength(sources);
                Object array = java.lang.reflect.Array.newInstance(type, size);
                java.util.Collection collection = (java.util.Collection)sources;
                final int[] i = {0};
                E finalElementDefaultVaule = elementDefaultVaule;
                collection.forEach(o -> {
                    java.lang.reflect.Array.set(array, i[0]++, Obj.to(o, elementType, null, finalElementDefaultVaule, null));
                });
                return (E[])array;
            } else {
                return defaultValue;
            }
        }
    }

    // =========to INT=========
    public static class INT {

        public static java.lang.Integer to(Object value, java.lang.Integer defaultValue) {
            java.lang.Long result = LONG.to(value, defaultValue != null ? java.lang.Long.valueOf(defaultValue.longValue()) : null);
            if (result == null) {
                return defaultValue;
            }

            if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                return defaultValue;
            }

            return result.intValue();
        }

    }

    // =========to INT=========
    public static class SHORT {

        public static java.lang.Short to(Object value, java.lang.Short defaultValue) {
            java.lang.Long result = LONG.to(value, defaultValue != null ? java.lang.Long.valueOf(defaultValue.longValue()) : null);
            if (result == null) {
                return defaultValue;
            }

            if (result < Short.MIN_VALUE || result > Short.MAX_VALUE) {
                return defaultValue;
            }

            return result.shortValue();
        }

    }

    // =========to LONG=========
    public static class LONG {
        public static java.lang.Long to(java.lang.Integer value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.longValue();
        }

        public static java.lang.Long to(java.lang.Short value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.longValue();
        }

        public static java.lang.Long to(java.lang.Long value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value;
        }

        public static java.lang.Long to(java.lang.Float value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            float f = value.floatValue();
            if (f > (1.0f*java.lang.Long.MAX_VALUE) || f < (1.0f*java.lang.Long.MIN_VALUE)) {// 域宽控制
                return defaultValue;
            }
            return value.longValue();
        }

        public static java.lang.Long to(java.lang.Double value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            double d = value.doubleValue();
            if (d > (1.0d*java.lang.Long.MAX_VALUE) || d < (1.0d*java.lang.Long.MIN_VALUE)) {// 域宽控制
                return defaultValue;
            }
            return value.longValue();
        }

        public static java.lang.Long to(Byte value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.longValue();
        }

        public static java.lang.Long to(java.lang.Boolean value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value ? 1l : 0l;
        }

        public static java.lang.Long to(Character value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }

            if (BOOL.isFalseFeature(value)) {
                return 1l;
            } else if (BOOL.isFalseFeature(value)) {
                return 0l;
            }

            if (value < '0' || value > '9') {
                return defaultValue;
            }
            return (long)(value - '0');
        }

        public static java.lang.Long to(CharSequence value, java.lang.Long defaultValue) {
            if (value == null || value.length() == 0) {
                return defaultValue;
            }

            if (BOOL.isTrueFeature(value)) {
                return 1l;
            } else if (BOOL.isFalseFeature(value)) {
                return 0l;
            }

            try {
                double l = java.lang.Double.parseDouble(value.toString());
                if (l > java.lang.Long.MAX_VALUE || l < java.lang.Long.MIN_VALUE) {// 域宽控制
                    return defaultValue;
                }
                return (long)l;
            } catch (Throwable e) { }
            return defaultValue;
        }

        public static java.lang.Long to(Object value, java.lang.Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof CharSequence) {
                return to((CharSequence)value, defaultValue);
            } else if (value instanceof java.lang.Double) {
                return to((java.lang.Double)value, defaultValue);
            } else if (value instanceof java.lang.Float) {
                return to((java.lang.Float)value, defaultValue);
            } else if (value instanceof java.lang.Long) {
                return (java.lang.Long)value;
            } else if (value instanceof java.lang.Short) {
                return to((java.lang.Short)value, defaultValue);
            } else if (value instanceof java.lang.Integer) {
                return to((java.lang.Integer)value, defaultValue);
            } else if (value instanceof Byte) {
                return to((Byte)value, defaultValue);
            } else if (value instanceof java.lang.Boolean) {
                return to((java.lang.Boolean)value, defaultValue);
            } else if (value instanceof Character) {
                return to((Character)value, defaultValue);
            } else if (value.getClass().isEnum()) {// 枚举支持
                if (value instanceof EnumMapping) {
                    return (long) ((EnumMapping) value).intValue();
                } else {
                    return (long) ((java.lang.Enum) value).ordinal();
                }
            } else {
                return defaultValue;
            }
        }
    }

    // =========to BYTE=========
    public static class BYTE {

        public static java.lang.Byte to(Object value, java.lang.Byte defaultValue) {
            java.lang.Short result = SHORT.to(value, defaultValue != null ? java.lang.Short.valueOf(defaultValue.shortValue()) : null);
            if (result == null) {
                return defaultValue;
            }

            if (result < Byte.MIN_VALUE || result > Byte.MAX_VALUE) {
                return defaultValue;
            }

            return result.byteValue();
        }

    }

    // =========to CHAR=========
    public static class CHAR {

        public static java.lang.Character to(Object value, java.lang.Character defaultValue) {
            if (value == null) {
                return defaultValue;
            }

            if (value instanceof Character) {
                return (Character)value;
            }

            String str = Str.to(value, null);
            if (str == null || str.length() != 1) {
                return defaultValue;
            }

            return str.charAt(0);
        }

    }

    // =========to String=========
    public static class Str {

        public static java.lang.String to(java.lang.Integer value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(java.lang.Short value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(java.lang.Long value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(java.lang.Float value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(java.lang.Double value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(Byte value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(java.lang.Boolean value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(Character value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.toString();
        }

        public static java.lang.String to(CharSequence value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            if (value.length() == 0) {
                return "";
            }
            if (value instanceof Str) {
                return (java.lang.String)value;
            }
            return value.toString();
        }

        public static java.lang.String to(Object value, java.lang.String defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof CharSequence) {
                return to((CharSequence)value, defaultValue);
            } else if (value instanceof java.lang.Double) {
                return to((java.lang.Double)value, defaultValue);
            } else if (value instanceof java.lang.Float) {
                return to((java.lang.Float)value, defaultValue);
            } else if (value instanceof java.lang.Long) {
                return to((java.lang.Long)value, defaultValue);
            } else if (value instanceof java.lang.Short) {
                return to((java.lang.Short)value, defaultValue);
            } else if (value instanceof java.lang.Integer) {
                return to((java.lang.Integer)value, defaultValue);
            } else if (value instanceof Byte) {
                return to((Byte)value, defaultValue);
            } else if (value instanceof java.lang.Boolean) {
                return to((java.lang.Boolean)value, defaultValue);
            } else if (value instanceof Character) {
                return to((Character)value, defaultValue);
            } else if (value.getClass().isEnum()) {// 枚举支持
                return ((java.lang.Enum)value).name();
            } else {
                return defaultValue;
            }
        }
    }

    // =========to BOOL=========
    public static class BOOL {
        public static boolean isTrueFeature(CharSequence value) {
            if (value == null) {
                return false;
            }
            java.lang.String str = value.toString();
            if (str.equalsIgnoreCase("Y")
                    || str.equalsIgnoreCase("T")
                    || str.equalsIgnoreCase("1")
                    || str.equalsIgnoreCase("YES")
                    || str.equalsIgnoreCase("TRUE")
                    || str.equalsIgnoreCase("ON")
                    || str.equalsIgnoreCase("OK")
                    || str.equalsIgnoreCase("SUCCESS")
                    || str.equalsIgnoreCase("RIGHT")) {
                return true;
            }
            return false;
        }

        public static boolean isFalseFeature(CharSequence value) {
            if (value == null) {
                return false;
            }
            java.lang.String str = value.toString();
            if (str.equalsIgnoreCase("N")
                    || str.equalsIgnoreCase("F")
                    || str.equalsIgnoreCase("0")
                    || str.equalsIgnoreCase("NO")
                    || str.equalsIgnoreCase("FALSE")
                    || str.equalsIgnoreCase("OFF")
                    || str.equalsIgnoreCase("CANCEL")
                    || str.equalsIgnoreCase("FAILURE")
                    || str.equalsIgnoreCase("WRONG")) {
                return true;
            }
            return false;
        }

        public static boolean isTrueFeature(char value) {
            if (value == 'Y' || value == 'T' || value == '1') {
                return true;
            }
            return false;
        }

        public static boolean isFalseFeature(char value) {
            if (value == 'N' || value == 'F' || value == '0') {
                return true;
            }
            return false;
        }

        public static java.lang.Boolean to(java.lang.Integer value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value != 0;
        }

        public static java.lang.Boolean to(java.lang.Short value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value != 0;
        }

        public static java.lang.Boolean to(java.lang.Long value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value != 0;
        }

        public static java.lang.Boolean to(java.lang.Float value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value != 0f;
        }

        public static java.lang.Boolean to(java.lang.Double value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value != 0d;
        }

        public static java.lang.Boolean to(Byte value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value != 0;
        }

        public static java.lang.Boolean to(Character value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            if (BOOL.isFalseFeature(value)) {
                return java.lang.Boolean.TRUE;
            } else if (BOOL.isFalseFeature(value)) {
                return java.lang.Boolean.FALSE;
            }
            return defaultValue;
        }

        public static java.lang.Boolean to(CharSequence value, java.lang.Boolean defaultValue) {
            if (value == null || value.length() == 0) {
                return defaultValue;
            }
            if (BOOL.isTrueFeature(value)) {
                return java.lang.Boolean.TRUE;
            } else if (BOOL.isFalseFeature(value)) {
                return java.lang.Boolean.FALSE;
            }
            return defaultValue;
        }

        public static java.lang.Boolean to(Object value, java.lang.Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof CharSequence) {
                return to((CharSequence)value, defaultValue);
            } else if (value instanceof java.lang.Double) {
                return to((java.lang.Double)value, defaultValue);
            } else if (value instanceof java.lang.Float) {
                return to((java.lang.Float)value, defaultValue);
            } else if (value instanceof java.lang.Long) {
                return to((java.lang.Long)value, defaultValue);
            } else if (value instanceof java.lang.Short) {
                return to((java.lang.Short)value, defaultValue);
            } else if (value instanceof java.lang.Integer) {
                return to((java.lang.Integer)value, defaultValue);
            } else if (value instanceof Byte) {
                return to((Byte)value, defaultValue);
            } else if (value instanceof java.lang.Boolean) {
                return (java.lang.Boolean)value;
            } else if (value instanceof Character) {
                return to((Character)value, defaultValue);
            } else {
                return defaultValue;
            }
        }
    }

    // =========to Enum=========
    public static class Enum {

        public static <T extends java.lang.Enum<T>> T to(Object value, Class<T> enumType, T defaultValue) {
            if (value == null || enumType == null) {
                return defaultValue;
            }

            // 暂时只支持int和string转换
            if (value instanceof Integer) {
                return to((Integer)value, enumType, defaultValue);
            } else if (value instanceof CharSequence) {
                return to((CharSequence)value, enumType, defaultValue);
            }
            return defaultValue;
        }


        public static <T extends java.lang.Enum<T>> T to(Integer ordinal, Class<T> enumType, T defaultValue) {
            if (ordinal == null || enumType == null) {
                return defaultValue;
            }

            // 采用EnumMapping进行转换
            if (EnumMapping.class.isAssignableFrom(enumType)) {
                T[] values = enumType.getEnumConstants();
                for (int i = 0; i < values.length; i++) {
                    if (ordinal == ((EnumMapping)values[i]).intValue()) {
                        return values[i];
                    }
                }
            } else {// 默认ordinal
                T[] values = enumType.getEnumConstants();
                for (int i = 0; i < values.length; i++) {
                    if (ordinal == values[i].ordinal()) {
                        return values[i];
                    }
                }
            }
            return defaultValue;
        }

        public static <T extends java.lang.Enum<T>> T to(CharSequence name, Class<T> enumType, T defaultValue) {
            if (name == null || enumType == null) {
                return defaultValue;
            }
            try {
                return java.lang.Enum.valueOf(enumType, name.toString());
            } catch (Throwable e) {}
            return defaultValue;
        }
    }


    // =========to FLOAT=========
    public static class FLOAT {

        public static java.lang.Float to(Object value, java.lang.Float defaultValue) {
            java.lang.Double result = DOUBLE.to(value, defaultValue != null ? java.lang.Double.valueOf(defaultValue) : null);
            if (result == null) {
                return defaultValue;
            }

            if (result < java.lang.Float.MIN_VALUE || result > java.lang.Float.MAX_VALUE) {
                return defaultValue;
            }

            return result.floatValue();
        }
    }

    // =========to DOUBLE=========
    public static class DOUBLE {
        public static java.lang.Double to(java.lang.Integer value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.doubleValue();
        }

        public static java.lang.Double to(java.lang.Short value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.doubleValue();
        }

        public static java.lang.Double to(java.lang.Long value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.doubleValue();
        }

        public static java.lang.Double to(java.lang.Float value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.doubleValue();
        }

        public static java.lang.Double to(java.lang.Double value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value;
        }

        public static java.lang.Double to(Byte value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value.doubleValue();
        }

        public static java.lang.Double to(java.lang.Boolean value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value ? 1d : 0d;
        }

        public static java.lang.Double to(Character value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }

            if (BOOL.isFalseFeature(value)) {
                return 1d;
            } else if (BOOL.isFalseFeature(value)) {
                return 0d;
            }

            if (value < '0' || value > '9') {
                return defaultValue;
            }
            return (double)(value - '0');
        }

        public static java.lang.Double to(CharSequence value, java.lang.Double defaultValue) {
            if (value == null || value.length() == 0) {
                return defaultValue;
            }

            if (BOOL.isTrueFeature(value)) {
                return 1d;
            } else if (BOOL.isFalseFeature(value)) {
                return 0d;
            }

            try {
                return java.lang.Double.parseDouble(value.toString());
            } catch (Throwable e) { }
            return defaultValue;
        }

        public static java.lang.Double to(Object value, java.lang.Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof CharSequence) {
                return to((CharSequence)value, defaultValue);
            } else if (value instanceof java.lang.Double) {
                return (java.lang.Double)value;
            } else if (value instanceof java.lang.Float) {
                return to((java.lang.Float)value, defaultValue);
            } else if (value instanceof java.lang.Long) {
                return to((java.lang.Long)value, defaultValue);
            } else if (value instanceof java.lang.Short) {
                return to((java.lang.Short)value, defaultValue);
            } else if (value instanceof java.lang.Integer) {
                return to((java.lang.Integer)value, defaultValue);
            } else if (value instanceof Byte) {
                return to((Byte)value, defaultValue);
            } else if (value instanceof java.lang.Boolean) {
                return to((java.lang.Boolean)value, defaultValue);
            } else if (value instanceof Character) {
                return to((Character)value, defaultValue);
            } else {
                return defaultValue;
            }
        }
    }
}
