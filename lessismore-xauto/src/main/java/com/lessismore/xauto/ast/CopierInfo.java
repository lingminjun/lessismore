package com.lessismore.xauto.ast;

import com.lessismore.xauto.copy.Copier;

import java.util.*;

public class CopierInfo {
    public final ClassInfo target;
    public final ClassInfo source;
    public final String packageName;
    public final String name;

    // 存放所有的赋值对象
    public final List<AssignInfo> assignInfos = new ArrayList<>();
    public final Set<String> imps = new HashSet<>();
    private final Map<String,MappingInfo> mappings = new HashMap<>();

    public final String superClassName = Copier.class.getName();
    public final String superClassSimpleName = Copier.class.getSimpleName();

    public String getClassName() {
        return packageName + "." + name;
    }

    public String getSimpleName() {
        return name;
    }

    public CopierInfo(ClassInfo target, ClassInfo source, Map<String,MappingInfo> mappings) {
        this(null, target, source, mappings);
    }

    public CopierInfo(String packageName, ClassInfo target, ClassInfo source, Map<String,MappingInfo> mappings) {
        this.packageName = (packageName != null && packageName.length() > 0) ? packageName : source.packageName + ".copier";
        this.target = target;
        this.source = source;
        this.name = source.name + "To" + target.name + "Copier";
        if (mappings != null && mappings.size() > 0) {
            this.mappings.putAll(mappings);
        }

        // 添加imports
        this.imps.add(source.getClassName());
        this.imps.add(target.getClassName());
        this.imps.add(Copier.class.getName());

        //System.out.println(this.name + "构建开始\n" + source.toString());

        // 初始化assignInfos
        List<FieldInfo> allFields = target.getReverseAllFields();
        for (FieldInfo left : allFields) {
            // 只有可访问的属性才会构建
            if (left.isPublic || (left.setter != null && left.setter.isPublic)) {
                String matchName = left.getFieldMatchName();
                FieldInfo rightField = null;
                MethodInfo rightMethod = null;
                // 针对配置mapping来取
                MappingInfo mappingInfo = this.mappings.get(left.name);
                if (mappingInfo != null && StringUtils.notEmpty(mappingInfo.from)) {
                    rightField = source.findMatchField(mappingInfo.from);
                    matchName = StringUtils.convertFieldMatchName(mappingInfo.from, left.type); // type参数有一些问题
                    rightMethod = source.findGetterMatchMethod(matchName, true);
                } else {
                    rightField = source.findMatchField(matchName);
                    rightMethod = source.findGetterMatchMethod(matchName, true);
                }
                //System.out.println(this.name + "::target." + left.name + " sourceField = " + (rightField != null) + " ; sourceMethid = " + (rightMethod != null));
                // 构建赋值对象
                AssignInfo assignInfo = new AssignInfo(left.setter, rightMethod, left, rightField, left.getter);
                if (mappingInfo != null && StringUtils.notEmpty(mappingInfo.expression)) {
                    assignInfo.expression = mappingInfo.expression;
                }
                this.assignInfos.add(assignInfo);
            }
        }
        //System.out.println(this.name + "构建结束");
    }

    public ClassInfo getTarget() {
        return target;
    }

    public ClassInfo getSource() {
        return source;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public List<AssignInfo> getAssignInfos() {
        return assignInfos;
    }

    public List<String> getImports() {
        return new ArrayList<>(this.imps);
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public String getSuperClassSimpleName() {
        return superClassSimpleName;
    }
}
