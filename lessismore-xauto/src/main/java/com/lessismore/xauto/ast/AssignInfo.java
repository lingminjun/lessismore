package com.lessismore.xauto.ast;

import com.lessismore.xauto.copy.Converters;

public class AssignInfo {
    public static final String VAR_SOURCE = "source";
    public static final String VAR_TARGET = "target";

    // 左侧 target
    public final FieldInfo leftField;
    public final MethodInfo leftSetter;
    public final MethodInfo leftGetter;

    // 右侧 source
    public final FieldInfo rightField;
    public final MethodInfo rightGetter;

    private String assignmentStatement;

    public AssignInfo(MethodInfo leftSetter, MethodInfo rightGetter, FieldInfo leftField, FieldInfo rightField, MethodInfo leftGetter) {
        this.leftField = leftField;
        this.leftSetter = leftSetter;
        this.leftGetter = leftGetter;
        this.rightField = rightField;
        this.rightGetter = rightGetter;
    }

    public String getLeftType() {
        if (leftSetter != null && !leftSetter.isStatic && leftSetter.isSetter() && leftSetter.isPublic) {
            return leftSetter.params.get(0).type;
        }
        if (leftField != null && !leftField.isStatic && leftField.isPublic && !leftField.isFinal) {
            return leftField.type;
        }
        return null;
    }

    public String getRightType() {
        if (rightGetter != null && !rightGetter.isStatic && rightGetter.isGetter() && rightGetter.isPublic) {
            return rightGetter.returnType;
        }
        if (rightField != null && !rightField.isStatic && rightField.isPublic) {
            return rightField.type;
        }
        return null;
    }

    public String getTargetDefaultStatement() {
        if (leftGetter != null && !leftGetter.isStatic && leftSetter.isGetter() && leftSetter.isPublic) {
            return VAR_TARGET + "." + leftGetter.name + "()";
        }
        if (leftField != null && !leftField.isStatic && leftField.isPublic) {
            return VAR_TARGET + "." + leftField.name;
        }

        String type = getLeftType();
        if (Converters.Type.TYPE_int.equals(type)) {
            return "0";
        } else if (Converters.Type.TYPE_short.equals(type)) {
            return "0";
        } else if (Converters.Type.TYPE_long.equals(type)) {
            return "0l";
        } else if (Converters.Type.TYPE_float.equals(type)) {
            return "0.0f";
        } else if (Converters.Type.TYPE_double.equals(type)) {
            return "0.0d";
        } else if (Converters.Type.TYPE_char.equals(type)) {
            return "((char)0)";
        } else if (Converters.Type.TYPE_byte.equals(type)) {
            return "((byte)0)";
        }else if (Converters.Type.TYPE_bool.equals(type)) {
            return "false";
        }
        return "null";
    }

    public String getSourceValueStatement() {
        StringBuilder sourceStatement = new StringBuilder();
        if (rightGetter != null && rightGetter.isPublic) {
            sourceStatement.append(VAR_SOURCE);
            sourceStatement.append(".");
            sourceStatement.append(rightGetter.name);
            sourceStatement.append("()");
        } else {// 属性
            sourceStatement.append(VAR_SOURCE);
            sourceStatement.append(".");
            sourceStatement.append(rightField.name);
        }
        return sourceStatement.toString();
    }


    public String getAssignmentStatement() {
        if (assignmentStatement != null) {
            return assignmentStatement;
        }
        assignmentStatement = "// " + leftField.name + " = ? ";

        String leftType = getLeftType();
        String rightType = getRightType();
        // 是否有赋值语句
        if (leftType == null || rightType == null || leftField.isIgnore()) {
            return assignmentStatement;
        }

        // 相等的情况，直接赋值
        if (leftType.equals(rightType)) {
            assignmentStatement = getEqualTypeAssignmentStatement();
        } else if (Converters.Type.TYPE_int.equals(leftType) || Converters.Type.TYPE_INT.equals(leftType)) {
            assignmentStatement = getINTTypeAssignmentStatement();
        } else if (Converters.Type.TYPE_short.equals(leftType) || Converters.Type.TYPE_SHORT.equals(leftType)) {
            assignmentStatement = getSHORTTypeAssignmentStatement();
        } else if (Converters.Type.TYPE_long.equals(leftType) || Converters.Type.TYPE_LONG.equals(leftType)) {
            assignmentStatement = getLONGTypeAssignmentStatement();
        } else if (Converters.Type.TYPE_float.equals(leftType) || Converters.Type.TYPE_FLOAT.equals(leftType)) {
            assignmentStatement = getFLOATTypeAssignmentStatement();
        } else if (Converters.Type.TYPE_double.equals(leftType) || Converters.Type.TYPE_DOUBLE.equals(leftType)) {
            assignmentStatement = getDOUBLETypeAssignmentStatement();
        } else if (Converters.Type.TYPE_bool.equals(leftType) || Converters.Type.TYPE_BOOL.equals(leftType)) {
            assignmentStatement = getBOOLTypeAssignmentStatement();
        } else if (Converters.Type.TYPE_ARRAY_int.equals(leftType) || Converters.Type.TYPE_ARRAY_INT.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.TYPE_ARRAY_short.equals(leftType) || Converters.Type.TYPE_ARRAY_SHORT.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.TYPE_ARRAY_long.equals(leftType) || Converters.Type.TYPE_ARRAY_LONG.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.TYPE_ARRAY_float.equals(leftType) || Converters.Type.TYPE_ARRAY_FLOAT.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.TYPE_ARRAY_double.equals(leftType) || Converters.Type.TYPE_ARRAY_DOUBLE.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.TYPE_ARRAY_bool.equals(leftType) || Converters.Type.TYPE_ARRAY_BOOL.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.TYPE_STRING.equals(leftType) || Converters.Type.TYPE_ARRAY_STRING.equals(leftType)) {
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.isArray(leftType)) {//数组转发
            assignmentStatement = getARRAYTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.isCollection(leftType)) {//容器
            assignmentStatement = getCollectionTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        } else if (Converters.Type.isMap(leftType)) {//map
            // key相等的情况
            if (Converters.Type.getFirstElementType(leftType).equals(Converters.Type.getFirstElementType(rightType))) {
                java.lang.String[] types = Converters.Type.getElementTypes(leftType);
                if (types.length >= 2) {
                    assignmentStatement = getMapTypeAssignmentStatement(types[1]);
                }
            }
            // not support
        } else {
            assignmentStatement = getObjTypeAssignmentStatement(leftType, Converters.Type.getFirstElementType(leftType));
        }

        return assignmentStatement;
    }

