package cn.wuzuqing.greendao.dao;
import cn.wuzuqing.greendao.bean.School;
import cn.wuzuqing.lib.AbstractDao;
import cn.wuzuqing.lib.Database;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;
public class SchoolDao extends AbstractDao<Long, School> {

	public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS school";

	public SchoolDao(Database database) {super(database);}

	@Override 
	protected String tableName() { return "school"; }

	@Override 
	protected String keyName() { return "id"; }


	@Override 
	protected void setKey(School entity,Long id) {
		 entity.setId(id) ;
	 }


	@Override 
	protected Long readKey(School entity) {
 return entity.getId(); 
}

	public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS school (id INTEGER primary key autoincrement ,name TEXT,address TEXT,createDate INTEGER,sex INTEGER,old INTEGER,ex0 INTEGER,ex1 REAL,ex2 REAL,ex3 INTEGER)";

	@Override 
	protected String[] getColumns(){
		 return new String[]{"id","name","address","createDate","sex","old","ex0","ex1","ex2","ex3"};
	}


	@Override 
	protected School readEntity(Cursor cursor, int offset) {
		return new School(
			cursor.isNull(offset + 0)? null : cursor.getLong(0 + offset),
			cursor.isNull(offset + 1)? null : cursor.getString(1 + offset),
			cursor.isNull(offset + 2)? null : cursor.getString(2 + offset),
			cursor.isNull(offset + 3)? null : cursor.getLong(3 + offset),
			cursor.isNull(offset + 4)? null : cursor.getShort(4 + offset),
			cursor.isNull(offset + 5)? null : cursor.getShort(5 + offset)!=0,
			cursor.isNull(offset + 6)? null : cursor.getInt(6 + offset),
			cursor.isNull(offset + 7)? null : cursor.getFloat(7 + offset),
			cursor.isNull(offset + 8)? null : cursor.getDouble(8 + offset),
			cursor.isNull(offset + 9)? null : (byte)cursor.getShort(9 + offset));
	}

	@Override 
	protected void bindValue(SQLiteStatement stmt, School entity) {
		stmt.clearBindings();
		Long id = entity.getId();
		if ( id != null) {
			stmt.bindLong(1, id);
		}
		String name = entity.getName();
		if ( name != null) {
			stmt.bindString(2, name);
		}
		String address = entity.getAddress();
		if ( address != null) {
			stmt.bindString(3, address);
		}
		Long createDate = entity.getCreateDate();
		if ( createDate != null) {
			stmt.bindLong(4, createDate);
		}
		Short sex = entity.getSex();
		if ( sex != null) {
			stmt.bindLong(5, sex);
		}
		Boolean old = entity.getOld();
		if ( old != null) {
			stmt.bindLong(6, old?1L:0L );
		}
		Integer ex0 = entity.getEx0();
		if ( ex0 != null) {
			stmt.bindLong(7, ex0);
		}
		Float ex1 = entity.getEx1();
		if ( ex1 != null) {
			stmt.bindDouble(8, ex1);
		}
		Double ex2 = entity.getEx2();
		if ( ex2 != null) {
			stmt.bindDouble(9, ex2);
		}
		Byte ex3 = entity.getEx3();
		if ( ex3 != null) {
			stmt.bindLong(10, ex3);
		}
	}

	@Override
	protected boolean isEntityUpdateable() {
		return false;
	}
}
