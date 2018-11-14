package cn.wuzuqing.greendao.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static SPUtil instance;
    private SharedPreferences preferences;

    private SPUtil(Context context) {
        preferences = context.getSharedPreferences("sputil.xml", Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(context);
                }
            }
        }
    }

    public static SPUtil getInstance() {
        return instance;
    }

    public void put(String key, Object obj) {
        SharedPreferences.Editor edit = preferences.edit();
        if (obj instanceof String) {
            edit.putString(key, obj.toString());
        } else if (obj instanceof Integer) {
            edit.putInt(key, (Integer) obj);
        } else if (obj instanceof Boolean) {
            edit.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Float) {
            edit.putFloat(key, (Float) obj);
        } else if (obj instanceof Long) {
            edit.putLong(key, (Long) obj);
        }
        edit.apply();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0L);
    }

    public float getFloat(String key) {
        return preferences.getFloat(key, 0f);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }


}
