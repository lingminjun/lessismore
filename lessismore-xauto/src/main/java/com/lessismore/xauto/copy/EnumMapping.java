package com.lessismore.xauto.copy;

/**
 * 枚举指定映射值，不一定适用ordinal
 */
public interface EnumMapping {
    default int intValue() {
        if (this instanceof Enum) {
            return ((Enum) this).ordinal();
        } else {
            throw new RuntimeException("此协议仅仅适应于枚举类型");
        }
    }
}
