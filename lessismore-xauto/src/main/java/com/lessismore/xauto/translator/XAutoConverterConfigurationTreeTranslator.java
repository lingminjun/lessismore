package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoConverterConfiguration;
import com.lessismore.xauto.ast.ClassInfo;
import com.lessismore.xauto.ast.ConvertersInfo;
import com.lessismore.xauto.ast.CopierInfo;
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
import java.util.List;


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
                if (annotation.args.size() == 2) {
                    ClassInfo target = null;
                    ClassInfo source = null;
                    if (annotation.args.get(0) instanceof JCTree.JCAssign) {//等式，直接取右侧
                        if (((JCTree.JCAssign) annotation.args.get(0)).lhs.toString().equals("target")) {
                            JCTree.JCFieldAccess targetField = (JCTree.JCFieldAccess)((JCTree.JCAssign) annotation.args.get(0)).rhs;
                            JCTree.JCFieldAccess sourceField = (JCTree.JCFieldAccess)((JCTree.JCAssign) annotation.args.get(1)).rhs;
                            target = getClassInfo(targetField.selected.type.toString());
                            source = getClassInfo(sourceField.selected.type.toString());
                        } else {
                            JCTree.JCFieldAccess targetField = (JCTree.JCFieldAccess)((JCTree.JCAssign) annotation.args.get(1)).rhs;
                            JCTree.JCFieldAccess sourceField = (JCTree.JCFieldAccess)((JCTree.JCAssign) annotation.args.get(0)).rhs;
                            target = getClassInfo(targetField.selected.type.toString());
                            source = getClassInfo(sourceField.selected.type.toString());
                        }
                    }
                    if (target != null && source != null) {//等式，直接取右侧
//                        System.out.println("target==>"+target.toString());
//                        System.out.println("source==>"+target.toString());
                        CopierInfo copierInfo = new CopierInfo(target,source);
                        targetAndSources.add(copierInfo);
                    }
                }
            }
        }

        return targetAndSources;
    }

    private java.util.List<JCTree.JCAnnotation> getConverterAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoConverterConfiguration.class.getName());
        java.util.List<JCTree.JCAnnotation> converters = new ArrayList<JCTree.JCAnnotation>();
        for (JCTree.JCExpression expression : autoConfiguration.args) {
            if (expression instanceof JCTree.JCAssign) {//等式，直接取右侧
                JCTree.JCExpression rhs = ((JCTree.JCAssign) expression).rhs;
                if (rhs instanceof JCTree.JCAnnotation) {
                    converters.add((JCTree.JCAnnotation)rhs);
                } else if (rhs instanceof JCTree.JCNewArray) {// 数组，多个
                    com.sun.tools.javac.util.List<JCTree.JCExpression> elems = ((JCTree.JCNewArray) rhs).elems;
                    for (JCTree.JCExpression elem : elems) {
                        if (elem instanceof JCTree.JCAnnotation) {
                            converters.add((JCTree.JCAnnotation)elem);
                        }
                    }
                }
            }
        }
        return converters;
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
