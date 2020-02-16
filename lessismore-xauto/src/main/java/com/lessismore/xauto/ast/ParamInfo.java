package com.lessismore.xauto.ast;

public class ParamInfo {

    public ParamInfo(String name) {
        this.name = name;
    }

    public final String name;
    public String type;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type != null? type : "");
        builder.append(" ");
        builder.append(name);
        return builder.toString();
    }
}
