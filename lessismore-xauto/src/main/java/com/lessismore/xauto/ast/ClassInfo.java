package com.lessismore.xauto.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ClassInfo {
    public final String packageName;
    public final String name;

    // 父类结构
    public ClassInfo superClass;

    public ClassInfo(String fullName) {
        int idx = fullName.lastIndexOf(".");
        this.packageName = fullName.substring(0,idx);
        this.name = fullName.substring(idx+1);
    }
    public ClassInfo(String pkg, String name) {
        this.packageName = pkg;
        this.name = name;
    }

    public String getClassName() {
        return packageName + "." + name;
    }

    public String getSimpleName() {
        return name;
    }

    public final List<FieldInfo> staticFields = new ArrayList<>();
    public final List<MethodInfo> staticMethods = new ArrayList<>();

    public final List<FieldInfo> fields = new ArrayList<>();
    public final List<MethodInfo> methods = new ArrayList<>();

    public List<FieldInfo> getReverseFields() {
        List<FieldInfo> list = new ArrayList<>(fields);
        Collections.reverse(list);
        return list;
    }

    // 构造方法
    public final List<MethodInfo> constructMethods = new ArrayList<>();

    public List<FieldInfo> getAllFields() {
        List<FieldInfo> fields = new ArrayList<>(this.fields);
        ClassInfo sp = superClass;
        while (sp != null) {
            fields.addAll(sp.fields);
            sp = sp.superClass;
        }
        return fields;
    }

    public List<FieldInfo> getReverseAllFields() {
        List<FieldInfo> list = new ArrayList<>(getAllFields());
        Collections.reverse(list);
        return list;
    }

    public FieldInfo findField(String name) {
        for (FieldInfo field : fields) {
            if (field.name.equals(name)) {
                return field;
            }
        }
        for (FieldInfo field : staticFields) {
            if (field.name.equals(name)) {
                return field;
            }
        }

        if (superClass != null) {
            return superClass.findField(name);
        }

        return null;
    }

    public List<MethodInfo> findMethods(String name) {
        List<MethodInfo> ms = new ArrayList<>();
        for (MethodInfo method : methods) {
            if (method.name.equals(name)) {
                ms.add(method);
            }
        }
        for (MethodInfo method : staticMethods) {
            if (method.name.equals(name)) {
                ms.add(method);
            }
        }

        if (superClass != null) {
            ms.addAll(superClass.findMethods(name));
        }

        return ms;
    }


    public FieldInfo findMatchField(String matchName) {
        for (FieldInfo field : fields) {
            if (field.getFieldMatchName().equals(matchName)) {
                return field;
            }
        }

        if (superClass != null) {
            return superClass.findMatchField(matchName);
        }

        return null;
    }

    public MethodInfo findGetterMatchMethod(String matchName, boolean isPublic) {
        for (MethodInfo method : methods) {
            if (method.isGetter() && method.getFieldMatchName().equals(matchName)) {
                if (isPublic) {
                    if (method.isPublic) {
                        return method;
                    }
                } else {
                    return method;
                }
            }
        }

        if (superClass != null) {
            return superClass.findGetterMatchMethod(matchName, isPublic);
        }

        return null;
    }

    public MethodInfo findSetterMatchMethod(String matchName, boolean isPublic) {
        for (MethodInfo method : methods) {
            if (method.isSetter() && method.getFieldMatchName().equals(matchName)) {
                if (isPublic) {
                    if (method.isPublic) {
                        return method;
                    }
                } else {
                    return method;
                }
            }
        }

        if (superClass != null) {
            return superClass.findSetterMatchMethod(matchName, isPublic);
        }

        return null;
    }

    public MethodInfo findMethod(String methodSignature) {
        return findMethod(methodSignature,true);
    }

    public MethodInfo findMethod(String methodSignature, boolean inherit) {
        for (MethodInfo method : methods) {
            if (method.signature.equals(methodSignature)) {
                return method;
            }
        }
        for (MethodInfo method : staticMethods) {
            if (method.signature.equals(methodSignature)) {
                return method;
            }
        }

        if (inherit && superClass != null) {
            return superClass.findMethod(methodSignature, inherit);
        }

        return null;
    }

    public boolean hasMethod(String methodSignature) {
        return hasMethod(methodSignature, false);
    }

    public boolean hasMethod(String methodSignature, boolean inherit) {
        return findMethod(methodSignature, inherit) != null;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public ClassInfo getSuperClass() {
        return superClass;
    }

    public List<FieldInfo> getStaticFields() {
        return staticFields;
    }

    public List<MethodInfo> getStaticMethods() {
        return staticMethods;
    }

    public List<FieldInfo> getFields() {
        return fields;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public List<MethodInfo> getConstructMethods() {
        return constructMethods;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ");
        builder.append(packageName);
        builder.append("\nclass ");
        builder.append(name);
        if (superClass != null) {
            builder.append(" extends ");
            builder.append(superClass.getClassName());
        }
        builder.append(" {\n");
        if (!staticFields.isEmpty()) {
            builder.append("\n\t/** 静态属性 **/\n");
            for (FieldInfo fieldInfo : staticFields) {
                builder.append("\t");
                builder.append(fieldInfo.toString());
                builder.append("; // matchName=");
                builder.append(fieldInfo.getFieldMatchName());
                builder.append("\n");
            }
        }
        if (!fields.isEmpty()) {
            builder.append("\n\t/** 属性 **/\n");
            for (FieldInfo fieldInfo : fields) {
                builder.append("\t");
                builder.append(fieldInfo.toString());
                builder.append("; // matchName=");
                builder.append(fieldInfo.getFieldMatchName());
                builder.append("\n");
            }
        }
        if (!staticMethods.isEmpty()) {
            builder.append("\n\t/** 静态方法 **/\n");
            for (MethodInfo methodInfo : staticMethods) {
                builder.append("\t");
                builder.append(methodInfo.toString());
                builder.append("; // matchName=");
                builder.append(methodInfo.getFieldMatchName());
                builder.append("\n");
            }
        }
        if (!constructMethods.isEmpty()) {
            builder.append("\n\t/** 构造方法 **/\n");
            for (MethodInfo methodInfo : constructMethods) {
                builder.append("\t");
                builder.append(methodInfo.toString());
                builder.append(";\n");
            }
        }
        if (!methods.isEmpty()) {
            builder.append("\n\t/** 实例方法 **/\n");
            for (MethodInfo methodInfo : methods) {
                builder.append("\t");
                builder.append(methodInfo.toString());
                builder.append("; // matchName=");
                builder.append(methodInfo.getFieldMatchName());
                builder.append("\n");
            }
        }

        builder.append("}\n");

        if (superClass != null) {
            builder.append("\n\n");
            builder.append(superClass.toString());
        }
        return builder.toString();
    }
}
