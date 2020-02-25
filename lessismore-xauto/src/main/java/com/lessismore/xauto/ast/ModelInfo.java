package com.lessismore.xauto.ast;

import java.util.*;


public class ModelInfo {

    public final ClassInfo model;
    // className => classInfo
    public final Map<String,ClassInfo> others = new HashMap<>();

    public ModelInfo(ClassInfo model) {
        this.model = model;
    }

    public ClassInfo getModel() {
        return model;
    }

    public Map<String, ClassInfo> getOthers() {
        return others;
    }
}