    private String assemblyAssignmentStatement(CharSequence convert, String type, String elementType) {

        StringBuilder valueStatement = new StringBuilder();
        if (convert != null) {
            valueStatement.append(convert);
            valueStatement.append("(");
            valueStatement.append(getSourceValueStatement());
            valueStatement.append(",");
            if (type != null) {
                valueStatement.append(type);
                valueStatement.append(",");
                if (elementType != null) {
                    valueStatement.append(elementType);
                    valueStatement.append(",");
                }
            }
            valueStatement.append(getTargetDefaultStatement()); // defaultValue
            if (type != null) {
                valueStatement.append(",null"); // elementDefaultValue
            }
            valueStatement.append(")");
        } else {
            valueStatement.append(getSourceValueStatement());
        }

        StringBuilder statement = new StringBuilder();
        // 优先采用setter
        if (leftSetter != null && leftSetter.isPublic) {
            statement.append(VAR_TARGET);
            statement.append(".");
            statement.append(leftSetter.name);
            statement.append("(");
            statement.append(valueStatement);
            statement.append(")");
        } else {// 属性
            statement.append(VAR_TARGET);
            statement.append(".");
            statement.append(leftField.name);
            statement.append(" = ");
            statement.append(valueStatement);
        }

        return statement.toString();
    }

    private String getEqualTypeAssignmentStatement() {
        return assemblyAssignmentStatement(null, null, null);
    }


    private String getINTTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.INT.class.getName() + ".to", null, null);
    }

    private String getSHORTTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.SHORT.class.getName() + ".to", null, null);
    }

    private String getLONGTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.LONG.class.getName() + ".to", null, null);
    }

    private String getFLOATTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.FLOAT.class.getName() + ".to", null, null);
    }

    private String getDOUBLETypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.DOUBLE.class.getName() + ".to", null, null);
    }

    private String getBOOLTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.BOOL.class.getName() + ".to", null, null);
    }

    private String getCHARTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.CHAR.class.getName() + ".to", null, null);
    }

    private String getBYTETypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.BYTE.class.getName() + ".to", null, null);
    }

    private String getStringTypeAssignmentStatement() {
        return assemblyAssignmentStatement(Converters.Str.class.getName() + ".to", null, null);
    }

    private String getARRAYTypeAssignmentStatement(String type, String elementType) {
        return assemblyAssignmentStatement(Converters.Array.class.getName() + ".to", type, elementType);
    }

    private String getCollectionTypeAssignmentStatement(String type, String elementType) {
        return assemblyAssignmentStatement(Converters.Collection.class.getName() + ".to", type, elementType);
    }

    private String getObjTypeAssignmentStatement(String type, String elementType) {
        return assemblyAssignmentStatement(Converters.Obj.class.getName() + ".to", type, elementType);
    }

    private String getMapTypeAssignmentStatement(String elementType) {
        return assemblyAssignmentStatement(Converters.Obj.class.getName() + ".to", elementType, null);
    }


    public FieldInfo getLeftField() {
        return leftField;
    }

    public MethodInfo getLeftSetter() {
        return leftSetter;
    }

    public MethodInfo getLeftGetter() {
        return leftGetter;
    }

    public FieldInfo getRightField() {
        return rightField;
    }

    public MethodInfo getRightGetter() {
        return rightGetter;
    }
}
