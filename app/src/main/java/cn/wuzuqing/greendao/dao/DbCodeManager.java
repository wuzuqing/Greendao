package cn.wuzuqing.greendao.dao;
import android.content.Context;


public class DbCodeManager {

	private static DbCodeManager INSTANCE;

	private static final String DB_NAME = "im.db";

	private DaoMaster daoMaster;

	private DaoSession session;

	private DbCodeManager(Context context) {
		daoMaster = new DaoMaster(context, DB_NAME);
		this.session = daoMaster.getSession();
	}

	public static DbCodeManager getInstance() { 
		 return INSTANCE;
	} 

	public synchronized static void init(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new DbCodeManager(context);
		}
	}

	public DaoSession getSession() { 
		 return session;
	} 

}