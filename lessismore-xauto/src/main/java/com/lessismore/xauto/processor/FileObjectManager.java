package com.lessismore.xauto.processor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FileObjectManager {

    private static Filer filer;
    private static Map<String, WriterItem> writers = new HashMap<>();

    static private class WriterItem {
        String resouceName;
        FileObject fileObject;
        Writer writer;
    }

    public FileObjectManager(Filer filer) {
        if (this.filer == null) {
            this.filer = filer;
        }
    }

    public Filer getFiler() {
        return filer;
    }


    // 只允许查找一次
    public final WriterItem getReadResouceFileObject(String resourceName) {
        WriterItem item = writers.get(resourceName);
        if (item != null) {
            return item;
        }
        try {
            item = new WriterItem();
            item.fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceName);
            item.resouceName = resourceName;
            item.writer = item.fileObject.openWriter();
            writers.put(resourceName, item);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return item;
    }

    public final void writeFileObject(FileObject jfo, String codes) {
        try {
            Writer writer = jfo.openWriter();
            writer.write(codes);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeResourceFile(String relativeName, String content) {
        try {
            FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", relativeName);
            writeFileObject(fileObject, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeJavaSourceFile(String className, String codes) {
        try {
            JavaFileObject jfo = filer.createSourceFile(className, new Element[]{});
            writeFileObject(jfo, codes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeJavaSourceFile(String relativePath, String className, String codes) {
        String extension = JavaFileObject.Kind.SOURCE.extension;
        if (className.endsWith(extension)) {
            className = className.substring(0, className.length() - extension.length());
        }
        String relativeName = null;
        if (relativePath.endsWith(File.separator)) {
            relativeName = relativePath + className.replaceAll("\\.", File.separator) + extension;
        } else {
            relativeName = relativePath + File.separator + className.replaceAll("\\.", File.separator) + extension;
        }
        try {
            FileObject fileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", relativeName);
            writeFileObject(fileObject, codes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeAppendResourceFile(String resourceName, String content) {
        try {
            WriterItem item = getReadResouceFileObject(resourceName);
            Writer writer = item.writer;
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public final String readResourceFile(String resourceName) {
//        StringBuilder buffer = new StringBuilder();
//        try {
//            FileObject jfo = getReadResouceFileObject(resourceName);
//            if (jfo != null) {
//                Reader reader = jfo.openReader(false);
//
//                // 根据文件创建文件的输入流
//                char[] bt = new char[64]; // 用来保存每行读取的内容
//                int len = -1;
//                while ((len = reader.read(bt)) > 0) {
//                    buffer.append(new String(bt, 0, len));
//                }
//
//                reader.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return buffer.toString();
//    }

}
