package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoGenerator;
import com.lessismore.xauto.ast.ClassInfo;
import com.lessismore.xauto.processor.FileObjectManager;
import com.lessismore.xauto.wirter.FreemarkerTemplateResolver;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;


public class XAutoGeneratorTreeTranslator extends AbstractTreeTranslator {

    private FreemarkerTemplateResolver resolver = new FreemarkerTemplateResolver();

    public XAutoGeneratorTreeTranslator(Context context, FileObjectManager filer, Elements elements, Types types, TreeMaker maker, Names names, Messager messager) {
        super(context,filer, elements, types, maker, names, messager);
    }

    @Override
    protected void translateClass(JCTree.JCClassDecl jcClassDecl, ClassInfo classInfo) {

        // 查找可转换的目标对象
        List<TemplateInfo> templateInfos = getAllTemplateInfos(jcClassDecl);
        for (TemplateInfo templateInfo : templateInfos) {
            generatorTemplate(templateInfo);
        }
    }

    private static class TemplateInfo {
        String className;
        String template;
        String suffix = "ftl";
        ClassInfo model;
        String out;
    }

    private List<TemplateInfo> getAllTemplateInfos(JCTree.JCClassDecl jcClassDecl) {
        List<JCTree.JCAnnotation> converters = getTemplateAnnotations(jcClassDecl);
        List<TemplateInfo> templateInfos = new ArrayList<>();
        if (converters != null) {
            for (JCTree.JCAnnotation annotation : converters) {
                TemplateInfo templateInfo = new TemplateInfo();
                for (JCTree.JCExpression expression : annotation.args) {
                    if (expression instanceof JCTree.JCAssign) {
                        if (((JCTree.JCAssign) expression).lhs.toString().equals("className")) {
                            templateInfo.className = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("template")) {
                            templateInfo.template = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("suffix")) {
                            templateInfo.suffix = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("model")) {
                            JCTree.JCFieldAccess field = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            templateInfo.model = getClassInfo(field.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("out")) {
                            templateInfo.out = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        }
                    }
                }

                if (templateInfo.model != null && templateInfo.className != null && templateInfo.className.length() > 0 && templateInfo.template != null && templateInfo.template.length() > 0) {
                    templateInfos.add(templateInfo);
                }
            }
        }

        return templateInfos;
    }

    private List<JCTree.JCAnnotation> getTemplateAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoGenerator.class.getName());
        List<JCTree.JCAnnotation> converters = new ArrayList<JCTree.JCAnnotation>();
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

    private void generatorTemplate(TemplateInfo templateInfo) {
        String name = resolver.resolverStringTemplate(templateInfo.className, templateInfo.model);
        if (name != null && name.length() > 0) {
            String codes = resolver.resolver(templateInfo.template, templateInfo.suffix, templateInfo.model);
            if (codes != null && codes.length() > 0) {
                writeSourceFile(name, codes);
            }
        }
    }

}
