package cn.wuzuqing.lib;

import java.util.List;

/**
 * @author: 吴祖清
 * @createDate: 2018/11/12 11:08
 * @description: java类作用描述
 * @updateUser: 吴祖清
 * @updateDate: 2018/11/12 11:08
 * @updateRemark: 更新说明
 * @version: 1.0
 */
public interface BaseDao<K extends Number, T> {

    /**
     * 添加单条数据
     *
     * @param entity 要添加的数据
     * @return 受影响的数量
     */
    long insert(T entity);

    /**
     * 添加多条数据
     *
     * @param entity 要添加的数据
     * @return 受影响的数量
     */
    long insertIx(T... entity);

    /**
     * 添加多条数据
     *
     * @param entity 要添加的数据
     * @return 受影响的数量
     */
    long insertIx(Iterable<T> entity);

    /**
     * 更新单条数据
     *
     * @param entity 要更新的数据
     * @return 受影响的数量
     */
    long update(T entity);


    /**
     * 更新多条数据
     *
     * @param entity 要更新的数据
     * @return 受影响的数量
     */
    long updateIx(T... entity);


    /**
     * 更新多条数据
     *
     * @param entity 要更新的数据
     * @return 受影响的数量
     */
    long updateIx(Iterable<T> entity);


    /**
     * 根据对象删除数据
     *
     * @param entity 要删除的数据
     * @return 受影响的数量
     */
    long delete(T entity);

    /**
     * 根据对象的id删除数据
     *
     * @param key 主键id
     * @return 受影响的数量
     */
    long delete(K key);

    /**
     * 删除所有数据
     *
     * @return 受影响的数量
     */
    long deleteAll();

    /**
     * 查询所有数据
     *
     * @return 数据集合
     */
    List<T> loadAll();


    List<T> loadPage(String key,Object value,int pageSize);
    /**
     * 根据id查询数据
     *
     * @param key 主键id
     * @return
     */
    T findById(K key);
}
