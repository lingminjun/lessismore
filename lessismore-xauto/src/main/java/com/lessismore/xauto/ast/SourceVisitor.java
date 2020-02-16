package com.lessismore.xauto.ast;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 编译解析过程记录特定的AST
 */
public class SourceVisitor extends TreeScanner<Void, Void> {

    private static class ClassNode {
        ClassTree classTree;

        List<MethodTree> methods = new ArrayList<MethodTree>();
        List<VariableTree> feilds = new ArrayList<VariableTree>();
        List<BlockTree> blocks = new ArrayList<BlockTree>();

        int scope; // 0:field,1:method,3:block
    }

    List<ImportTree> imports = new ArrayList<ImportTree>();

    private Stack<ClassNode> stack = new Stack<ClassNode>();
    private List<ClassNode> classes = new ArrayList<ClassNode>();

    /**
     * 获得编译后的方法语法树
     * @return
     */
    public List<MethodTree> getMethods() {
        if (!classes.isEmpty()) {
            return new ArrayList<MethodTree>(classes.get(0).methods);
        }
        return new ArrayList<MethodTree>();
    }

    /**
     * 获得编译后的属性语法树
     * @return
     */
    public List<VariableTree> getFields() {
        if (!classes.isEmpty()) {
            return new ArrayList<VariableTree>(classes.get(0).feilds);
        }
        return new ArrayList<VariableTree>();
    }

    /**
     * 获得编译后的类书法树
     * @return
     */
    public JCTree.JCClassDecl getClassDecl() {
        if (!classes.isEmpty()) {
            return (JCTree.JCClassDecl)(classes.get(0).classTree);
        }
        return null;
    }

    /**
     * 获得编译后的类书法树
     * @return
     */
    public JCTree.JCClassDecl getInnerClassDecl() {
        if (classes.size() > 1) {
            return (JCTree.JCClassDecl)(classes.get(1).classTree);
        }
        return null;
    }

    /**
     * 获得当前编译文件的import包列表
     * @return
     */
    public List<JCTree.JCImport> getImports() {
        List<JCTree.JCImport> list = new ArrayList<>();
        for (ImportTree tree : imports) {
            if (tree instanceof JCTree.JCImport) {
                list.add((JCTree.JCImport)tree);
            }
        }
        return list;
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree node, Void aVoid) {
        return super.visitCompilationUnit(node, aVoid);
    }

    @Override
    public Void visitClass(ClassTree node, Void aVoid) {
        formatPtrln("Class name: %s, extends: %s, implements: %s",
                node.getSimpleName(), node.getExtendsClause(), node.getImplementsClause());
        ClassNode classNode = new ClassNode();
        classNode.classTree = node;
        stack.push(classNode);
        classes.add(classNode);
        Void result = super.visitClass(node, aVoid);
        stack.pop();
        return result;
    }


    @Override
    public Void visitVariable(VariableTree node, Void aVoid) {
        formatPtrln("variable name: %s, type: %s, kind: %s", node.getName(), node.getType(), node.getKind());
        ClassNode classNode = stack.peek();
        if (classNode != null && classNode.scope == 0) {
            classNode.feilds.add(node);
        }
        return super.visitVariable(node,aVoid);
    }

    @Override
    public Void visitBlock(BlockTree node, Void aVoid) {
        formatPtrln("block static: %b, body: %s",
                node.isStatic(), node.toString().replaceAll("\\n",""));
        ClassNode classNode = stack.peek();
        if (classNode != null && classNode.scope == 0) {
            classNode.blocks.add(node);
            classNode.scope = 2;
        }
        Void result = super.visitBlock(node, aVoid);
        if (classNode != null) {
            classNode.scope = 0;
        }
        return result;
    }



    @Override
    public Void visitImport(ImportTree node, Void aVoid) {
        formatPtrln("import name: %s", node.getQualifiedIdentifier());
        imports.add(node);
        return super.visitImport(node, aVoid);
    }

    @Override
    public Void visitMethod(MethodTree node, Void aVoid) {
        //formatPtrln("method modify: %s, name: %s, params %s, body: %s, kind: %s",
//                node.getModifiers(), node.getName(), node.getParameters(), node.getBody().toString().replaceAll("\\n",""), node.getKind());
//        System.out.println("METHOD:[" + node + "]");
        ClassNode classNode = stack.peek();
        if (classNode != null && classNode.scope == 0) {
            classNode.methods.add(node);
            classNode.scope = 1;
        }
        Void result = super.visitMethod(node, aVoid);
        if (classNode != null) {
            classNode.scope = 0;
        }
        return result;
    }

    public static void formatPtrln(String format, Object... args) {
        //System.out.println(String.format(format, args));
    }
}
