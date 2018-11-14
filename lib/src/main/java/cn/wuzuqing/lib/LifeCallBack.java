package cn.wuzuqing.lib;

import java.util.List;

public interface LifeCallBack {
    List<String> getAllCreateSql();

    List<String> getAllDropSql();
}