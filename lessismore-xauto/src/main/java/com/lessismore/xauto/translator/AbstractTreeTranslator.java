package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.*;
import com.lessismore.xauto.ast.*;
import com.lessismore.xauto.copy.Converters;
import com.lessismore.xauto.processor.FileObjectManager;
import com.lessismore.xauto.processor.Logger;
import com.lessismore.xauto.wirter.FreemarkerTemplateResolver;
import com.lessismore.xauto.wirter.TemplateResolver;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractTreeTranslator extends TreeTranslator {

    protected Logger log;
    protected Context context;
    protected TreeMaker maker;//创建AST节点的一些方法
    protected Names names;        //创建标识符的方法
    protected Elements elements; // .getTypeElement == ClassSymbol
    protected Types types; // JavacTypes
    protected FileObjectManager filer;

    protected TemplateResolver resolver = new FreemarkerTemplateResolver();

    private boolean stack = false;
    private int step = 1;

    public AbstractTreeTranslator(Context context,FileObjectManager filer, Elements elements, Types types, TreeMaker maker,Names names, Messager messager) {
        this.context = context;
        this.filer = filer;
        this.elements = elements;
        this.types = types;
        this.maker = maker;
        this.names = names;
        this.log = new Logger(messager);
    }


    @Override
    public final void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        ClassInfo classInfo = parseClass(jcClassDecl);
//        System.out.println("visit class:\n" + classInfo.toString());
        super.visitClassDef(jcClassDecl);
        if (stack) {
            super.visitClassDef(jcClassDecl);
            return;
        } else {
            stack = true;
        }
        translateClass(jcClassDecl,classInfo);
        super.visitClassDef(jcClassDecl);
        stack = false;
    }

    private boolean hasAutoGetter(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation auto = getAnnotation(jcClassDecl.mods.annotations, XAutoConvert.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, XAutoAccessor.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, XAutoGetter.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, "lombok.Data");
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, "lombok.Getter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoSetter(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation auto = getAnnotation(jcClassDecl.mods.annotations, XAutoConvert.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, XAutoAccessor.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, XAutoSetter.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, "lombok.Data");
        if (auto != null) {
            return true;
        }
        auto = getAnnotation(jcClassDecl.mods.annotations, "lombok.Setter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoGetter(JCTree.JCVariableDecl jcVariableDecl) {
        JCTree.JCAnnotation auto = getAnnotation(jcVariableDecl.mods.annotations, "lombok.Getter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoSetter(JCTree.JCVariableDecl jcVariableDecl) {
        JCTree.JCAnnotation auto = getAnnotation(jcVariableDecl.mods.annotations, "lombok.Setter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoGetter(Symbol.ClassSymbol classSymbol) {
        Attribute.Compound auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), XAutoConvert.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), XAutoAccessor.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), XAutoGetter.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), "lombok.Data");
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), "lombok.Getter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoSetter(Symbol.ClassSymbol classSymbol) {
        Attribute.Compound auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), XAutoConvert.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), XAutoAccessor.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), XAutoSetter.class.getName());
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), "lombok.Data");
        if (auto != null) {
            return true;
        }
        auto = getCompoundAnnotation(classSymbol.getAnnotationMirrors(), "lombok.Setter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoGetter(Symbol.VarSymbol varSymbol) {
        Attribute.Compound auto = getCompoundAnnotation(varSymbol.getAnnotationMirrors(), "lombok.Getter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private boolean hasAutoSetter(Symbol.VarSymbol varSymbol) {
        Attribute.Compound auto = getCompoundAnnotation(varSymbol.getAnnotationMirrors(), "lombok.Setter");
        if (auto != null) {
            return true;
        }
        return false;
    }

    private ClassInfo parseClass(JCTree.JCClassDecl jcClassDecl) {
        ClassInfo classInfo = new ClassInfo(elements.getPackageOf(jcClassDecl.sym).asType().toString(), jcClassDecl.name.toString());

        Map<String, MethodInfo> staticMethods = new HashMap<>();
        Map<String, MethodInfo> instanceMethods = new HashMap<>();

        boolean classAutoGetter = hasAutoGetter(jcClassDecl);
        boolean classAutoSetter = hasAutoSetter(jcClassDecl);

        // 类定义中包含成员变量、成员函数和构造函数
        for (JCTree tree : jcClassDecl.defs) {

            // 只关注成员变量
            if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;

                FieldInfo fieldInfo = new FieldInfo(jcVariableDecl.name.toString());
                fieldInfo.var = jcVariableDecl;

                if (jcVariableDecl.vartype.type != null) {
                    fieldInfo.type = jcVariableDecl.vartype.type.toString();
                } else {
                    fieldInfo.type = jcVariableDecl.vartype.toString();
                }

                fieldInfo.isStatic = jcVariableDecl.mods.getFlags().contains(Modifier.STATIC);
                fieldInfo.isPublic = jcVariableDecl.mods.getFlags().contains(Modifier.PUBLIC);
                fieldInfo.isPrivate = jcVariableDecl.mods.getFlags().contains(Modifier.PRIVATE);
                fieldInfo.isFinal = jcVariableDecl.mods.getFlags().contains(Modifier.FINAL);
                fieldInfo.ignore = getAnnotation(jcVariableDecl.mods.annotations, XAutoIgnore.class.getName()) != null;

                fieldInfo.pos = jcVariableDecl.pos;

                fieldInfo.autoGetter = !fieldInfo.isStatic && (classAutoGetter || hasAutoGetter(jcVariableDecl));
                fieldInfo.autoSetter = !fieldInfo.isStatic && (classAutoSetter || hasAutoSetter(jcVariableDecl));

                if (fieldInfo.isStatic) {
                    classInfo.staticFields.add(fieldInfo);
                } else {
                    classInfo.fields.add(fieldInfo);
                }

            } else if (tree.getKind().equals(Tree.Kind.METHOD)) {
                JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;

                List<JCTree.JCVariableDecl> params = jcMethodDecl.getParameters();
                java.util.List<String> paramTypes = new ArrayList<>();
                java.util.List<ParamInfo> paramList = new ArrayList<>();
                if (params != null) {
                    for (JCTree.JCVariableDecl var : params) {
                        ParamInfo paramInfo = new ParamInfo(var.name.toString());
                        if (var.vartype.type != null) {
                            paramInfo.type = var.vartype.type.toString();
                            //paramInfo.type = var.vartype.toString();
                        } else {
                            paramInfo.type = var.vartype.toString();
                        }
                        paramTypes.add(paramInfo.type);
                        paramList.add(paramInfo);
                    }
                }

                MethodInfo methodInfo = null;
                if (jcMethodDecl.name.toString().equals("<init>")) {
                    methodInfo = new MethodInfo(jcClassDecl.name.toString(), paramTypes);
                } else {
                    methodInfo = new MethodInfo(jcMethodDecl.name.toString(), paramTypes);
                }
                if (jcMethodDecl.getReturnType() != null) {
                    if (jcMethodDecl.getReturnType().type != null) {
                        methodInfo.returnType = jcMethodDecl.getReturnType().type.toString();
                    } else {
                        methodInfo.returnType = jcMethodDecl.getReturnType().toString();
                    }
                } else {
                    methodInfo.returnType = ((Type.JCVoidType)types.getNoType(TypeKind.VOID)).toString();
                }
                methodInfo.isStatic = jcMethodDecl.mods.getFlags().contains(Modifier.STATIC);
                methodInfo.isPublic = jcMethodDecl.mods.getFlags().contains(Modifier.PUBLIC);
                methodInfo.isPrivate = jcMethodDecl.mods.getFlags().contains(Modifier.PRIVATE);
                methodInfo.params.addAll(paramList);

                // 构造函数
                if (methodInfo.name.equals(classInfo.name) && !methodInfo.isStatic) {
                    classInfo.constructMethods.add(methodInfo);
                } else {
                    // 其他方法
                    if (methodInfo.isStatic) {
                        classInfo.staticMethods.add(methodInfo);
                    } else {
                        classInfo.methods.add(methodInfo);
                    }

                    // 方法签名：名字+参数类型 theFunction(java.lang.String,int)
                    if (methodInfo.isStatic) {
                        staticMethods.put(methodInfo.signature, methodInfo);
                    } else {
                        instanceMethods.put(methodInfo.signature, methodInfo);
                    }
                }
            }

        }

        // 是否有getter
        for (FieldInfo fieldInfo : classInfo.fields) {
            fieldInfo.getter = instanceMethods.get(fieldInfo.getGetterSignature());
            fieldInfo.setter = instanceMethods.get(fieldInfo.getSetterSignature());

            if (fieldInfo.getter == null && fieldInfo.autoGetter) {
                fieldInfo.getter = FieldInfo.createGetter(fieldInfo);
                classInfo.methods.add(fieldInfo.getter);
            }
            if (fieldInfo.setter == null && fieldInfo.autoSetter) {
                fieldInfo.setter = FieldInfo.createSetter(fieldInfo);
                classInfo.methods.add(fieldInfo.setter);
            }
        }

        // 是否有setter
        for (FieldInfo fieldInfo : classInfo.staticFields) {
            fieldInfo.getter = staticMethods.get(fieldInfo.getGetterSignature());
            fieldInfo.setter = staticMethods.get(fieldInfo.getSetterSignature());
        }

        // 查看父类
        if (jcClassDecl.extending != null) {
            // 直接带有包名，取名字即可
            String superClassName = null;
            if (jcClassDecl.extending instanceof JCTree.JCFieldAccess) {
                superClassName = jcClassDecl.extending.toString();
            } else if (jcClassDecl.extending instanceof JCTree.JCIdent) {
                superClassName = ((JCTree.JCIdent) jcClassDecl.extending).type.toString();
            }
            classInfo.superClass = getClassInfo(superClassName);
        }


        return classInfo;
    }

    protected final ClassInfo getClassInfo(String className) {
        // 基类没有必要
        if (className.equals(Converters.Type.TYPE_OBJECT)) {
            return null;
        }
        Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol)elements.getTypeElement(className);
        if (classSymbol == null) {
            return null;
        }

        boolean classAutoGetter = hasAutoGetter(classSymbol);
        boolean classAutoSetter = hasAutoSetter(classSymbol);

        ClassInfo classInfo = new ClassInfo(className);
        Map<String, MethodInfo> staticMethods = new HashMap<>();
        Map<String, MethodInfo> instanceMethods = new HashMap<>();
        if (classSymbol != null) {
            // 只关心属性
            for (Symbol symbol : classSymbol.members().getElements()) {
                // 静态的过滤掉
                boolean isStatic = symbol.getModifiers().contains(Modifier.STATIC);

                if (symbol.getKind() == ElementKind.FIELD && symbol instanceof Symbol.VarSymbol) {// kind == field
                    Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) symbol;

                    FieldInfo fieldInfo = new FieldInfo(varSymbol.name.toString());
                    fieldInfo.type = varSymbol.type.toString();
                    fieldInfo.isPublic = symbol.getModifiers().contains(Modifier.PUBLIC);
                    fieldInfo.isPrivate = symbol.getModifiers().contains(Modifier.PRIVATE);
                    fieldInfo.isFinal = symbol.getModifiers().contains(Modifier.FINAL);
                    fieldInfo.isStatic = isStatic;
                    fieldInfo.ignore = getCompoundAnnotation(varSymbol.getAnnotationMirrors(), "com.lessismore.xauto.annotation.XAutoIgnore") != null;

                    fieldInfo.autoGetter = !isStatic && (classAutoGetter || hasAutoGetter(varSymbol));
                    fieldInfo.autoSetter = !isStatic && (classAutoSetter || hasAutoSetter(varSymbol));

                    fieldInfo.pos = varSymbol.pos;

                    if (isStatic) {
                        classInfo.staticFields.add(fieldInfo);
                    } else {
                        classInfo.fields.add(fieldInfo);
                    }

                } else if (symbol.getKind() == ElementKind.METHOD && symbol instanceof Symbol.MethodSymbol) {// kind == method
                    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) symbol;
                    MethodInfo methodInfo = new MethodInfo(methodSymbol.toString());
                    methodInfo.returnType = methodSymbol.getReturnType().toString();
                    methodInfo.isPublic = symbol.getModifiers().contains(Modifier.PUBLIC);
                    methodInfo.isPrivate = symbol.getModifiers().contains(Modifier.PRIVATE);
                    methodInfo.isStatic = isStatic;
                    List<Symbol.VarSymbol> params = methodSymbol.getParameters();
                    if (params != null) {
                        for (Symbol.VarSymbol var : params) {
                            ParamInfo paramInfo = new ParamInfo(var.name.toString());
                            paramInfo.type = var.type.toString();
                            methodInfo.params.add(paramInfo);
                        }
                    }
                    if (isStatic) {
                        classInfo.staticMethods.add(methodInfo);
                    } else {
                        classInfo.methods.add(methodInfo);
                    }

                    // 方法签名：名字+参数类型 theFunction(java.lang.String,int)
                    if (isStatic) {
                        staticMethods.put(methodSymbol.toString(), methodInfo);
                    } else {
                        instanceMethods.put(methodSymbol.toString(), methodInfo);
                    }
                } else if (symbol.getKind() == ElementKind.CONSTRUCTOR && symbol instanceof Symbol.MethodSymbol) {// kind == method
                    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) symbol;
                    MethodInfo methodInfo = new MethodInfo(methodSymbol.toString());
                    methodInfo.returnType = methodSymbol.getReturnType().toString();
                    methodInfo.isPublic = symbol.getModifiers().contains(Modifier.PUBLIC);
                    methodInfo.isPrivate = symbol.getModifiers().contains(Modifier.PRIVATE);
                    List<Symbol.VarSymbol> params = methodSymbol.getParameters();
                    if (params != null) {
                        for (Symbol.VarSymbol var : params) {
                            ParamInfo paramInfo = new ParamInfo(var.name.toString());
                            paramInfo.type = var.type.toString();
                            methodInfo.params.add(paramInfo);
                        }
                    }
                    classInfo.constructMethods.add(methodInfo);
                }

            }

            // 是否有getter
            for (FieldInfo fieldInfo : classInfo.fields) {
                fieldInfo.getter = instanceMethods.get(fieldInfo.getGetterSignature());
                fieldInfo.setter = instanceMethods.get(fieldInfo.getSetterSignature());

                if (fieldInfo.getter == null && fieldInfo.autoGetter) {
                    fieldInfo.getter = FieldInfo.createGetter(fieldInfo);
                    classInfo.methods.add(fieldInfo.getter);
                }
                if (fieldInfo.setter == null && fieldInfo.autoSetter) {
                    fieldInfo.setter = FieldInfo.createSetter(fieldInfo);
                    classInfo.methods.add(fieldInfo.setter);
                }
            }

            // 是否有setter
            for (FieldInfo fieldInfo : classInfo.staticFields) {
                fieldInfo.getter = staticMethods.get(fieldInfo.getGetterSignature());
                fieldInfo.setter = staticMethods.get(fieldInfo.getSetterSignature());
            }
        }

        // 查看他的supper
        // 查看父类
        if (classSymbol.getSuperclass() != null) {
            // 直接带有包名，取名字即可
            String superClassName = classSymbol.getSuperclass().toString();
            classInfo.superClass = getClassInfo(superClassName);
        }

        return classInfo;
    }

    protected abstract void translateClass(JCTree.JCClassDecl jcClassDecl, ClassInfo classInfo);

    /**
     * 支持所有类型的复制
     * @param tree
     * @param <T>
     * @return
     */
    public final  <T extends JCTree> T copyTree(T tree) {
        if (tree == null) {
            return null;
        }

        if (tree instanceof JCTree.LetExpr) {
            return (T)copyLetExpr((JCTree.LetExpr)tree);
        } else if (tree instanceof JCTree.JCErroneous) {
            return (T)copyErroneous((JCTree.JCErroneous)tree);
        } else if (tree instanceof JCTree.JCAnnotatedType) {
            return (T)copyAnnotationType((JCTree.JCAnnotatedType)tree);
        } else if (tree instanceof JCTree.JCModifiers) {
            return (T)copyModifiers((JCTree.JCModifiers)tree);
        } else if (tree instanceof JCTree.JCAnnotation) {
            return (T)copyAnnotation((JCTree.JCAnnotation)tree);
        } else if (tree instanceof JCTree.TypeBoundKind) {
            return (T)copyBoundKind((JCTree.TypeBoundKind)tree);
        } else if (tree instanceof JCTree.JCWildcard) {
            return (T)copyWildcard((JCTree.JCWildcard)tree);
        } else if (tree instanceof JCTree.JCTypeParameter) {
            return (T)copyTypeParameter((JCTree.JCTypeParameter)tree);
        } else if (tree instanceof JCTree.JCTypeIntersection) {
            return (T)copyTypeIntersection((JCTree.JCTypeIntersection)tree);
        } else if (tree instanceof JCTree.JCTypeUnion) {
            return (T)copyTypeUnion((JCTree.JCTypeUnion)tree);
        } else if (tree instanceof JCTree.JCTypeApply) {
            return (T)copyTypeApply((JCTree.JCTypeApply)tree);
        } else if (tree instanceof JCTree.JCArrayTypeTree) {
            return (T)copyArrayType((JCTree.JCArrayTypeTree)tree);
        } else if (tree instanceof JCTree.JCPrimitiveTypeTree) {
            return (T)copyPrimitiveType((JCTree.JCPrimitiveTypeTree)tree);
        } else if (tree instanceof JCTree.JCLiteral) {
            return (T)copyLiteral((JCTree.JCLiteral)tree);
        } else if (tree instanceof JCTree.JCIdent) {
            return (T)copyIdent((JCTree.JCIdent)tree);
        } else if (tree instanceof JCTree.JCMemberReference) {
            return (T)copyMemberReference((JCTree.JCMemberReference)tree);
        } else if (tree instanceof JCTree.JCFieldAccess) {
            return (T)copyFieldAccess((JCTree.JCFieldAccess)tree);
        } else if (tree instanceof JCTree.JCArrayAccess) {
            return (T)copyArrayAccess((JCTree.JCArrayAccess)tree);
        } else if (tree instanceof JCTree.JCInstanceOf) {
            return (T)copyInstanceOf((JCTree.JCInstanceOf)tree);
        } else if (tree instanceof JCTree.JCTypeCast) {
            return (T)copyTypeCast((JCTree.JCTypeCast)tree);
        } else if (tree instanceof JCTree.JCBinary) {
            return (T)copyBinary((JCTree.JCBinary)tree);
        } else if (tree instanceof JCTree.JCUnary) {
            return (T)copyUnary((JCTree.JCUnary)tree);
        } else if (tree instanceof JCTree.JCAssignOp) {
            return (T)copyAssignOp((JCTree.JCAssignOp)tree);
        } else if (tree instanceof JCTree.JCAssign) {
            return (T)copyAssign((JCTree.JCAssign)tree);
        } else if (tree instanceof JCTree.JCParens) {
            return (T)copyParens((JCTree.JCParens)tree);
        } else if (tree instanceof JCTree.JCLambda) {
            return (T)copyLambda((JCTree.JCLambda)tree);
        } else if (tree instanceof JCTree.JCNewArray) {
            return (T)copyNewArray((JCTree.JCNewArray)tree);
        } else if (tree instanceof JCTree.JCNewClass) {
            return (T)copyNewClass((JCTree.JCNewClass)tree);
        } else if (tree instanceof JCTree.JCMethodInvocation) {
            return (T)copyMethodInvocation((JCTree.JCMethodInvocation)tree);
        } else if (tree instanceof JCTree.JCAssert) {
            return (T)copyAssert((JCTree.JCAssert)tree);
        } else if (tree instanceof JCTree.JCThrow) {
            return (T)copyThrow((JCTree.JCThrow)tree);
        } else if (tree instanceof JCTree.JCReturn) {
            return (T)copyReturn((JCTree.JCReturn)tree);
        } else if (tree instanceof JCTree.JCContinue) {
            return (T)copyContinue((JCTree.JCContinue)tree);
        } else if (tree instanceof JCTree.JCBreak) {
            return (T)copyBreak((JCTree.JCBreak)tree);
        } else if (tree instanceof JCTree.JCExpressionStatement) {
            return (T)copyExpression((JCTree.JCExpressionStatement)tree);
        } else if (tree instanceof JCTree.JCIf) {
            return (T)copyIf((JCTree.JCIf)tree);
        } else if (tree instanceof JCTree.JCConditional) {
            return (T)copyConditional((JCTree.JCConditional)tree);
        } else if (tree instanceof JCTree.JCCatch) {
            return (T)copyCatch((JCTree.JCCatch)tree);
        } else if (tree instanceof JCTree.JCTry) {
            return (T)copyTry((JCTree.JCTry)tree);
        } else if (tree instanceof JCTree.JCSynchronized) {
            return (T)copySynchronized((JCTree.JCSynchronized)tree);
        } else if (tree instanceof JCTree.JCCase) {
            return (T)copyCase((JCTree.JCCase)tree);
        } else if (tree instanceof JCTree.JCSwitch) {
            return (T)copySwitch((JCTree.JCSwitch)tree);
        } else if (tree instanceof JCTree.JCLabeledStatement) {
            return (T)copyLabeledStatement((JCTree.JCLabeledStatement)tree);
        } else if (tree instanceof JCTree.JCEnhancedForLoop) {
            return (T)copyEnhancedFor((JCTree.JCEnhancedForLoop)tree);
        } else if (tree instanceof JCTree.JCForLoop) {
            return (T)copyFor((JCTree.JCForLoop)tree);
        } else if (tree instanceof JCTree.JCWhileLoop) {
            return (T)copyWhile((JCTree.JCWhileLoop)tree);
        } else if (tree instanceof JCTree.JCDoWhileLoop) {
            return (T)copyDoWhile((JCTree.JCDoWhileLoop)tree);
        } else if (tree instanceof JCTree.JCBlock) {
            return (T)copyBlock((JCTree.JCBlock)tree);
        } else if (tree instanceof JCTree.JCSkip) {
            return (T)copySkip((JCTree.JCSkip)tree);
        } else if (tree instanceof JCTree.JCVariableDecl) {
            return (T)copyVariable((JCTree.JCVariableDecl)tree);
        } else if (tree instanceof JCTree.JCMethodDecl) {
            return (T)copyMethod((JCTree.JCMethodDecl)tree);
        } else if (tree instanceof JCTree.JCClassDecl) {
            return (T)copyClass((JCTree.JCClassDecl)tree);
        } else if (tree instanceof JCTree.JCImport) {
            return (T)copyImport((JCTree.JCImport)tree);
        } else {
            log.log("暂不支持类型<" + tree.getClass().getName() + ":" + tree.hashCode() + ">复制！！！");
        }
        return null;
    }

    private <T extends JCTree> List<T> copyTrees(List<T> trees) {
        List<T> list = List.nil();
        if (trees == null) {
            return list;
        }
        Iterator<T> iterator = trees.iterator();
        while (iterator.hasNext()) {
            list = list.append(copyTree(iterator.next()));
        }
        return list;
    }


    private Name copyName(Name name) {
        if (name == null) {
            return null;
        }
        return names.fromString(name.toString());
    }
