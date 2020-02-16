package com.lessismore.xauto.translator;

import com.lessismore.xauto.ast.ClassInfo;
import com.lessismore.xauto.ast.FieldInfo;
import com.lessismore.xauto.processor.FileObjectManager;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;


public class XAutoDataTreeTranslator extends AbstractTreeTranslator {

    private boolean genSetter;
    private boolean genGetter;
    private boolean genToString;

    public XAutoDataTreeTranslator(Context context, FileObjectManager filer, Elements elements, Types types, TreeMaker maker, Names names, Messager messager, boolean setter, boolean getter, boolean toString) {
        super(context,filer,elements, types, maker, names, messager);
        this.genGetter = getter;
        this.genSetter = setter;
        this.genToString = toString;
    }

    @Override
    protected void translateClass(JCTree.JCClassDecl jcClassDecl, ClassInfo classInfo) {
        // 添加 toString 方法
//        if (genToString && !classInfo.hasMethod("toString()")) {
//            makeToStringMethodDecl(jcClassDecl);
//        }

        // 添加getter
        if (genSetter) {
            for (FieldInfo fieldInfo : classInfo.getReverseFields()) {
                if ((fieldInfo.setter == null || fieldInfo.autoSetter) && !fieldInfo.isFinal) {
                    String setterMethod = resolver.resolver("xauto/ftl/setter", "ftl", fieldInfo);

                    Type.MethodType methodType = null;
                    if (fieldInfo.var != null && fieldInfo.var.vartype != null && fieldInfo.var.vartype.type != null) {
                        List<Type> argtypes = List.nil();
                        argtypes = argtypes.append(fieldInfo.var.vartype.type);
                        Type restype = ((Type.JCVoidType) types.getNoType(TypeKind.VOID));
                        methodType = new Type.MethodType(argtypes, restype, List.nil(), null);
                    }
                    addMethod(jcClassDecl, setterMethod, fieldInfo.pos, methodType);
//                    addMethod(jcClassDecl, setterMethod, fieldInfo.pos, null);
                }
            }
        }

        // 添加getter
        if (genGetter) {
            for (FieldInfo fieldInfo : classInfo.getReverseFields()) {
                if (fieldInfo.getter == null || fieldInfo.autoGetter) {
                    String getterMethod = resolver.resolver("xauto/ftl/getter", "ftl", fieldInfo);

                    Type.MethodType methodType = null;
                    if (fieldInfo.var != null && fieldInfo.var.vartype != null && fieldInfo.var.vartype.type != null) {
                        List<Type> argtypes = List.nil();
                        Type restype = fieldInfo.var.vartype.type;
                        methodType = new Type.MethodType(argtypes, restype, List.nil(), null);
                    }
                    addMethod(jcClassDecl,getterMethod, -1, methodType);
//                    addMethod(jcClassDecl,getterMethod, -1, null);
                }
            }
        }

    }
}
