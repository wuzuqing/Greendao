package cn.wuzuqing.greendao;

import android.app.Application;

import cn.wuzuqing.greendao.dao.DbCodeManager;
import cn.wuzuqing.greendao.utils.SPUtil;

/**
 * @author: 吴祖清
 * @createDate: 2018/11/12 9:51
 * @description: java类作用描述
 * @updateUser: 吴祖清
 * @updateDate: 2018/11/12 9:51
 * @updateRemark: 更新说明
 * @version: 1.0
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SPUtil.init(getApplicationContext());
        DbCodeManager.init(getApplicationContext());
//        Dbcode.init(getApplicationContext());
    }
}
