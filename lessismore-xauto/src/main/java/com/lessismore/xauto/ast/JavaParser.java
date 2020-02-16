package com.lessismore.xauto.ast;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JavaParser {

    private Context context;
    private JavacFileManager fileManager;
    private JavacTool javacTool;

    public JavaParser() {
        this(new Context());
    }
    public JavaParser(Context context) {
        this.context = context == null ? new Context() : context;
        this.fileManager = new JavacFileManager(context, true, Charset.defaultCharset());
        this.javacTool = new JavacTool();
    }



    private String saveToTmpdir(String code) {
        String m5 = md5(code);
        String dir = System.getProperty("java.io.tmpdir");
        String file = dir + (dir.endsWith(File.separator) ? "" : File.separator) + m5 + ".java";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(code.getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    public JCTree.JCMethodDecl parseJavaClassMethod(String method) {
        String code = "public class $ { " + method + " }";
        java.util.List<SourceVisitor> visitors = parseJavaClass(code);
        if (visitors == null || visitors.isEmpty()) {
            return null;
        }
        MethodTree methodTree = visitors.get(0).getMethods().get(0);
        if (methodTree instanceof JCTree.JCMethodDecl) {
            return (JCTree.JCMethodDecl)methodTree;
        }
        return null;
    }

    public JCTree.JCVariableDecl parseJavaClassField(String field) {
        String code = "public class $ { " + field + " }";
        java.util.List<SourceVisitor> visitors = parseJavaClass(code);
        if (visitors == null || visitors.isEmpty()) {
            return null;
        }
        VariableTree variableTree = visitors.get(0).getFields().get(0);
        if (variableTree instanceof JCTree.JCVariableDecl) {
            return (JCTree.JCVariableDecl)variableTree;
        }
        return null;
    }

    public JCTree.JCAnnotation parseJavaClassAnnotation(String annotation) {
        String code = annotation + " public class $ {  }";
        java.util.List<SourceVisitor> visitors = parseJavaClass(code);
        if (visitors == null || visitors.isEmpty()) {
            return null;
        }
        JCTree.JCClassDecl classDecl = visitors.get(0).getClassDecl();
        return classDecl.mods.annotations.get(0);
    }

    public JCTree.JCAnnotation parseJavaClassMethodAnnotation(String annotation) {
        JCTree.JCMethodDecl methodDecl = parseJavaClassMethod(annotation + " public void func() {}");
        if (methodDecl != null) {
            return methodDecl.mods.annotations.get(0);
        }
        return null;
    }

    public JCTree.JCAnnotation parseJavaClassFieldAnnotation(String annotation) {
        JCTree.JCVariableDecl variableDecl = parseJavaClassField(annotation + " public int afield = 10;");
        if (variableDecl != null) {
            return variableDecl.mods.annotations.get(0);
        }
        return null;
    }

    public JCTree.JCClassDecl parseJavaInnerClass(String classCode) {
        String code = " public class $ { " + classCode + " }";
        java.util.List<SourceVisitor> visitors = parseJavaClass(code);
        if (visitors == null || visitors.isEmpty()) {
            return null;
        }
        return visitors.get(0).getInnerClassDecl();
    }

    public JCTree.JCClassDecl parseJavaTheClass(String code) {
        java.util.List<SourceVisitor> visitors = parseJavaClass(code);
        if (visitors == null || visitors.isEmpty()) {
            return null;
        }
        return visitors.get(0).getClassDecl();
    }

    public java.util.List<SourceVisitor> parseJavaClass(String code) {
        String file = saveToTmpdir(code);
        return parseJavaFile(file);
    }

    public java.util.List<SourceVisitor> parseJavaFile(String file) {
        java.util.List<SourceVisitor> results = new java.util.ArrayList<SourceVisitor>();

        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjects(file);
        JavaCompiler.CompilationTask compilationTask = javacTool.getTask(null, fileManager, null, null, null, files);
        JavacTask javacTask = (JavacTask) compilationTask;
        try {
            Iterable<? extends CompilationUnitTree> result = javacTask.parse();
            for (CompilationUnitTree tree : result) {
                SourceVisitor sourceVisitor = new SourceVisitor();
                tree.accept(sourceVisitor, null);
                results.add(sourceVisitor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    protected static final String getClassElementUniqueKey(JCTree jcTree) {
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
                builder.append(parameter.getType().toString());
            }
            return builder.toString();
        } else {
            return jcTree.toString();
        }
    }

    public static final String md5(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return toHex(md5.digest(content.getBytes(Charset.defaultCharset())));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final char[] hexArray = "0123456789abcdef".toCharArray();
    private static final String toHex(byte[] bs) {
        if (bs == null) return null;
        char[] hexChars = new char[bs.length * 2];
        for (int i = 0; i < bs.length; i++) {
            int v = bs[i] & 0xff;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0f];
        }
        return new String(hexChars);
    }
}
