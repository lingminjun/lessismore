package com.lessismore.xauto.processor;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

public class Logger {
    private Messager messager;
    private boolean verbose = true;

    public Logger(Messager messager,boolean verbose) {
        this.messager = messager;
        this.verbose = verbose;
    }

    public Logger(Messager messager) {
        this.messager = messager;
    }

    /**
     * 输出日志
     * @param msg
     */
    public final void log(String msg) {
        // 日志一律先不打印
        // messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    /**
     * 输出名字，明确索引
     * @param name
     */
    public final void log(Name name) {
        String msg = string(name);
        log(msg);
    }


    /**
     * 输出ident，明确标识name
     * @param ident
     */
    public final void log(JCTree.JCIdent ident) {
        String msg = string(ident);
        log(msg);
    }

    /**
     * 输出method
     * @param method
     */
    public final void log(JCTree.JCMethodDecl method) {
        String msg = string(method);
        log(msg);
    }


    /**
     * 输出method
     * @param variable
     */
    public final void log(JCTree.JCVariableDecl variable) {
        String msg = string(variable);
        log(msg);
    }





    public final String string(JCTree.JCMethodDecl method) {
        if (method == null) {
            return "null";
        }
        String msg = String.format("<%s:%d>{mods=%s; return=%s; name=%s; typarams=%s; \nbody=%s}",
                method.getClass().getName(),method.hashCode(),
                string(method.mods),
                string(method.restype),
                string(method.name),
                string(method.typarams),
                string(method.body));
        return msg;
    }


    public final String string(JCTree.JCBlock block) {
        if (block == null) {
            return "null";
        }
        return block.toString();
    }

    public final String string(JCTree.JCIdent ident) {
        if (ident == null) {
            return "null";
        }
        String msg = String.format("<%s:%d>{name=%s; type=%s}",
                ident.getClass().getName(),ident.hashCode(),
                string(ident.name),
                string(ident.type));
        return msg;
    }


    public final String string(JCTree.JCVariableDecl variable) {
        if (variable == null) {
            return "null";
        }
        String msg = String.format("<%s:%d>{name=%s; type=%s}",
                variable.getClass().getName(),variable.hashCode(),
                string(variable.name),
                string(variable.vartype));
        return msg;
    }


    public final String string(List<JCTree.JCTypeParameter> parameters) {
        if (parameters == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (JCTree.JCTypeParameter parameter : parameters) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(string(parameter));
        }
        return builder.toString();
    }

    public final String string(JCTree.JCTypeParameter  parameter) {
        if (parameter == null) {
            return "null";
        }
        String msg = String.format("<%s:%d>{name=%s}",
                parameter.getClass().getName(),parameter.hashCode(),
                string(parameter.name));
        return msg;
    }

    public final String string(JCTree.JCExpression expression) {
        if (expression == null) {
            return "null";
        }
        if (expression instanceof JCTree.JCIdent) {
            return string((JCTree.JCIdent)expression);
        }
        String msg = String.format("<%s:%d>{desc=%s}",
                expression.getClass().getName(),expression.hashCode(),
                expression.toString());
        return msg;
    }

    public final String string(Name name) {
        if (name == null) {
            return "null";
        }
        String msg = String.format("<%s:%d>{index=%d; table=%s}",
                name.getClass().getName(),name.hashCode(),
                name.getIndex(),
                string(name.table));
        return msg;
    }

    public final String string(Name.Table name) {
        if (name == null) {
            return "null";
        }
        String msg = String.format("<%s:%d>",
                name.getClass().getName(),name.hashCode());
        return msg;
    }

    public final String string(ModifiersTree modifiers) {
        if (modifiers == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (AnnotationTree annotationTree : modifiers.getAnnotations()) {
            builder.append(annotationTree.getAnnotationType());
            builder.append("\n");
        }
        for (Modifier modifier : modifiers.getFlags()) {
            builder.append(modifier).append(" ");
        }
        return builder.toString();
    }

    public final String string(Type type) {
        if (type == null) {
            return "null";
        }
        String msg = String.format("%s:{base=%b}",
                type.toString(),type.isPrimitive());
        return msg;
    }
}
