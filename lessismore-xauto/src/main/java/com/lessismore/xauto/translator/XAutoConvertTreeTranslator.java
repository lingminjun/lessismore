package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoTarget;
import com.lessismore.xauto.ast.ClassInfo;
import com.lessismore.xauto.ast.CopierInfo;
import com.lessismore.xauto.ast.MappingInfo;
import com.lessismore.xauto.ast.StringUtils;
import com.lessismore.xauto.copy.CopierInterface;
import com.lessismore.xauto.processor.FileObjectManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XAutoConvertTreeTranslator extends AbstractTreeTranslator {

    public XAutoConvertTreeTranslator(Context context, FileObjectManager filer, Elements elements, Types types, TreeMaker maker, Names names, Messager messager) {
        super(context,filer, elements, types, maker, names, messager);
    }

    @Override
    protected void translateClass(JCTree.JCClassDecl jcClassDecl, ClassInfo classInfo) {

        // 查找可转换的目标对象
        List<TargetInfo> targets = getTargets(jcClassDecl);
        List<String> copiers = new ArrayList<>();
        if (targets != null) {
            for (TargetInfo target : targets) {
                // 添加新的拷贝类
                CopierInfo copierInfo = makeWriteCopierInfo(classInfo, target);
                if (classInfo != null) {
                    copiers.add(copierInfo.getClassName());
                }
            }
        }

        // 写service
        if (copiers.size() > 0) {
            appendCopierService(copiers);
        }
    }

    private static class TargetInfo {
        ClassInfo target;
        Map<String,MappingInfo> mappings;
    }

    private List<TargetInfo> getTargets(JCTree.JCClassDecl jcClassDecl) {
        List<TargetInfo> targets = new ArrayList<>();
        java.util.List<JCTree.JCAnnotation> targetAnnotations = getTargetAnnotations(jcClassDecl);
        if (targetAnnotations != null && targetAnnotations.size() > 0) {

            for (JCTree.JCAnnotation annotation : targetAnnotations) {
                ClassInfo target = null;
                String targetClassName = null;
                Map<String,MappingInfo> mapping = new HashMap<>();
                for (JCTree.JCExpression expression : annotation.args) {
                    if (expression instanceof JCTree.JCAssign) {//等式，直接取右侧
                        if (((JCTree.JCAssign) expression).lhs.toString().equals("target")) {
                            JCTree.JCFieldAccess targetField = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            target = getClassInfo(targetField.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("targetClassName")) {
                            targetClassName = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("mapping")) {
                            Map<String,MappingInfo> mp = Utils.parseMapping(((JCTree.JCAssign) expression).rhs);
                            if (mp != null && mp.size() > 0) {
                                mapping.putAll(mp);
                            }
                        }
                    }
                }

                // 去类名
                if (target == null && StringUtils.notEmpty(targetClassName)) {
                    target = getClassInfo(targetClassName);
                }

                if (target != null) {
                    TargetInfo targetInfo = new TargetInfo();
                    targetInfo.target = target;
                    targetInfo.mappings = mapping;
                    targets.add(targetInfo);
                }
            }
        }

        return targets;
    }

    private java.util.List<JCTree.JCAnnotation> getTargetAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoConvert.class.getName());
        return getArgsAnnotations(autoConfiguration, XAutoTarget.class.getName());
    }

    private CopierInfo makeWriteCopierInfo(ClassInfo sourceInfo, TargetInfo targetInfo) {
        CopierInfo copierInfo = new CopierInfo(targetInfo.target, sourceInfo, targetInfo.mappings);
        String copierSourceCodes = resolver.resolver("xauto/ftl/copier", "ftl", copierInfo);
        if (copierSourceCodes != null && copierSourceCodes.length() > 0) {
            writeJavaSourceFile(copierInfo.getClassName(), copierSourceCodes);
        } else {
            return null;
        }
        return copierInfo;
    }

    private void appendCopierService(List<String> copiers) {
        String sourceName = CopierInterface.class.getName();
        StringBuilder builder = new StringBuilder();
        for (String line : copiers) {
            builder.append(line).append("\n");
        }
        appendServiceResourceFile(sourceName,builder.toString());
    }
}
