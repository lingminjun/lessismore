package com.lessismore.xauto.ast;

public class MappingInfo {
    /**
     * 目标属性名：target.keyField
     */
    public final String field;

    /**
     * 原始属性名：source.fromField，
     */
    public final String from;

    /**
     * 原始属性值转化表达式：如 new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(#{fromValue})
     */
    public String expression;

    public MappingInfo(String field, String from) {
        this.field = field;
        this.from = from;
    }

    @Override
    public String toString() {
        return "{" + field + " => " + from + "}";
    }
}
