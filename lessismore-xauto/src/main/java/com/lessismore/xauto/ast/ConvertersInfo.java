package com.lessismore.xauto.ast;

import com.lessismore.xauto.copy.ConverterConfiguration;
import com.lessismore.xauto.copy.Copier;
import com.lessismore.xauto.copy.CopierInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvertersInfo {
    public final String packageName;
    public final String name;
    public final ClassInfo configuration;

    // 存放所有的赋值对象
    public final List<CopierInfo> copiers = new ArrayList<>();
    // 存放所有的赋值对象
    public final List<AssignInfo> assignInfos = new ArrayList<>();
    public final Set<String> imps = new HashSet<>();

    public String getClassName() {
        return packageName + "." + name;
    }

    public String getSimpleName() {
        return name;
    }

    public ConvertersInfo(ClassInfo configuration, List<CopierInfo> copiers) {
        this(null, configuration, copiers);
    }
    public ConvertersInfo(String packageName, ClassInfo configuration, List<CopierInfo> copiers) {
        this.configuration = configuration;
        this.packageName = (packageName != null && packageName.length() > 0) ? packageName : configuration.packageName + ".configuration";
        this.copiers.addAll(copiers);
        this.name = configuration.name + "AutoConfiguration";

        this.imps.add(ConverterConfiguration.class.getName());
        this.imps.add(Copier.class.getName());
        this.imps.add(CopierInterface.class.getName());
        this.imps.add(List.class.getName());
        this.imps.add(ArrayList.class.getName());
        for (CopierInfo copier : copiers) {
            this.imps.addAll(copier.imps);
        }
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

    public ClassInfo getConfiguration() {
        return configuration;
    }

    public List<CopierInfo> getCopiers() {
        return copiers;
    }

    public Set<String> getImps() {
        return imps;
    }
}
