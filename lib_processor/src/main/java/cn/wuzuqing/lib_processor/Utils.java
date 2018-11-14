package cn.wuzuqing.lib_processor;

import javax.lang.model.element.Element;


public class Utils {
    public static String getName(Element element) {
        return element.getSimpleName().toString();
    }

    public static String getFieldType(Element element) {
        return element.asType().toString().replaceAll("java.lang.", "");
    }

    public static String getDbNameByClassName(String name) {
        char[] chars = name.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c >= 'A' && c <= 'Z') {
                if (i != 0) {
                    sb.append("_");
                }
                sb.append((char) (c + 32));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }


    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    public static String getColumnType(String field) {
        switch (field) {
            case "String":
                return "TEXT";
            case "Integer":
            case "Boolean":
            case "Short":
            case "Byte":
            case "Long":
                return "INTEGER";
            case "Double":
            case "Float":
                return "REAL";
            default:
                return "TEXT";
        }
    }

    public static String getBindDbType(String type) {
        switch (type) {
            case "String":
                return "String";
            case "Short":
            case "Boolean":
            case "Byte":
            case "Integer":
            case "Long":
                return "Long";
            case "Double":
            case "Float":
                return "Double";
            default:
        }
        return "String";
    }
}
