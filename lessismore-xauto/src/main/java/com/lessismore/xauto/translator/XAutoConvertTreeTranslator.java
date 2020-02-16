package com.lessismore.xauto.translator;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.ast.ClassInfo;
import com.lessismore.xauto.ast.CopierInfo;
import com.lessismore.xauto.ast.FieldInfo;
import com.lessismore.xauto.copy.CopierInterface;
import com.lessismore.xauto.processor.FileObjectManager;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XAutoConvertTreeTranslator extends AbstractTreeTranslator {

    public XAutoConvertTreeTranslator(Context context, FileObjectManager filer, Elements elements, Types types, TreeMaker maker, Names names, Messager messager) {
        super(context,filer, elements, types, maker, names, messager);
    }

    @Override
    protected void translateClass(JCTree.JCClassDecl jcClassDecl, ClassInfo classInfo) {

        // 查找可转换的目标对象
        List<ClassInfo> targets = getTargets(jcClassDecl);
        List<String> copiers = new ArrayList<>();
        if (targets != null) {
            for (ClassInfo target : targets) {
                // 添加新的拷贝类
                CopierInfo copierInfo = makeWriteCopierInfo(classInfo, target);
                if (classInfo != null) {
                    copiers.add(copierInfo.getClassName());
                }
            }
        }

        // 写service
        if (copiers.size() > 0) {
            appendCopierService(copiers);
        }
    }

    private List<ClassInfo> getTargets(JCTree.JCClassDecl jcClassDecl) {
        JCTree.JCAnnotation autoDto = getAnnotation(jcClassDecl.mods.annotations, XAutoConvert.class.getName());
        List<ClassInfo> targets = new ArrayList<>();
        if (autoDto != null) {
            String temp = getAnnotationValue(autoDto, "value");
            if (temp != null && temp.trim().length() > 0) {
                temp = temp.trim();
                //log.log("002》》》" + temp);
                if (temp.startsWith("{") && temp.endsWith("}")) {
                    temp = temp.substring(1, temp.length() - 1);
                }

                String[] ss = temp.split(",");
                for (String className : ss) {
                    className = className.trim();
                    if (className.startsWith("\"") && className.endsWith("\"")) {
                        className = className.substring(1, className.length() - 1);
                    }

                    ClassInfo classInfo = getClassInfo(className);
                    if (classInfo != null) {
                        targets.add(classInfo);
                    }
                }
            }
        }

        return targets;
    }

    private CopierInfo makeWriteCopierInfo(ClassInfo sourceInfo, ClassInfo targetInfo) {
        CopierInfo copierInfo = new CopierInfo(targetInfo, sourceInfo);
        String copierSourceCodes = resolver.resolver("xauto/ftl/copier", "ftl", copierInfo);
        writeSourceFile(copierInfo.getClassName(), copierSourceCodes.toString());
        return copierInfo;
    }

    private void appendCopierService(List<String> copiers) {
        String sourceName = CopierInterface.class.getName();
        StringBuilder builder = new StringBuilder();
        for (String line : copiers) {
            builder.append(line).append("\n");
        }
        appendServiceResourceFile(sourceName,builder.toString());
    }
}
