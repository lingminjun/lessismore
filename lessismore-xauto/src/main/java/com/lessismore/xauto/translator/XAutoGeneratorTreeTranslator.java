package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoGenerator;
import com.lessismore.xauto.annotation.XAutoTemplate;
import com.lessismore.xauto.ast.ClassInfo;
import com.lessismore.xauto.ast.ModelInfo;
import com.lessismore.xauto.ast.StringUtils;
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
        String classTemplate;
        String resourceTemplate;

        String classRelativePath;
        String resourceRelativePath;
        String metaInfoFileName;

        ModelInfo modelInfo;
    }

    private List<TemplateInfo> getAllTemplateInfos(JCTree.JCClassDecl jcClassDecl) {
        List<JCTree.JCAnnotation> converters = getTemplateAnnotations(jcClassDecl);
        List<TemplateInfo> templateInfos = new ArrayList<>();
        if (converters != null) {
            for (JCTree.JCAnnotation annotation : converters) {
                TemplateInfo templateInfo = new TemplateInfo();
                ClassInfo model = null;
                List<ClassInfo> others = new ArrayList<>();
                for (JCTree.JCExpression expression : annotation.args) {
                    if (expression instanceof JCTree.JCAssign) {
                        if (((JCTree.JCAssign) expression).lhs.toString().equals("className")) {
                            templateInfo.className = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("classTemplate")) {
                            templateInfo.classTemplate = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("resourceTemplate")) {
                            templateInfo.resourceTemplate = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("model")) {
                            JCTree.JCFieldAccess field = (JCTree.JCFieldAccess)((JCTree.JCAssign) expression).rhs;
                            model = getClassInfo(field.selected.type.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("others")) {
                            JCTree.JCExpression rhs = ((JCTree.JCAssign) expression).rhs;
                            if (rhs instanceof JCTree.JCFieldAccess) {
                                JCTree.JCFieldAccess field = (JCTree.JCFieldAccess)rhs;
                                ClassInfo m = getClassInfo(field.selected.type.toString());
                                if (m != null) {
                                    others.add(m);
                                }
                            } else if (rhs instanceof JCTree.JCNewArray) {
                                com.sun.tools.javac.util.List<JCTree.JCExpression> elems = ((JCTree.JCNewArray) rhs).elems;
                                for (JCTree.JCExpression elem : elems) {
                                    if (elem instanceof JCTree.JCFieldAccess) {
                                        JCTree.JCFieldAccess field = (JCTree.JCFieldAccess)elem;
                                        ClassInfo m = getClassInfo(field.selected.type.toString());
                                        if (m != null) {
                                            others.add(m);
                                        }
                                    }
                                }
                            }
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("classRelativePath")) {
                            templateInfo.classRelativePath = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("resourceRelativePath")) {
                            templateInfo.resourceRelativePath = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        } else if (((JCTree.JCAssign) expression).lhs.toString().equals("metaInfoFileName")) {
                            templateInfo.metaInfoFileName = valueString(((JCTree.JCAssign) expression).rhs.toString());
                        }
                    }
                }

                if (model != null && (StringUtils.notEmpty(templateInfo.className) && StringUtils.notEmpty(templateInfo.classTemplate) || StringUtils.notEmpty(templateInfo.resourceTemplate))) {
                    templateInfo.modelInfo = new ModelInfo(model);
                    if (!others.isEmpty()) {
                        for (ClassInfo clz : others) {
                            templateInfo.modelInfo.others.put(clz.getClassName(),clz);
                        }
                    }
                    templateInfos.add(templateInfo);
                }
            }
        }

        return templateInfos;
    }

    private List<JCTree.JCAnnotation> getTemplateAnnotations(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoConfiguration = getAnnotation(jcClassDecl.mods.annotations, XAutoGenerator.class.getName());
        return getArgsAnnotations(autoConfiguration, XAutoTemplate.class.getName());
    }

    private void generatorTemplate(TemplateInfo templateInfo) {
        if (StringUtils.notEmpty(templateInfo.className)) {
            String name = resolver.resolverStringTemplate(templateInfo.className, templateInfo.modelInfo);
            if (name != null && name.length() > 0) {
                int idx = templateInfo.classTemplate.lastIndexOf(".");
                String extension = "";
                String template = templateInfo.classTemplate;
                if (idx > 0 && idx < templateInfo.classTemplate.length()) {
                    extension = templateInfo.classTemplate.substring(idx + 1);
                    template = templateInfo.classTemplate.substring(0,idx);
                }

                String codes = resolver.resolver(template, extension, templateInfo.modelInfo);
                if (codes != null && codes.length() > 0) {
                    if (StringUtils.notEmpty(templateInfo.classRelativePath)) {
                        writeJavaSourceFile(templateInfo.classRelativePath, name, codes);
                    } else {
                        writeJavaSourceFile(name, codes);
                    }

                    // 添加resource资源
                    if (StringUtils.notEmpty(templateInfo.metaInfoFileName)) {
                        appendServiceResourceFile(templateInfo.metaInfoFileName, name);
                    }
                }
            }
        }

        if (StringUtils.notEmpty(templateInfo.resourceTemplate)) {
            int idx = templateInfo.resourceTemplate.lastIndexOf(".");
            String extension = "";
            String template = templateInfo.resourceTemplate;
            if (idx > 0 && idx < templateInfo.resourceTemplate.length()) {
                extension = templateInfo.resourceTemplate.substring(idx + 1);
                template = templateInfo.resourceTemplate.substring(0,idx);
            }

            String content = resolver.resolver(template, extension, templateInfo.modelInfo);
            if (content != null && content.length() > 0) {
                if (StringUtils.notEmpty(templateInfo.resourceRelativePath)) {
                    writeResourceFile(templateInfo.resourceRelativePath, content);
                } else {
                    writeResourceFile(templateInfo.resourceRelativePath, content);
                }
            }
        }
    }

}
