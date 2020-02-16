package com.lessismore.xauto.processor;

import com.lessismore.xauto.annotation.XAutoGenerator;
import com.lessismore.xauto.translator.XAutoConverterConfigurationTreeTranslator;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * XAutoGenerator自定义增强器
 */
public class XAutoGeneratorProcessor extends JavacProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(XAutoGenerator.class.getName());
        return sets;
    }

    @Override
    protected boolean processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, int round) {
        // 标记需要自动增强实体
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XAutoGenerator.class);
        elements.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new XAutoConverterConfigurationTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager));
        });

        return false;
    }
}