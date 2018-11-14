package cn.wuzuqing.greendao.dao;

import java.util.Arrays;
import java.util.List;
import cn.wuzuqing.lib.Database;
import cn.wuzuqing.lib.LifeCallBack;


public class DaoSession  implements LifeCallBack {
	private SchoolDao schoolDao;
	public DaoSession(Database database) {
		schoolDao = new SchoolDao(database);
	}

	public SchoolDao getSchoolDao() {return schoolDao;}

	@Override
	public List<String> getAllCreateSql() {
		return Arrays.asList(SchoolDao.CREATE_TABLE_SQL);
	}

	@Override
	public List<String> getAllDropSql() {
		return Arrays.asList(SchoolDao.DROP_TABLE_SQL);
	}

}