package com.lessismore.xauto.ast;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
    public MethodInfo(String signature) {
        this.signature = signature;
        int idx = signature.indexOf("(");
        this.name = signature.substring(0, idx);
    }

    public MethodInfo(String name, List<String> paramTypes) {
        StringBuilder builder = new StringBuilder("(");
        for (String type : paramTypes) {
            if (builder.length() > 1) {
                builder.append(",");
            }
            builder.append(type);
        }
        builder.append(")");
        this.signature = name + builder.toString();
        this.name = name;
    }

    public final String signature;
    public final String name;
    public String returnType;

    public boolean isStatic;
    public boolean isPublic;
    public boolean isPrivate;

    public final List<ParamInfo> params = new ArrayList<>();

    // 专门用于匹配赋值用的属性名
    private String matchFieldName;
    public String getMatchFieldName() {
        if (matchFieldName != null) {
            return matchFieldName;
        }

        String subName = name;
        if (name.startsWith("is")) {
            // 仅仅当时getter
            if (isGetter()) {
                subName = name.substring(2);
            }
        } else if (name.startsWith("get")) {
            // 仅仅当时getter
            if (isGetter()) {
                subName = name.substring(3);
            }
        } else if (name.startsWith("set")) {
            // 仅仅当时setter
            if (isSetter()) {
                subName = name.substring(3);
            }
        }

        matchFieldName = subName;
        return matchFieldName;
    }

    public boolean isGetter() {
        if (params.size() == 0 && returnType != null && !returnType.equals("void")) {
            if (name.startsWith("is")) {
                if (StringUtils.isLowerCase(name, 2) && StringUtils.isUpperCase(name, 3)) {
                    return true;
                } else if (StringUtils.isUpperCase(name, 2)) {
                    return true;
                } else {// 无法截取is
                    return false;
                }
            } else if (name.startsWith("get")) {
                if (StringUtils.isLowerCase(name, 3) && StringUtils.isUpperCase(name, 4)) {
                    return true;
                } else if (StringUtils.isUpperCase(name, 3)) {
                    return true;
                } else {// 无法截取get
                    return false;
                }
            }
        }
        return false;
    }

    public boolean isSetter() {
        // 仅仅当时getter
        if (params.size() == 1 && (returnType == null || returnType.equals("void"))) {
            if (name.startsWith("set")) {
                if (StringUtils.isLowerCase(name, 3) && StringUtils.isUpperCase(name, 4)) {
                    return true;
                } else if (StringUtils.isUpperCase(name, 3)) {
                    return true;
                } else {// 无法截取set
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(isPrivate?"private ":(isPublic?"public ":"protected "));
        builder.append(isStatic?"static ":"");
        builder.append(returnType != null? returnType : "");
        builder.append(" ");
        builder.append(name);
        builder.append("(");
        boolean isFirst = true;
        for (ParamInfo param : params) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(", ");
            }
            builder.append(param.toString());
        }
        builder.append(")");
        return builder.toString();
    }

    public String getSignature() {
        return signature;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public List<ParamInfo> getParams() {
        return params;
    }
}
