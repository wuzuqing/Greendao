package cn.wuzuqing.lib;

import android.app.Activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author: 吴祖清
 * @createDate: 2018/11/12 10:18
 * @description: java类作用描述
 * @updateUser: 吴祖清
 * @updateDate: 2018/11/12 10:18
 * @updateRemark: 更新说明
 * @version: 1.0
 */
public class Bind {

    public static void bind(Activity activity) {
        //在这里对activity中的字段进行负值或者调用负值的方法
        //使用反射的方法调用我们自动生成的类
        try {
            //获取class对象
            Class<?> bindClass = Class.forName(activity.getClass().getCanonicalName() + "$Binding");
            // 获取有参数的构造方法(构造方法的参数)
            Constructor<?> constructor = bindClass.getDeclaredConstructor(Class.forName(activity.getClass().getCanonicalName()));
            //调用构造方式
            constructor.newInstance(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
