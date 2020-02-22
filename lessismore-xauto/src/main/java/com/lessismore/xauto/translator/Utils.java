package com.lessismore.xauto.translator;

import com.lessismore.xauto.ast.MappingInfo;
import com.lessismore.xauto.ast.StringUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Utils {
    public static final Map<String,MappingInfo> parseMapping(JCTree.JCExpression rhs) {
        Map<String,MappingInfo> mapping = new HashMap<>();

        // 多种写法支持
        java.util.List<JCTree.JCAnnotation> mpAnnotations = new ArrayList<>();
        if (rhs instanceof JCTree.JCAnnotation) {
            mpAnnotations.add((JCTree.JCAnnotation)rhs);
        } else if (rhs instanceof JCTree.JCNewArray) {// 数组，多个
            com.sun.tools.javac.util.List<JCTree.JCExpression> elems = ((JCTree.JCNewArray) rhs).elems;
            for (JCTree.JCExpression elem : elems) {
                if (elem instanceof JCTree.JCAnnotation) {
                    mpAnnotations.add((JCTree.JCAnnotation)elem);
                }
            }
        }

        // XAutoMapping(field,from,expression)
        for (JCTree.JCAnnotation mp : mpAnnotations) {

            String from = null;
            String field = null;
            String expCode = null;

            for (JCTree.JCExpression exp : mp.args) {
                if (((JCTree.JCAssign) exp).lhs.toString().equals("from")) {
                    from = StringUtils.valueString (((JCTree.JCAssign) exp).rhs.toString());
                } else if (((JCTree.JCAssign) exp).lhs.toString().equals("field")) {
                    field = StringUtils.valueString (((JCTree.JCAssign) exp).rhs.toString());
                } else if (((JCTree.JCAssign) exp).lhs.toString().equals("expression")) {
                    expCode = StringUtils.valueString (((JCTree.JCAssign) exp).rhs.toString());
                }

            }
            if (StringUtils.notEmpty(from) && StringUtils.notEmpty(field)) {
                MappingInfo info = new MappingInfo(field,from);
                if (StringUtils.notEmpty(expCode)) {
                    info.expression = expCode;
                }
                mapping.put(field,info);
            }
        }
        return mapping;
    }
}
