package com.lessismore.xauto.processor;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoConverterConfiguration;
import com.lessismore.xauto.translator.XAutoConvertTreeTranslator;
import com.lessismore.xauto.translator.XAutoConverterConfigurationTreeTranslator;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * XAutoService增强
 */
public class XAutoConvertProcessor extends JavacProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(XAutoConvert.class.getName());
        sets.add(XAutoConverterConfiguration.class.getName());
        return sets;
    }

    @Override
    protected boolean processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, int round) {
        // 标记需要自动增强实体
        Set<? extends Element> elements1 = roundEnv.getElementsAnnotatedWith(XAutoConvert.class);
        elements1.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new XAutoConvertTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager));
        });

        // 标记需要自动增强实体
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XAutoConverterConfiguration.class);
        elements.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new XAutoConverterConfigurationTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager));
        });

        return false;
    }
}