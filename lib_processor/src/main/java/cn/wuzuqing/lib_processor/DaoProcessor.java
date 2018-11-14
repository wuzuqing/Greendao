package cn.wuzuqing.lib_processor;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import cn.wuzuqing.lib_annotation.Entity;
import cn.wuzuqing.lib_annotation.Gentrace;
import cn.wuzuqing.lib_annotation.Id;
import cn.wuzuqing.lib_annotation.Index;
import cn.wuzuqing.lib_annotation.ScanPackage;
import cn.wuzuqing.lib_annotation.Transient;


@AutoService(Processor.class)
public class DaoProcessor extends AbstractProcessor {

    Elements elementUtils;
    private File rootFile;
    private File srcFile;
    private String srcPath = "/app/src/main/java/";
    private String daoPackage = "";
    Random random = new Random();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elememts = roundEnvironment.getElementsAnnotatedWith(Entity.class);

        TypeElement classElement;

        Map<String, List<VariableElement>> fieldMaps = new HashMap<>();
        Map<String, List<ExecutableElement>> constructorMaps = new HashMap<>();
        String packName = "";

        for (Element ele : elememts) {
            if (ele.getKind() == ElementKind.CLASS) {
                classElement = (TypeElement) ele;
                packName = classElement.getQualifiedName().toString();
                List<? extends Element> allMembers = elementUtils.getAllMembers(classElement);
                if (allMembers.size() > 0) {
                    fieldMaps.put(packName, ElementFilter.fieldsIn(allMembers));
                    constructorMaps.put(packName, ElementFilter.constructorsIn(allMembers));
                }
            }
        }
        String regular = "";
        if (packName.contains(".bean")) {
            regular = ".bean";
        } else if (packName.contains(".entity")) {
            regular = ".entity";
        }
        int index = packName.lastIndexOf(regular);
        if (index != -1) {
            packName = packName.substring(0, index);
            if (packName == null || packName.length() < 5) {
                return false;
            }
            daoPackage = packName + ".dao";
        }
        srcFile = new File(System.getProperty("user.dir"), srcPath);
        rootFile = new File(srcFile, daoPackage.replaceAll("\\.", "/"));
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }

        generateFile(fieldMaps, constructorMaps, daoPackage);
        return false;
    }


    private void generateFile(Map<String, List<VariableElement>> maps, Map<String, List<ExecutableElement>> constructorMaps, String packageName) {
        boolean needRefreshSession = false;
        List<String> classNames = new ArrayList<>();
        String filePath = "";
        for (String key : maps.keySet()) {
            try {
                TypeElement typeElement = elementUtils.getTypeElement(key);
                String className = getName(typeElement);
                classNames.add(className);
                filePath = packageName + "." + className + "Dao";
                if (!check(maps.get(key), constructorMaps.get(key)) && existsFile(filePath, false).exists()) {
                    continue;
                }

                Entity entity = typeElement.getAnnotation(Entity.class);
                String tableName = entity.nameInDb();
                if (tableName == null || tableName.length() == 0) {
                    tableName = Utils.getDbNameByClassName(className);
                }
                List<FieldProperty> fieldProperties = refreshClassFile(key, maps.get(key), typeElement);
                String idToUpperName = Utils.toUpperCaseFirstOne(idProperty.getFieldName());
                StringBuilder sb = new StringBuilder("package " + packageName + ";\n");
                sb.append("import " + key + ";\n").
                        append("import cn.wuzuqing.lib.AbstractDao;\n").
                        append("import cn.wuzuqing.lib.Database;\n").
                        append("import android.database.sqlite.SQLiteStatement;\n").
                        append("import android.database.Cursor;\n").

                        append("public class " + className + "Dao extends AbstractDao<Long, " + className + "> {\n").
                        append("\n\tpublic static final String DROP_TABLE_SQL = \"DROP TABLE IF EXISTS " + tableName + "\";\n").

                        append("\n\tpublic " + className + "Dao(Database database) {\n\t\tsuper(database);\n\t\t isSetPrimaryKey = CREATE_TABLE_SQL.contains(\"autoincrement\");\n\t}\n").

                        append("\n\t@Override \n\tprotected String tableName() { return \"" + tableName + "\"; }\n").

                        append("\n\t@Override \n\tprotected String keyName() { return \"" + idProperty.getFieldName() + "\"; }\n\n").

                        append("\n\t@Override \n\tprotected void setKey(" + className + " entity," + idProperty.getFieldType() + " " + idProperty.getFieldName()
                                + ") {\n\t\t entity.set" + idToUpperName + "(" + idProperty.getFieldName() + ") ;\n\t }\n\n").

                        append("\n\t@Override \n\tprotected Long readKey(" + className + " entity) {\n return entity.get" +
                                idToUpperName + "(); \n}\n");


                toList(sb, fieldProperties, tableName, className);
                sb.append("}\n");
                writeData(filePath, sb.toString(), true);
                needRefreshSession = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        createDaoMaster(packageName);
        createDCManager(packageName);
        createDaoSession(classNames, packageName, needRefreshSession);
    }

    private FieldProperty idProperty;

    private List<FieldProperty> refreshClassFile(String key, List<VariableElement> variableElements, TypeElement classElement) {
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        StringBuilder sb = new StringBuilder("package " + packageName + ";\n\n");
        String name = Entity.class.getPackage().getName();
        sb.append("import " + name + ".*;\n\n");

        Entity entity = classElement.getAnnotation(Entity.class);
        if (isEmpty(entity.nameInDb())) {
            sb.append("@Entity()\n");
        } else {
            sb.append("@Entity( nameInDb = \"" + entity.nameInDb() + "\")\n");
        }

        String className = getName(classElement);

        sb.append("public class " + className + " {\n\n");

        List<FieldProperty> fieldProperties = new ArrayList<>();
        FieldProperty fieldProperty;
        int tranCount = 0;
        // 字段
        for (VariableElement variableElement : variableElements) {
            fieldProperty = new FieldProperty(variableElement);
            Transient annotation = variableElement.getAnnotation(Transient.class);
            if (annotation != null) {
                tranCount++;
                fieldProperty.appendTransient(sb, "Transient");
            } else {
                Id id = variableElement.getAnnotation(Id.class);
                if (id != null) {
                    idProperty = fieldProperty;
                    fieldProperty.appendAnnotation(sb, "Id", "autoincrement", true, !id.autoincrement());
                }
                Index index = variableElement.getAnnotation(Index.class);
                if (index != null) {
                    fieldProperty.appendAnnotation(sb, "Index", "name", index.name(), isEmpty(index.name()));
                }
            }

            fieldProperty.appendSelf(sb);

            fieldProperties.add(fieldProperty);
        }
        appendGentrace(sb);
        sb.append("\tpublic " + className + "(){\n\t}\n\n");

        appendGentrace(sb);
        StringBuilder content = new StringBuilder();
        sb.append("\tpublic " + className + "(");

        int maxSize = fieldProperties.size() - 1 - tranCount;
        for (int i = 0; i <= maxSize; i++) {
            FieldProperty property = fieldProperties.get(i);
            if (!property.isTransient()) {
                sb.append(property.getFieldType()).append(" ").append(property.getFieldName());
                if (i != maxSize) {
                    sb.append(",");
                }
                content.append("\t\tthis.").append(property.getFieldName()).append(" = ").append(property.getFieldName()).append(";\n");
            }
        }
        sb.append("){\n").append(content).append("\t}\n\n");

        StringBuilder toStr = new StringBuilder("\t@Override\tpublic String toString() {\n\t\treturn \"").append(className).append("{\" +\n");
        boolean isFirst = true;
        for (FieldProperty property : fieldProperties) {
            property.appendGetSet(sb);
            if (isFirst) {
                isFirst = false;
                toStr.append("\t\t\t\t\"").append(property.getFieldName()).append("=\" + ").append(property.getFieldName()).append(" + \n");
            } else {
                toStr.append("\t\t\t\t\",").append(property.getFieldName()).append("=\" + ").append(property.getFieldName()).append(" + \n");
            }

        }
        toStr.append("\t\t\t\t\"}\";\n\t}");




        sb.append(toStr);
        sb.append("\n\n}\n");
        writeData(key, sb.toString(), false);

        return fieldProperties;
    }

    private void appendGentrace(StringBuilder sb) {
        sb.append("\t@Gentrace(").append(random.nextInt()).append(")\n");
    }


    private String getName(Element element) {
        return element.getSimpleName().toString();
    }

    private String getFieldType(Element element) {
        return element.asType().toString().replaceAll("java.lang.", "");
    }

    private boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    private boolean check(List<VariableElement> elements, List<ExecutableElement> constructors) {
        if (constructors == null || constructors.size() == 0) {
            return true;
        }
        List<String> sureConstructor = new ArrayList<>();
        List<String> annotationConstructor = new ArrayList<>();
        VariableElement element;
        String fieldName;
        String fieldType;
        int gentraceCount = 0;
        for (int i = 0; i < constructors.size(); i++) {
            ExecutableElement constructorElement = constructors.get(i);
            Gentrace gentrace = constructorElement.getAnnotation(Gentrace.class);
            if (gentrace != null) {
                List<? extends VariableElement> elementParameters = constructorElement.getParameters();
                gentraceCount++;
                if (elementParameters.size() > 0) {
                    for (VariableElement elementParameter : elementParameters) {
                        fieldName = Utils.getName(elementParameter);
                        fieldType = Utils.getFieldType(elementParameter);
                        annotationConstructor.add(fieldType + "_" + fieldName);
                    }
                }
            }
        }
        if (gentraceCount != 2) {
            return true;
        }

        for (int i = 0; i < elements.size(); i++) {
            element = elements.get(i);
            Transient aTransient = element.getAnnotation(Transient.class);
            if (aTransient != null) {
                continue;
            }
            fieldName = getName(element);
            fieldType = getFieldType(element);
            sureConstructor.add(fieldType + "_" + fieldName);
        }

        if (sureConstructor.size() != annotationConstructor.size()) {
            return true;
        }
        Collections.sort(sureConstructor);
        Collections.sort(annotationConstructor);


        for (int i = 0; i < sureConstructor.size(); i++) {
            if (!sureConstructor.get(i).equals(annotationConstructor.get(i))) {
                return true;
            }
        }
        return false;
    }


    private void createDaoSession(List<String> classNames, String newPackage, boolean notNeedRefresh) {
        if (!notNeedRefresh) {
            return;
        }
        String fileName = newPackage + ".DaoSession";
        StringBuilder sb = new StringBuilder("package " + newPackage + ";\n\n");
        sb.append("import java.util.Arrays;\n");
        sb.append("import java.util.List;\n");
        sb.append("import cn.wuzuqing.lib.Database;\n");
        sb.append("import cn.wuzuqing.lib.LifeCallBack;\n\n\n");

        StringBuilder fields = new StringBuilder();

        StringBuilder methods = new StringBuilder();

        StringBuilder constructor = new StringBuilder("\tpublic DaoSession(Database database) {\n");

        StringBuilder createSql = new StringBuilder("\t@Override\n\tpublic List<String> getAllCreateSql() {\n");

        StringBuilder dropSql = new StringBuilder("\t@Override\n\tpublic List<String> getAllDropSql() {\n");

        createSql.append("\t\treturn Arrays.asList(");
        dropSql.append("\t\treturn Arrays.asList(");

        String filedName;
        int maxSize = classNames.size() - 1;
        int count = 0;
        for (String name : classNames) {
            filedName = Utils.toLowerCaseFirstOne(name) + "Dao";
            fields.append("\tprivate ").append(name).append("Dao ").append(filedName).append(";\n");
            constructor.append("\t\t").append(filedName).append(" = new ").append(name).append("Dao(database);\n");
            methods.append("\tpublic ").append(name).append("Dao get").append(name).append("Dao() {return ").append(filedName).append(";}\n\n");
            if (count < maxSize) {
                createSql.append(String.format("%sDao.CREATE_TABLE_SQL,", name));
                dropSql.append(String.format("%sDao.DROP_TABLE_SQL,", name));
            } else {
                createSql.append(String.format("%sDao.CREATE_TABLE_SQL", name));
                dropSql.append(String.format("%sDao.DROP_TABLE_SQL", name));
            }
            count++;
        }
        constructor.append("\t}\n\n");
        createSql.append(");\n\t}\n\n");
        dropSql.append(");\n\t}\n\n");
        sb.append("public class DaoSession  implements LifeCallBack {\n");
        sb.append(fields.toString());
        sb.append(constructor.toString());
        sb.append(methods.toString());
        sb.append(createSql.toString());
        sb.append(dropSql.toString());
        sb.append("}");
        writeData(fileName, sb.toString(), true);
    }

    private void createDCManager(String newPackage) {
        String fileName = newPackage + ".DbCodeManager";
        if (existsFile(fileName, false).exists()) {
            return;
        }

        String sb = "package " + newPackage + ";\n" + "import android.content.Context;\n\n\n" +
                "public class DbCodeManager {\n\n" +
                "\tprivate static DbCodeManager INSTANCE;\n\n" +
                "\tprivate static final String DB_NAME = \"im.db\";\n\n" +
                "\tprivate DaoMaster daoMaster;\n\n" +
                "\tprivate DaoSession session;\n\n" +
                "\tprivate DbCodeManager(Context context) {\n" +
                "\t\tdaoMaster = new DaoMaster(context, DB_NAME);\n" +
                "\t\tthis.session = daoMaster.getSession();\n" +
                "\t}\n\n" +
                "\tpublic static DbCodeManager getInstance() { \n\t\t return INSTANCE;\n\t} \n\n" +
                "\tpublic synchronized static void init(Context context) {\n" +
                "\t\tif (INSTANCE == null) {\n" +
                "\t\t\tINSTANCE = new DbCodeManager(context);\n" +
                "\t\t}\n\t}\n\n" +
                "\tpublic DaoSession getSession() { \n\t\t return session;\n\t} \n\n" +
                "}";
        writeData(fileName, sb, true);
    }

    private void createDaoMaster(String newPackage) {
        String fileName = newPackage + ".DaoMaster";
        if (existsFile(fileName, false).exists()) {
            return;
        }
        String sb = "package " + newPackage + ";\n\n" +
                "import android.content.Context;\n" +
                "import cn.wuzuqing.lib.AbsDaoMaster;\n" +
                "import cn.wuzuqing.lib.DevOpenHelper;\n\n\n" +
                "public class DaoMaster extends AbsDaoMaster {\n\n" +
                "\tpublic static final int SCHEMA_VERSION = 1;\n\n" +
                "\tprivate DaoSession daoSession;\n\n" +
                "\tpublic DaoMaster(Context context, String name) {\n" +
                "\t\tDevOpenHelper helper = new DevOpenHelper(context, name, SCHEMA_VERSION);\n" +
                "\t\tdaoSession = new DaoSession(helper);\n" +
                "\t\thelper.setLifeCallBack(daoSession);\n" +
                "\t}\n\n" +
                "\tpublic DaoSession getSession() {\n\t\treturn daoSession;\n}\n\n" +
                "}";

        writeData(fileName, sb, true);
    }

    private void toList(StringBuilder sb, List<FieldProperty> fieldProperties, String tableName, String className) {

        StringBuffer createSql = new StringBuffer();
        StringBuffer readEntity = new StringBuffer();
        StringBuffer bindValue = new StringBuffer();
        StringBuffer columns = new StringBuffer();
        createSql.append("\n\tpublic static final String CREATE_TABLE_SQL = \"CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        readEntity.append("\n\t@Override \n\tprotected " + className + " readEntity(Cursor cursor, int offset) {\n\t\t")
                .append("return new " + className + "(");

        bindValue.append("\n\t@Override \n\tprotected void bindValue(SQLiteStatement stmt, " + className + " entity) {\n\t\tstmt.clearBindings();\n");

        columns.append("\n\t@Override \n\tprotected String[] getColumns(){\n\t\t return new String[]{");
        String columnName;
        for (int i = 0; i < fieldProperties.size(); i++) {
            FieldProperty property = fieldProperties.get(i);
            if (property.isTransient()) {
                continue;
            }
            String readTypeName = property.getFieldType();
            if (property.getFieldType().equals("Integer")) {
                readTypeName = "Int";
            }
            // cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)
            if (readTypeName.equals("Byte")) {
                readEntity.append("\n\t\t\tcursor.isNull(offset + " + i + ")? null : (byte)cursor.getShort(" + i + " + offset),");
            } else if (readTypeName.equals("Boolean")) {
                readEntity.append("\n\t\t\tcursor.isNull(offset + " + i + ")? null : cursor.getShort(" + i + " + offset)!=0,");
            } else {
                readEntity.append("\n\t\t\tcursor.isNull(offset + " + i + ")? null : cursor.get" + readTypeName + "(" + i + " + offset),");
            }
            Id id = property.getElement().getAnnotation(Id.class);
            if (id != null) {
                createSql.append(property.getFieldName()).append(" INTEGER primary key ");
                if (id.autoincrement()) {
                    createSql.append("autoincrement ");
                }
                columns.append("\"").append(property.getFieldName()).append("\",");
            } else {
                Index index = property.getElement().getAnnotation(Index.class);
                if (index != null) {
                    columnName = index.name();
                } else {
                    columnName = property.getFieldName();
                }
                columns.append("\"").append(columnName).append("\",");
                createSql.append(",").append(columnName).append(" ").append(Utils. getColumnType(property.getFieldType()));
            }

            bindValue.append("\t\t").append(property.getFieldType()).append(" ").append(property.getFieldName())
                    .append(" = entity.get").append(Utils.toUpperCaseFirstOne(property.getFieldName())).append("();\n")
                    .append("\t\tif ( " + property.getFieldName() + " != null) {\n");
            if (property.getFieldType().equals("Boolean")) {
                bindValue.append("\t\t\tstmt.bindLong(" + (i + 1) + ", " +
                        property.getFieldName() + "?1L:0L );\n\t\t}\n");
            } else {
                bindValue.append("\t\t\tstmt.bind" + Utils.getBindDbType(property.getFieldType()) + "(" + (i + 1) + ", " +
                        property.getFieldName() + ");\n\t\t}\n");
            }

        }

        createSql.append(")\";\n");
        bindValue.append("\t}\n");
        readEntity.deleteCharAt(readEntity.length() - 1);
        readEntity.append(");\n\t}\n");
        columns.deleteCharAt(columns.length() - 1);
        columns.append("};\n\t}\n\n");

        sb.append(createSql.toString());
        sb.append(columns.toString());
        sb.append(readEntity.toString());
        sb.append(bindValue.toString());

    }


    private File existsFile(String className, boolean isDelete) {
        String name = className.substring(className.lastIndexOf(".") + 1) + ".java";
        File file = new File(rootFile, name);
        if (isDelete) {
            file.delete();
        }
        return file;
    }

    private void writeData(String className, String content, boolean useDaoPackage) {
        try {
            File file = null;
            if (useDaoPackage) {
                file = existsFile(className, true);
            } else {
                String name = className.replaceAll("\\.", "/") + ".java";
                file = new File(srcFile, name);
                if (file.exists()) {
                    file.delete();
                }
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();

//            JavaFileObject source = processingEnv.getFiler()
//                    .createSourceFile(className);
//            Writer writer = source.openWriter();
//            writer.write(content);
//            writer.flush();
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        super.getSupportedAnnotationTypes();
        Set<String> types = new HashSet<>();
        types.add(Entity.class.getCanonicalName());
        types.add(Id.class.getCanonicalName());
        types.add(Index.class.getCanonicalName());
        types.add(Gentrace.class.getCanonicalName());
        types.add(ScanPackage.class.getCanonicalName());
        types.add(Transient.class.getCanonicalName());
        return types;
    }
}
