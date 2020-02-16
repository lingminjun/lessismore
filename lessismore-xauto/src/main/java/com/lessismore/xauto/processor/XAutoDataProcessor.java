package com.lessismore.xauto.processor;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoAccessor;
import com.lessismore.xauto.annotation.XAutoGetter;
import com.lessismore.xauto.annotation.XAutoSetter;
import com.lessismore.xauto.translator.XAutoDataTreeTranslator;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * XAutoService增强
 */
public class XAutoDataProcessor extends JavacProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(XAutoGetter.class.getName());
        sets.add(XAutoSetter.class.getName());
        sets.add(XAutoAccessor.class.getName());
        sets.add(XAutoConvert.class.getName());
        return sets;
    }

    @Override
    public boolean processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, int round) {

        Set<JCTree> deals = new HashSet<>();
        // datas
        Set<? extends Element> datas1 = roundEnv.getElementsAnnotatedWith(XAutoConvert.class);
        datas1.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            if (deals.contains(jcTree)) {
                return;
            } else {
                deals.add(jcTree);
            }
            jcTree.accept(new XAutoDataTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager,true,true, true));
        });

        // datas
        Set<? extends Element> datas = roundEnv.getElementsAnnotatedWith(XAutoAccessor.class);
        datas.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            if (deals.contains(jcTree)) {
                return;
            } else {
                deals.add(jcTree);
            }
            jcTree.accept(new XAutoDataTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager,true,true, true));
        });

        // setter
        Set<? extends Element> setters = roundEnv.getElementsAnnotatedWith(XAutoSetter.class);
        setters.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            if (deals.contains(jcTree)) {
                return;
            }
            jcTree.accept(new XAutoDataTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager,true,false, false));
        });

        // getter
        Set<? extends Element> getters = roundEnv.getElementsAnnotatedWith(XAutoGetter.class);
        getters.forEach(element -> {
            //生成jCTree语法树
            JCTree jcTree = trees.getTree(element);
            if (deals.contains(jcTree)) {
                return;
            }
            jcTree.accept(new XAutoDataTreeTranslator(context,filer,elementUtils,typeUtils,maker,names,messager,false,true, false));
        });

        return false;
    }

}