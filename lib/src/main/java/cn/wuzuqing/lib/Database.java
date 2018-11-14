package cn.wuzuqing.lib;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author: 吴祖清
 * @createDate: 2018/11/12 13:08
 * @description:
 * @updateUser: 吴祖清
 * @updateDate: 2018/11/12 13:08
 * @updateRemark: 更新说明
 * @version: 1.0
 */
public interface Database {

    SQLiteDatabase getWritableDatabase();

    SQLiteDatabase getReadableDatabase();

//    long insertIx(String table, ContentValues values);
//
//    int updateIx(String table, ContentValues values, String whereClause, String[] whereArgs);
//
//    int delete(String table, String whereClause, String[] whereArgs);
//
//    Cursor rawQuery(String sql, String[] selectionArgs);
//
//    void execSQL(String sql, Object[] selectionArgs);
//
//    void execSQL(String sql);
}
