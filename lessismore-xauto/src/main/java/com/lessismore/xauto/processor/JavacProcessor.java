package com.lessismore.xauto.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

// https://www.jianshu.com/p/67abc0edf408
abstract class JavacProcessor extends AbstractProcessor {

    protected Messager messager;  //编译期打log用的
    protected Context context;
    protected JavacTrees trees;   //待处理的抽象语法树
    protected TreeMaker maker;//创建AST节点的一些方法
    protected Names names;        //创建标识符的方法

    protected Elements elementUtils; // .getTypeElement == ClassSymbol
    protected Types typeUtils; // JavacTypes
    protected FileObjectManager filer;

    protected Logger log;
    private int round = 1;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        this.context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.filer = new FileObjectManager(processingEnv.getFiler());
//        boolean verbose = Boolean.valueOf( processingEnv.getOptions().get( "verbose" ) );
        this.log = new Logger(messager);
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (round > 1) {// 只在第一轮打印
            return false;
        }
        return processAnnotations(annotations, roundEnv, round++);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    protected abstract boolean processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, int round);
}
