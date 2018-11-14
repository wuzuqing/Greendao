package cn.wuzuqing.lib;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author: 吴祖清
 * @createDate: 2018/11/12 11:10
 * @description: java类作用描述
 * @updateUser: 吴祖清
 * @updateDate: 2018/11/12 11:10
 * @updateRemark: 更新说明
 * @version: 1.0
 */
public abstract class AbstractDao<K extends Number, T> implements BaseDao<K, T> {

    protected Database database;
    private String insertSql;
    private String updateSql;
    private int updateKeyIndex;

    private static final int WORK_INSERT = 0;
    private static final int WORK_UPDATE = 1;
    private static final int WORK_DELETE = 2;

    public AbstractDao(Database database) {
        this.database = database;
        String[] columns = getColumns();
        insertSql = SqlUtils.createSqlInsert("INSERT INTO ", tableName(), columns);
        updateSql = SqlUtils.createSqlUpdate(tableName(), columns, new String[]{keyName()});
        updateKeyIndex = columns.length + 1;
    }


    protected static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    /**
     * 获取所有字段
     *
     * @return 字段数组
     */
    protected abstract String[] getColumns();

    /**
     * 读取数据
     *
     * @param cursor 游标
     * @param offset 偏移位置
     * @return 数据
     */
    abstract protected T readEntity(Cursor cursor, int offset);

    /**
     * 绑定数据
     *
     * @param stat   数据库操作对象
     * @param entity 数据
     */
    abstract protected void bindValue(SQLiteStatement stat, T entity);

    /**
     * 获取主键名称
     *
     * @return 主键名称
     */
    abstract protected String keyName();

    /**
     * 获取数据表名
     *
     * @return
     */
    abstract protected String tableName();

    /**
     * 从对象获取主键
     *
     * @param entity 对象
     * @return 主键
     */
    abstract protected K readKey(T entity);

    /**
     * 给对象设置主键
     *
     * @param entity 对象
     * @param id     主键的值
     */
    abstract protected void setKey(T entity, K id);


    protected String selectSql(K key) {
        if (key == null) {
            return format("select * from %s", tableName());
        }
        return format("select * from %s where %s = %s", tableName(), keyName(), key.toString());
    }

    protected String deleteSql(K key) {
        if (key == null) {
            return format("DELETE FROM %s", tableName());
        }
        return format("DELETE FROM %s WHERE %s = %s", tableName(), keyName(), key.toString());
    }

    @Override
    public long insertIx(T... entity) {
        return insertIx(Arrays.asList(entity));
    }

    @Override
    public long insert(T entity) {
        return insertIx(entity);
    }

    @Override
    public long insertIx(Iterable<T> entityList) {
        return doWork(entityList, insertSql, WORK_INSERT);
    }

    @Override
    public long updateIx(T... entity) {
        return updateIx(Arrays.asList(entity));
    }

    @Override
    public long update(T entity) {
        return updateIx(entity);
    }

    @Override
    public long updateIx(Iterable<T> entityList) {
        return doWork(entityList, updateSql, WORK_UPDATE);
    }

    @Override
    public long delete(T entity) {
        return delete(readKey(entity));
    }

    @Override
    public long delete(K key) {
        return doWork(null, deleteSql(key), WORK_DELETE);
    }

    @Override
    public long deleteAll() {
        return doWork(null, deleteSql(null), WORK_DELETE);
    }

    private long doWork(Iterable<T> entityList, String sql, int type) {
        long rows = 0;
        SQLiteDatabase db = null;
        try {
            db = database.getWritableDatabase();
            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            if (type == WORK_DELETE) {
                rows += stat.executeUpdateDelete();
            } else {
                if (entityList == null) {
                    return 0;
                }
                for (T entity : entityList) {
                    switch (type) {
                        //插入数据
                        case WORK_INSERT:
                            bindValue(stat, entity);
                            long result = stat.executeInsert();
                            if (result < 0) {
                                return -1;
                            }
                            setKey(entity, (K) Long.valueOf(result));
                            rows += 1;
                            break;
                        //更新数据
                        case WORK_UPDATE:
                            K key = readKey(entity);
                            if (key.longValue() < 0) {
                                continue;
                            }
                            bindValue(stat, entity);
                            stat.bindLong(updateKeyIndex, key.longValue());
                            rows += stat.executeUpdateDelete();
                            break;
                        default:
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rows;
    }


    @Override
    public List<T> loadPage(String key, Object value, int pageSize) {
        return queryList(format("select * from %s where %s = %s LIMIT %d", key, value.toString(), pageSize));
    }

    @Override
    public List<T> loadAll() {
        return queryList(selectSql(null));
    }

    private List<T> queryList(String sql) {
        Cursor cursor = database.getReadableDatabase().rawQuery(sql, null);
        List<T> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(readEntity(cursor, 0));
        }
        cursor.close();
        return result;
    }


    @Override
    public T findById(K key) {
        Cursor cursor = database.getReadableDatabase().rawQuery(selectSql(key), null);
        T entity = null;
        if (cursor.moveToNext()) {
            entity = readEntity(cursor, 0);
        }
        cursor.close();
        return entity;
    }
}
