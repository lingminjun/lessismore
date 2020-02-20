package com.lessismore.xauto.ast;

import com.lessismore.xauto.copy.Converters;
import com.sun.tools.javac.tree.JCTree;

public class FieldInfo {

    public FieldInfo(String name) {
        this.name = name;
    }

    public final String name;
    public String type;

    public boolean isStatic;
    public boolean isPublic;
    public boolean isPrivate;
    public boolean isFinal;
    public MethodInfo getter;
    public MethodInfo setter;

    public boolean autoGetter;
    public boolean autoSetter;

    public JCTree.JCVariableDecl var;

    public int pos = -1;

    // 标记忽略:主要 getter and setter
    public boolean ignore;

    public boolean isProtected() {
        return !isPrivate && !isPrivate;
    }

    public boolean hasGetter() {
        return getter != null;
    }

    public boolean hasSetter() {
        return setter != null;
    }

    private String getAccessMethodName(String name, String prefix) {
        if (StringUtils.isLowerCase(name, 0) && StringUtils.isUpperCase(name, 1)) {
            return prefix + name;
        } else {
            return prefix + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        }
    }

    // 注意特殊命名
    public String getGetterName() {
        if (getter != null) {
            return getter.name;
        }

        // 特殊的isXxx
        if ("boolean".equals(type) && name.startsWith("is")) {
            if (StringUtils.isUpperCase(name, 2)) {
                return name;
            }
        }

        if ("java.lang.Boolean".equals(type) && name.startsWith("is")) {
            if (StringUtils.isUpperCase(name, 2)) {
                return "get" + name.substring(2);
            }
        }

        if ("boolean".equals(type)) {
            return getAccessMethodName(name,"is");
        } else {
            return getAccessMethodName(name, "get");
        }
    }

    // 注意特殊命名 aXxx and isXxx
    public String getSetterName() {
        if (setter != null) {
            return setter.name;
        }

        // 特殊的isXxx
        if (("boolean".equals(type) || "java.lang.Boolean".equals(type)) && name.startsWith("is")) {
            if (StringUtils.isUpperCase(name, 2)) {
                return "set" + name.substring(2);
            }
        }

        return getAccessMethodName(name,"set");
    }

    public String getGetterSignature() {
        if (getter != null) {
            return getter.signature;
        }
        return getGetterName() + "()";
    }

    public String getSetterSignature() {
        if (setter != null) {
            return setter.signature;
        }
        return getSetterName() + "(" + type.toString() + ")";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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

    public MethodInfo getGetter() {
        return getter;
    }

    public MethodInfo getSetter() {
        return setter;
    }

    public boolean isIgnore() {
        return ignore;
    }

    // 专门用于匹配赋值用的属性名
    private String matchFieldName;
    public String getMatchFieldName() {
        if (matchFieldName != null) {
            return matchFieldName;
        }
        // is XXX 的情况
        if (("boolean".equals(type) || "java.lang.Boolean".equals(type)) && name.startsWith("is")) {
            if (StringUtils.isUpperCase(name, 2)) {
                matchFieldName = name.substring(2);
                return matchFieldName;
            }
        }

        // 小字母开头，如aXxx，保留
        if (StringUtils.isLowerCase(name, 0) && StringUtils.isUpperCase(name, 1)) {
            matchFieldName = name;
            return matchFieldName;
        }

        // 首字母大写
        matchFieldName = name.substring(0,1).toUpperCase();
        if (name.length() > 1) {
            matchFieldName +=  name.substring(1);
        }
        return matchFieldName;
    }

    public static MethodInfo createGetter(FieldInfo field) {
        MethodInfo method = new MethodInfo(field.getGetterName() + "()");
        method.isPublic = true;
        method.returnType = field.type;
        return method;
    }

    public static MethodInfo createSetter(FieldInfo field) {
        MethodInfo method = new MethodInfo(field.getSetterName() + "(" + field.type + ")");
        method.isPublic = true;
        ParamInfo paramInfo = new ParamInfo(field.name);
        paramInfo.type = field.type;
        method.params.add(paramInfo);
        method.returnType = Converters.Type.TYPE_VOID;
        return method;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(ignore?"ignore ":"");
        builder.append(isPrivate?"private ":(isPublic?"public ":"protected "));
        builder.append(isStatic?"static ":"");
        builder.append(type != null ? type  : "");
        builder.append(" ");
        builder.append(name);
        return builder.toString();
    }


}
