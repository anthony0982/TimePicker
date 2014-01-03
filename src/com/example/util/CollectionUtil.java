package com.example.util;

import java.util.Collection;
import java.util.Map;

/**
 * 集合对象工具类
 * 
 * @author wangzengyang@gmail.com 2012-11-12
 * 
 */
public class CollectionUtil {

    /**
     * 获取集合大小
     * 
     * @param collection
     *            集合
     * @return
     */
    public static int size(Collection<?> collection) {
        return isEmpty(collection) ? 0 : collection.size();
    }

    /**
     * 检查集合元素是否存在
     * 
     * @param collection
     *            集合
     * @return
     */
    public static boolean isAvailable(Collection<?> collection, int index) {
        if (isEmpty(collection))
            return false;
        return index >= 0 && index < collection.size();

    }

    /**
     * 检查集合元素是否为空
     * 
     * @param collection
     *            集合
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();

    }

    /**
     * 检查数组元素是否为空
     * 
     * @param array
     *            数组
     * @return
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;

    }

    /**
     * 检查Map元素是否为空
     * 
     * @param map
     *            Map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();

    }
}
