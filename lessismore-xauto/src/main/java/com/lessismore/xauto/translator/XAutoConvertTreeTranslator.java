package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoSource;
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

        // 查找可转换的对象
        List<ConvertInfo> convertInfos = getAllConverts(jcClassDecl);
        List<String> copiers = new ArrayList<>();
        if (convertInfos != null) {
            for (ConvertInfo convertInfo : convertInfos) {
                // 添加新的拷贝类
                CopierInfo copierInfo = makeWriteCopierInfo(classInfo, convertInfo);
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


    private abstract static class ConvertInfo {
        protected ClassInfo classInfo;
        protected Map<String,MappingInfo> mappings;
    }

    private static class TargetInfo extends ConvertInfo {
        public ClassInfo getTarget() {
            return classInfo;
        }
    }

    private static class SourceInfo extends ConvertInfo {
        public ClassInfo getSource() {
            return classInfo;
        }
    }

    private List<ConvertInfo> getAllConverts(JCTree.JCClassDecl jcClassDecl) {
        List<ConvertInfo> targets = new ArrayList<>();
        java.util.List<JCTree.JCAnnotation> annotations = getTargetAnnotations(jcClassDecl);
        java.util.List<JCTree.JCAnnotation> sourceAnnotations = getSourceAnnotations(jcClassDecl);
        if (sourceAnnotations != null) {
            annotations.addAll(sourceAnnotations);
        }

        if (annotations != null && annotations.size() > 0) {
            for (JCTree.JCAnnotation annotation : annotations) {
                ClassInfo target = null;
                ClassInfo source = null;
                String targetClassName = null;
                String sourceClassName = null;
                Map<String,MappingInfo> mapping = new HashMap<>();
                for (JCTree.JCExpression expression : annotation.args) {
                    if (expression instanceof JCTree.JCAssign) {//等式，直接取右侧
                        if (((JCTree.JCAssign) expression).lhs.toString().equals("target")) {
                            JCTree.JCFieldAccess targetField = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            target = getClassInfo(targetField.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("source")) {
                            JCTree.JCFieldAccess sourceField = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            source = getClassInfo(sourceField.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("targetClassName")) {
                            targetClassName = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("sourceClassName")) {
                            sourceClassName = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("mapping")) {
                            Map<String,MappingInfo> mp = Utils.parseMapping(((JCTree.JCAssign) expression).rhs);
                            if (mp != null && mp.size() > 0) {
                                mapping.putAll(mp);
                            }
                        }
                    }
                }

                // 取类名
                if (target == null && StringUtils.notEmpty(targetClassName)) {
                    target = getClassInfo(targetClassName);
                }

                // 取类名
                if (source == null && StringUtils.notEmpty(sourceClassName)) {
                    source = getClassInfo(sourceClassName);
                }

                if (target != null) {
                    TargetInfo targetInfo = new TargetInfo();
                    targetInfo.classInfo = target;
                    targetInfo.mappings = mapping;
                    targets.add(targetInfo);
                }

                if (source != null) {
                    SourceInfo sourceInfo = new SourceInfo();
                    sourceInfo.classInfo = source;
                    sourceInfo.mappings = mapping;
                    targets.add(sourceInfo);
                }
            }
        }

        return targets;
    }

    private java.util.List<JCTree.JCAnnotation> getTargetAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoConvert.class.getName());
        return getArgsAnnotations(autoConfiguration, XAutoTarget.class.getName());
    }

    private java.util.List<JCTree.JCAnnotation> getSourceAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoConvert.class.getName());
        return getArgsAnnotations(autoConfiguration, XAutoSource.class.getName());
    }


    private CopierInfo makeWriteCopierInfo(ClassInfo classInfo, ConvertInfo convertInfo) {
        CopierInfo copierInfo = null;
        if (convertInfo instanceof TargetInfo) {
            copierInfo = new CopierInfo(convertInfo.classInfo, classInfo, convertInfo.mappings);
        } else {
            copierInfo = new CopierInfo(classInfo, convertInfo.classInfo, convertInfo.mappings);
        }
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