//    private Symbol.VarSymbol copyVarSymbol(Symbol.VarSymbol varSymbol) {
//        return varSymbol.clone()
//    }
//

    private JCTree.LetExpr copyLetExpr(JCTree.LetExpr letExpr) {
        if (letExpr == null) {
            return null;
        }
        return maker.LetExpr(copyTrees(letExpr.defs),copyTree(letExpr.expr));
    }

    private JCTree.JCErroneous copyErroneous(JCTree.JCErroneous erroneous) {
        if (erroneous == null) {
            return null;
        }
        return maker.Erroneous(copyTrees(erroneous.errs));
    }

    private JCTree.JCAnnotatedType copyAnnotationType(JCTree.JCAnnotatedType annotationType) {
        if (annotationType == null) {
            return null;
        }
        return maker.AnnotatedType(copyTrees(annotationType.annotations),copyTree(annotationType.underlyingType));
    }

    private JCTree.JCAnnotation copyAnnotation(JCTree.JCAnnotation annotation) {
        if (annotation == null) {
            return null;
        }

        JCTree annotationType = copyTree(annotation.annotationType);
        List<JCTree.JCExpression> expressions = copyTrees(annotation.args);
        // 注解定义的Target是ElementType.TYPE_USE时为TYPE_ANNOTATION，可用注解泛型，于案例 public class A<T extends @Reality String> {}
        if (annotation.getTag().equals(JCTree.Tag.TYPE_ANNOTATION)) {
            return maker.TypeAnnotation(annotationType,expressions);
        } else {
            return maker.Annotation(annotationType,expressions);
        }
    }

    private JCTree.JCModifiers copyModifiers(JCTree.JCModifiers modifiers) {
        if (modifiers == null) {
            return null;
        }
        return maker.Modifiers(modifiers.flags,copyTrees(modifiers.annotations));
    }

    private JCTree.TypeBoundKind copyBoundKind(JCTree.TypeBoundKind boundKind) {
        if (boundKind == null) {
            return null;
        }
        return maker.TypeBoundKind(boundKind.kind);
    }

    private JCTree.JCWildcard copyWildcard(JCTree.JCWildcard wildcard) {
        if (wildcard == null) {
            return null;
        }
        return maker.Wildcard(copyBoundKind(wildcard.kind),copyTree(wildcard.inner));
    }

    private JCTree.JCTypeParameter copyTypeParameter(JCTree.JCTypeParameter parameter) {
        if (parameter == null) {
            return null;
        }
        return maker.TypeParameter(copyName(parameter.name),copyTrees(parameter.bounds),copyTrees(parameter.annotations));
    }

    private JCTree.JCTypeIntersection copyTypeIntersection(JCTree.JCTypeIntersection intersection) {
        if (intersection == null) {
            return null;
        }
        return maker.TypeIntersection(copyTrees(intersection.bounds));
    }

    private JCTree.JCTypeUnion copyTypeUnion(JCTree.JCTypeUnion union) {
        if (union == null) {
            return null;
        }
        return maker.TypeUnion(copyTrees(union.alternatives));
    }

    private JCTree.JCTypeApply copyTypeApply(JCTree.JCTypeApply apply) {
        if (apply == null) {
            return null;
        }
        return maker.TypeApply(copyTree(apply.clazz),copyTrees(apply.arguments));
    }

    private JCTree.JCArrayTypeTree copyArrayType(JCTree.JCArrayTypeTree arrayType) {
        if (arrayType == null) {
            return null;
        }
        return maker.TypeArray(copyTree(arrayType.elemtype));
    }


    private JCTree.JCPrimitiveTypeTree copyPrimitiveType(JCTree.JCPrimitiveTypeTree primitiveTypeTree) {
        if (primitiveTypeTree == null) {
            return null;
        }
        return maker.TypeIdent(primitiveTypeTree.typetag);
    }


    private JCTree.JCLiteral copyLiteral(JCTree.JCLiteral literal) {
        if (literal == null) {
            return null;
        }
        return maker.Literal(literal.typetag,literal.value);
    }

    private JCTree.JCIdent copyIdent(JCTree.JCIdent ident) {
        if (ident == null) {
            return null;
        }
        Symbol symbol = ident.sym;
        return maker.Ident(copyName(ident.name));
    }

    private JCTree.JCMemberReference copyMemberReference(JCTree.JCMemberReference reference) {
        if (reference == null) {
            return null;
        }
        return maker.Reference(reference.mode,copyName(reference.name),copyTree(reference.expr),copyTrees(reference.typeargs));
    }


    private JCTree.JCFieldAccess copyFieldAccess(JCTree.JCFieldAccess access) {
        if (access == null) {
            return null;
        }
        return maker.Select(copyTree(access.selected),copyName(access.name));
    }

    private JCTree.JCArrayAccess copyArrayAccess(JCTree.JCArrayAccess access) {
        if (access == null) {
            return null;
        }
        return maker.Indexed(copyTree(access.indexed),copyTree(access.index));
    }

    private JCTree.JCInstanceOf copyInstanceOf(JCTree.JCInstanceOf instanceOf) {
        if (instanceOf == null) {
            return null;
        }
        return maker.TypeTest(copyTree(instanceOf.expr),copyTree(instanceOf.clazz));
    }

    private JCTree.JCTypeCast copyTypeCast(JCTree.JCTypeCast typeCast) {
        if (typeCast == null) {
            return null;
        }
        return maker.TypeCast(copyTree(typeCast.clazz),copyTree(typeCast.expr));
    }

    private JCTree.JCBinary copyBinary(JCTree.JCBinary binary) {
        if (binary == null) {
            return null;
        }
        return maker.Binary(binary.getTag(),copyTree(binary.lhs),copyTree(binary.rhs));
    }

    private JCTree.JCUnary copyUnary(JCTree.JCUnary unary) {
        if (unary == null) {
            return null;
        }
        return maker.Unary(unary.getTag(),copyTree(unary.arg));
    }

    private JCTree.JCAssignOp copyAssignOp(JCTree.JCAssignOp assignOp) {
        if (assignOp == null) {
            return null;
        }
        return maker.Assignop(assignOp.getTag(),copyTree(assignOp.lhs),copyTree(assignOp.rhs));
    }

    private JCTree.JCAssign copyAssign(JCTree.JCAssign assign) {
        if (assign == null) {
            return null;
        }
        return maker.Assign(copyTree(assign.lhs),copyTree(assign.rhs));
    }

    private JCTree.JCParens copyParens(JCTree.JCParens parens) {
        if (parens == null) {
            return null;
        }
        return maker.Parens(copyTree(parens.expr));
    }

    private JCTree.JCLambda copyLambda(JCTree.JCLambda lambda) {
        if (lambda == null) {
            return null;
        }
        return maker.Lambda(copyTrees(lambda.params),copyTree(lambda.body));
    }

    private JCTree.JCNewArray copyNewArray(JCTree.JCNewArray newArray) {
        if (newArray == null) {
            return null;
        }
        return maker.NewArray(copyTree(newArray.elemtype),copyTrees(newArray.dims),copyTrees(newArray.elems));
    }

    private JCTree.JCNewClass copyNewClass(JCTree.JCNewClass newClass) {
        if (newClass == null) {
            return null;
        }
        return maker.NewClass(copyTree(newClass.encl),copyTrees(newClass.typeargs),copyTree(newClass.clazz),copyTrees(newClass.args),copyClass(newClass.def));
    }

    private JCTree.JCMethodInvocation copyMethodInvocation(JCTree.JCMethodInvocation invocation) {
        if (invocation == null) {
            return null;
        }
        return maker.Apply(copyTrees(invocation.typeargs),copyTree(invocation.meth),copyTrees(invocation.args));
    }

    private JCTree.JCAssert copyAssert(JCTree.JCAssert jcAssert) {
        if (jcAssert == null) {
            return null;
        }
        return maker.Assert(copyTree(jcAssert.cond),copyTree(jcAssert.detail));
    }

    private JCTree.JCThrow copyThrow(JCTree.JCThrow jcThrow) {
        if (jcThrow == null) {
            return null;
        }
        return maker.Throw(copyTree(jcThrow.expr));
    }

    private JCTree.JCReturn copyReturn(JCTree.JCReturn jcReturn) {
        if (jcReturn == null) {
            return null;
        }
        return maker.Return(copyTree(jcReturn.expr));
    }

    private JCTree.JCContinue copyContinue(JCTree.JCContinue jcContinue) {
        if (jcContinue == null) {
            return null;
        }
        return maker.Continue(copyName(jcContinue.label));
    }

    private JCTree.JCBreak copyBreak(JCTree.JCBreak jcBreak) {
        if (jcBreak == null) {
            return null;
        }
        return maker.Break(copyName(jcBreak.label));
    }

    private JCTree.JCExpressionStatement copyExpression(JCTree.JCExpressionStatement expression) {
        if (expression == null) {
            return null;
        }
        return maker.Exec(copyTree(expression.expr));
    }


    private JCTree.JCIf copyIf(JCTree.JCIf jcIf) {
        if (jcIf == null) {
            return null;
        }
        return maker.If(copyTree(jcIf.cond),copyTree(jcIf.thenpart),copyTree(jcIf.elsepart));
    }


    private JCTree.JCConditional copyConditional(JCTree.JCConditional conditional) {
        if (conditional == null) {
            return null;
        }
        return maker.Conditional(copyTree(conditional.cond),copyTree(conditional.truepart),copyTree(conditional.falsepart));
    }


    private JCTree.JCCatch copyCatch(JCTree.JCCatch jcCatch) {
        if (jcCatch == null) {
            return null;
        }
        return maker.Catch(copyVariable(jcCatch.param),copyBlock(jcCatch.body));
    }


    private JCTree.JCTry copyTry(JCTree.JCTry jcTry) {
        if (jcTry == null) {
            return null;
        }
        return maker.Try(copyTrees(jcTry.resources),copyBlock(jcTry.body),copyTrees(jcTry.catchers),copyBlock(jcTry.finalizer));
    }


    private JCTree.JCSynchronized copySynchronized(JCTree.JCSynchronized jcSynchronized) {
        if (jcSynchronized == null) {
            return null;
        }
        return maker.Synchronized(copyTree(jcSynchronized.lock),copyBlock(jcSynchronized.body));
    }


    private JCTree.JCCase copyCase(JCTree.JCCase jcCase) {
        if (jcCase == null) {
            return null;
        }
        return maker.Case(copyTree(jcCase.pat),copyTrees(jcCase.stats));
    }

    private JCTree.JCSwitch copySwitch(JCTree.JCSwitch jcSwitch) {
        if (jcSwitch == null) {
            return null;
        }
        return maker.Switch(copyTree(jcSwitch.selector),copyTrees(jcSwitch.cases));
    }

    private JCTree.JCLabeledStatement copyLabeledStatement(JCTree.JCLabeledStatement statement) {
        if (statement == null) {
            return null;
        }
        return maker.Labelled(copyName(statement.label),copyTree(statement.body));
    }

    private JCTree.JCEnhancedForLoop copyEnhancedFor(JCTree.JCEnhancedForLoop loop) {
        if (loop == null) {
            return null;
        }
        return maker.ForeachLoop(copyVariable(loop.var),copyTree(loop.expr),copyTree(loop.body));
    }

    private JCTree.JCForLoop copyFor(JCTree.JCForLoop loop) {
        if (loop == null) {
            return null;
        }
        return maker.ForLoop(copyTrees(loop.init),copyTree(loop.cond),copyTrees(loop.step),copyTree(loop.body));
    }


    private JCTree.JCWhileLoop copyWhile(JCTree.JCWhileLoop loop) {
        if (loop == null) {
            return null;
        }
        return maker.WhileLoop(copyTree(loop.cond),copyTree(loop.body));
    }

    private JCTree.JCDoWhileLoop copyDoWhile(JCTree.JCDoWhileLoop loop) {
        if (loop == null) {
            return null;
        }
        return maker.DoLoop(copyTree(loop.body),copyTree(loop.cond));
    }

    private JCTree.JCBlock copyBlock(JCTree.JCBlock block) {
        if (block == null) {
            return null;
        }
        return maker.Block(block.flags,copyTrees(block.stats));
    }

    private JCTree.JCSkip copySkip(JCTree.JCSkip skip) {
        if (skip == null) {
            return null;
        }
        return maker.Skip();
    }

    private JCTree.JCVariableDecl copyVariable(JCTree.JCVariableDecl variable) {
        if (variable == null) {
            return null;
        }
        JCTree.JCModifiers modifiers = copyModifiers(variable.mods);
        Name name = copyName(variable.name);
        JCTree.JCExpression vartype = copyTree(variable.vartype);
        JCTree.JCExpression init = copyTree(variable.init);
        // Symbol.VarSymbol sym = null;
        return maker.VarDef(modifiers, name, vartype, init);
    }

    private JCTree.JCMethodDecl copyMethod(JCTree.JCMethodDecl method) {
        if (method == null) {
            return null;
        }

//        this.mods = var1;
//        this.name = var2;
//        this.restype = var3;
//        this.typarams = var4;
//        this.params = var6;
//        this.recvparam = var5;
//        this.thrown = var7;
//        this.body = var8;
//        this.defaultValue = var9;
//        this.sym = var10;

        //方法的访问级别
        JCTree.JCModifiers modifiers = copyModifiers(method.mods);

        //方法名称
        Name methodName = copyName(method.name);

        //设置返回值类型
        JCTree.JCExpression restype = copyTree(method.restype);
        List<JCTree.JCTypeParameter> typarams = copyTrees(method.typarams);
        JCTree.JCVariableDecl recvparam = copyVariable(method.recvparam);
        List<JCTree.JCVariableDecl> params = copyTrees(method.params);
        List<JCTree.JCExpression> thrown = copyTrees(method.thrown);

        // 函数执行体
        JCTree.JCBlock body = copyBlock(method.body);

        JCTree.JCExpression defaultValue = copyTree(method.defaultValue);

        // Symbol.VarSymbol sym = null;
        //构建方法
        return maker.MethodDef(modifiers, methodName, restype, typarams, recvparam, params, thrown, body, defaultValue);
    }

    private JCTree.JCClassDecl copyClass(JCTree.JCClassDecl classDecl) {
        if (classDecl == null) {
            return null;
        }
        JCTree.JCModifiers mods = copyModifiers(classDecl.mods);
        Name name = copyName(classDecl.name);
        List<JCTree.JCTypeParameter> typarams = copyTrees(classDecl.typarams);
        JCTree.JCExpression extending = copyTree(classDecl.extending);
        List<JCTree.JCExpression> implementing = copyTrees(classDecl.implementing);
        List<JCTree> defs = copyTrees(classDecl.defs);
//        Symbol.VarSymbol sym;
        return maker.ClassDef(mods,name,typarams,extending,implementing,defs);
    }

    private JCTree.JCImport copyImport(JCTree.JCImport jcImport) {
        if (jcImport == null) {
            return null;
        }
        return maker.Import(copyTree(jcImport.qualid),jcImport.staticImport);
    }

    /**
     * 添加方法
     * @param jcClassDecl
     * @param codes
     * @return
     */
    protected final JCTree.JCMethodDecl addMethod(JCTree.JCClassDecl jcClassDecl,String codes) {
        return addMethod(jcClassDecl, codes, -1, null);
    }
    protected final JCTree.JCMethodDecl addMethod(JCTree.JCClassDecl jcClassDecl, String codes, int paramStartPos, Type.MethodType methodType) {
        if (codes == null || codes.length() == 0) {
            return null;
        }

        JavaParser javaParser = new JavaParser();
        JCTree.JCMethodDecl jcMethodDecl = javaParser.parseJavaClassMethod(codes);

        if (!containsDef(jcClassDecl.defs,jcMethodDecl)) {
            JCTree.JCMethodDecl jcMethodDecl1 = copyTree(jcMethodDecl);
            if (jcMethodDecl1.params.size() > 0 && paramStartPos > 0) {
                jcMethodDecl1.params.get(0).pos = paramStartPos;
            }
            // sym还需进一步补充
            if (jcMethodDecl1.sym == null && methodType != null) {
                jcMethodDecl1.sym = new Symbol.MethodSymbol(jcMethodDecl1.mods.flags, jcMethodDecl1.name, methodType, jcClassDecl.sym);
            }

            jcClassDecl.defs = jcClassDecl.defs.prepend(jcMethodDecl1);

            if (jcMethodDecl1.sym != null) {
                jcClassDecl.sym.members().enterIfAbsent(jcMethodDecl1.sym);
            }
            log.log(jcMethodDecl1);
//            jcClassDecl.sym.
            return jcMethodDecl1;
        } else {
            log.log("" + jcClassDecl.name + " exist method " + jcMethodDecl.getName());
        }


        return null;
    }

    /**
     * 添加属性
     * @param jcClassDecl
     * @param codes
     * @return
     */
    protected final JCTree.JCVariableDecl addField(JCTree.JCClassDecl jcClassDecl, String codes) {
        if (codes == null || codes.length() == 0) {
            return null;
        }

        JavaParser javaParser = new JavaParser();
        JCTree.JCVariableDecl variableDecl = javaParser.parseJavaClassField(codes);
        if (!containsDef(jcClassDecl.defs,variableDecl)) {
            JCTree.JCVariableDecl variableDecl1 = copyTree(variableDecl);

            jcClassDecl.defs = jcClassDecl.defs.prepend(variableDecl1);
            log.log(variableDecl1);
            return variableDecl1;
        } else {
            log.log("" + jcClassDecl.name + " exist field " + variableDecl.getName());
        }

        return null;
    }

    /**
     * 添加类注解
     * @param jcClassDecl
     * @param codes
     * @return
     */
    protected final JCTree.JCAnnotation addAnnotation(JCTree.JCClassDecl jcClassDecl, String codes) {
        if (codes == null || codes.length() == 0) {
            return null;
        }

        JavaParser javaParser = new JavaParser();
        JCTree.JCAnnotation annotation = javaParser.parseJavaClassAnnotation(codes);
        if (!containsAnnotation(jcClassDecl.mods.annotations,annotation)) {
            JCTree.JCAnnotation annotation1 = copyTree(annotation);

            jcClassDecl.mods.annotations = jcClassDecl.mods.annotations.prepend(annotation1);
            log.log(annotation1.toString());
            return annotation1;
        } else {
            log.log("" + jcClassDecl.name + " exist annotation " + annotation.getAnnotationType().toString());
        }

        return null;
    }

    /**
     * 添加方法注解
     * @param jcMethodDecl
     * @param codes
     * @return
     */
    protected final JCTree.JCAnnotation addAnnotation(JCTree.JCMethodDecl jcMethodDecl, String codes) {
        if (codes == null || codes.length() == 0) {
            return null;
        }

        JavaParser javaParser = new JavaParser();
        JCTree.JCAnnotation annotation = javaParser.parseJavaClassMethodAnnotation(codes);
        if (!containsAnnotation(jcMethodDecl.mods.annotations,annotation)) {
            JCTree.JCAnnotation annotation1 = copyTree(annotation);

            jcMethodDecl.mods.annotations = jcMethodDecl.mods.annotations.prepend(annotation1);
            log.log(annotation1.toString());
            return annotation1;
        } else {
            log.log("" + jcMethodDecl.name + " exist annotation " + annotation.getAnnotationType().toString());
        }

        return null;
    }

    /**
     * 添加属性注解
     * @param jcVariableDecl
     * @param codes
     * @return
     */
    protected final JCTree.JCAnnotation addAnnotation(JCTree.JCVariableDecl jcVariableDecl, String codes) {
        if (codes == null || codes.length() == 0) {
            return null;
        }

        JavaParser javaParser = new JavaParser();
        JCTree.JCAnnotation annotation = javaParser.parseJavaClassFieldAnnotation(codes);
        if (!containsAnnotation(jcVariableDecl.mods.annotations,annotation)) {
            JCTree.JCAnnotation annotation1 = copyTree(annotation);

            jcVariableDecl.mods.annotations = jcVariableDecl.mods.annotations.prepend(annotation1);
            log.log(annotation1.toString());
            return annotation1;
        } else {
            log.log("" + jcVariableDecl.name + " exist annotation " + annotation.getAnnotationType().toString());
        }

        return null;
    }

    /**
     * 添加属性注解
     * @param jcClassDecl
     * @param codes
     * @return
     */
    protected final JCTree.JCClassDecl addInnerClass(JCTree.JCClassDecl jcClassDecl, String codes) {
        if (codes == null || codes.length() == 0) {
            return null;
        }

        JavaParser javaParser = new JavaParser();
        JCTree.JCClassDecl javaInnerClass = javaParser.parseJavaInnerClass(codes);
        if (!containsDef(jcClassDecl.defs,javaInnerClass)) {
            JCTree.JCClassDecl jcClassDecl1 = copyTree(javaInnerClass);

            jcClassDecl.defs = jcClassDecl.defs.prepend(jcClassDecl1);
            log.log(jcClassDecl1.toString());
            return jcClassDecl1;
        } else {
            log.log("" + jcClassDecl.name + " exist inner class " + javaInnerClass.name.toString());
        }

        return null;
    }

    protected final boolean containsAnnotation(List<JCTree.JCAnnotation> annotations, JCTree.JCAnnotation annotation) {
        String treeKey = getClassElementUniqueKey(annotation);
        //log.log(">>> log " + treeKey);
        for (JCTree tree : annotations) {
            String key = getClassElementUniqueKey(tree);
            if (key.equals(treeKey)) {
                return true;
            }
        }
        return false;
    }

    protected final boolean containsDef(List<JCTree> defs, JCTree jcTree) {
        String treeKey = getClassElementUniqueKey(jcTree);
        //log.log(">>> log " + treeKey);
        for (JCTree tree : defs) {
            String key = getClassElementUniqueKey(tree);
            if (key.equals(treeKey)) {
                return true;
            }
        }
        return false;
    }

    protected final String getClassElementUniqueKey(JCTree jcTree) {
        // 字段
        if (jcTree instanceof JCTree.JCVariableDecl) {
            return ((JCTree.JCVariableDecl) jcTree).getName().toString();
        } else if (jcTree instanceof JCTree.JCMethodDecl) {
            JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl)jcTree;
            StringBuilder builder = new StringBuilder();
            builder.append(methodDecl.getName());
            builder.append(":");
            boolean first = true;
            for (JCTree.JCVariableDecl parameter : methodDecl.getParameters()) {
                if (first) {
                    first = false;
                } else {
                    builder.append(",");
                }
                // 存在一些全路类名和简类名不匹配的问题，临时处理java包下面的类名
                builder.append(parameter.getType().toString());
            }
            return builder.toString();
        } else if (jcTree instanceof JCTree.JCAnnotation) {
            return ((JCTree.JCAnnotation) jcTree).type.toString();
        } else if (jcTree instanceof JCTree.JCClassDecl) {
            return "class:" + ((JCTree.JCClassDecl) jcTree).type.toString();
        } else {
            return jcTree.toString();
        }
    }

    // 获取方法定义得annotation
    protected final JCTree.JCAnnotation getAnnotation(List<JCTree.JCAnnotation> annotations, String annotationType) {
        for (JCTree.JCAnnotation annotation : annotations) {
            if (annotation.type.toString().equals(annotationType)) {
                return annotation;
            }
        }
        return null;
    }

    protected final java.util.List<JCTree.JCAnnotation> getAnnotations(List<JCTree.JCAnnotation> annotations, String[] excludes) {
        java.util.List<JCTree.JCAnnotation> list = new ArrayList<JCTree.JCAnnotation>();
        for (JCTree.JCAnnotation annotation : annotations) {
            String type = annotation.type.toString();
            boolean excluded = false;
            for (String exclude : excludes) {
                if (exclude.equals(type)) {
                    excluded = true;
                    break;
                }
            }
            if (!excluded) {
                list.add(annotation);
            }
        }
        return list;
    }

    protected final String getAnnotationValue(JCTree.JCAnnotation annotation, String field) {
        if (annotation == null) {
            return null;
        }
        for (JCTree.JCExpression expression : annotation.args) {
            if (expression instanceof JCTree.JCAssign) {
                if (((JCTree.JCAssign) expression).lhs.toString().equals(field)) {
                    return valueString(((JCTree.JCAssign) expression).rhs.toString());
                }
            } else if (field.equals("value")) { //取省略value
                /*
                if (expression instanceof JCTree.JCNewArray) {

                } else if (expression instanceof JCTree.JCLiteral) {

                }*/
                return valueString(expression.toString());
            }
        }
        return null;
    }

    protected final String valueString(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1,value.length() - 1);
        } else if (value.startsWith("\'") && value.endsWith("\'")) {
            return value.substring(1,value.length() - 1);
        } else {
            return value;
        }
    }


    protected final String readInputStream(InputStream is) {
        StringBuilder buffer = new StringBuilder();
        // 根据文件创建文件的输入流
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            line = reader.readLine(); // 读取第一行
            while (line != null) { // 如果 line 为空说明读完了
                buffer.append(line); // 将读到的内容添加到 buffer 中
                buffer.append("\n"); // 添加换行符
                line = reader.readLine(); // 读取下一行
            }
        } catch (Throwable e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return buffer.toString();
    }

    protected final void writeSourceFile(String newFullPackage, String codes) {
        filer.writeJavaSourceFile(newFullPackage, codes);
    }

    protected final void appendServiceResourceFile(String resourceName, String appendContent) {
        String resourceFile = "META-INF/services/" + resourceName;
        filer.writeAppendResourceFile(resourceFile, appendContent);
    }

    protected final Attribute.Compound getCompoundAnnotation(List<Attribute.Compound> compounds, String annotationType) {
        for (Attribute.Compound compound : compounds) {
            if (compound.type.toString().equals(annotationType)) {
                return compound;
            }
        }
        return null;
    }

    protected final String getCompoundAnnotationValue(Attribute.Compound compound, String field) {
        if (compound == null) {
            return null;
        }

        Attribute attribute = compound.member(names.fromString(field));
        if (attribute != null) {
            return attribute.getValue().toString();
        }

        return null;
    }
}
