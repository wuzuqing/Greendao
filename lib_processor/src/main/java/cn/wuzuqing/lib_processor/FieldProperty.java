package cn.wuzuqing.lib_processor;

import javax.lang.model.element.VariableElement;


public class FieldProperty {
    private VariableElement element;
    private String fieldName;
    private String fieldType;
    private boolean isTransient;

    public FieldProperty(VariableElement element) {
        this.element = element;
        fieldType = Utils.getFieldType(element);
        fieldName = Utils.getName(element);
    }

    public VariableElement getElement() {
        return element;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    public void setElement(VariableElement element) {
        this.element = element;


    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }


    public void appendSelf(StringBuilder sb) {
        sb.append("\tprivate ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
    }

    public void appendGetSet(StringBuilder sb) {
        sb.append("\tpublic void set")
                .append(Utils.toUpperCaseFirstOne(fieldName)).append(" ( ").append(fieldType).append(" ").append(fieldName).append(" ){\n")
                .append("\t\tthis.").append(fieldName).append(" = ").append(fieldName).append(";\n\t}\n\n");

        sb.append("\tpublic ").append(fieldType).append(" get").append(Utils.toUpperCaseFirstOne(fieldName)).append(" () {\n")
                .append("\t\treturn this.").append(fieldName).append(" ;\n\t}\n\n");
    }

    public void appendTransient(StringBuilder sb, String annotationName) {
        sb.append("\t@" + annotationName + "()\n");
        if (annotationName.equals("Transient")) {
            isTransient = true;
        }
    }


    public void appendAnnotation(StringBuilder sb, String annotationName, String key, Object value, boolean isEmpty) {
        if (isEmpty) {
            appendTransient(sb, annotationName);
        } else {
            if (value instanceof String) {
                sb.append("\t@" + annotationName + "(" + key + " = \"" + value + "\")\n");
            } else {
                sb.append("\t@" + annotationName + "(" + key + " = " + value + ")\n");
            }
        }
    }
}
