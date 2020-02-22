package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoConverter;
import com.lessismore.xauto.annotation.XAutoConverterConfiguration;
import com.lessismore.xauto.ast.*;
import com.lessismore.xauto.copy.ConverterConfiguration;
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


public class XAutoConverterConfigurationTreeTranslator extends AbstractTreeTranslator {

    public XAutoConverterConfigurationTreeTranslator(Context context, FileObjectManager filer, Elements elements, Types types, TreeMaker maker, Names names, Messager messager) {
        super(context,filer, elements, types, maker, names, messager);
    }

    @Override
    protected void translateClass(JCTree.JCClassDecl jcClassDecl, ClassInfo classInfo) {

        // 查找可转换的目标对象
        List<CopierInfo> copierInfos = getAllCopiers(jcClassDecl);

        String configuration = null;
        if (copierInfos != null && copierInfos.size() > 0) {
            ConvertersInfo convertersInfo = new ConvertersInfo(classInfo, copierInfos);
            configuration = makeWriteConvertersConfiguration(convertersInfo);
        }

        // 写service
        if (configuration != null) {
            appendConfigurationService(configuration);
        }
    }

    private List<CopierInfo> getAllCopiers(JCTree.JCClassDecl jcClassDecl) {

        java.util.List<JCTree.JCAnnotation> converters = getConverterAnnotations(jcClassDecl);
        List<CopierInfo> targetAndSources = new ArrayList<>();
        if (converters != null) {
            for (JCTree.JCAnnotation annotation : converters) {
                // target = User.class, source = Person.class
                ClassInfo target = null;
                ClassInfo source = null;
                Map<String,MappingInfo> mapping = new HashMap<>();
                for (JCTree.JCExpression expression : annotation.args) {
                    if (expression instanceof JCTree.JCAssign) {//等式，直接取右侧
                        if (((JCTree.JCAssign) expression).lhs.toString().equals("target")) {
                            JCTree.JCFieldAccess targetField = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            target = getClassInfo(targetField.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("source")) {
                            JCTree.JCFieldAccess sourceField = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            source = getClassInfo(sourceField.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("mapping")) {
                            Map<String,MappingInfo> mp = Utils.parseMapping(((JCTree.JCAssign) expression).rhs);
                            if (mp != null && mp.size() > 0) {
                                mapping.putAll(mp);
                            }
                        }
                    }
                }

                if (target != null && source != null) {//等式，直接取右侧
                    CopierInfo copierInfo = new CopierInfo(target,source,mapping);
                    targetAndSources.add(copierInfo);
                }
            }
        }

        return targetAndSources;
    }

    private java.util.List<JCTree.JCAnnotation> getConverterAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoConverterConfiguration.class.getName());
        return getArgsAnnotations(autoConfiguration, XAutoConverter.class.getName());
    }

    private String makeWriteConvertersConfiguration(ConvertersInfo convertersInfo) {
        String copierSourceCodes = resolver.resolver("xauto/ftl/converter_configuration", "ftl", convertersInfo);
        writeSourceFile(convertersInfo.getClassName(), copierSourceCodes.toString());
        return convertersInfo.getClassName();
    }

    private void appendConfigurationService(String configuration) {
        String sourceName = ConverterConfiguration.class.getName();
        StringBuilder builder = new StringBuilder();
        builder.append(configuration).append("\n");
        appendServiceResourceFile(sourceName,builder.toString());
    }
}
