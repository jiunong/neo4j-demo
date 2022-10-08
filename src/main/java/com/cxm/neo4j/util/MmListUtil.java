package com.cxm.neo4j.util;

import cn.hutool.core.collection.ListUtil;
import com.cxm.neo4j.strategy.AtPredicate;

import java.util.List;

/**
 * @author dingxuhong.sict.ac.cn
 *  萌萌
 */
public class MmListUtil {

    /**
     * @TODO 遍历集合 两两对比 返回符合预测的集合
     * @param list
     * @param predicate
     * @return
     * @param <T>
     */
    public static <T> List<T> getEffectedList(List<T> list, AtPredicate<T> predicate) {
        List<T> objects = ListUtil.list(true);
        for (int i = list.size() - 1; i >= 0; i--) {
            for (int i1 = i - 1; i1 >= 0; i1--) {
                if (predicate.isEffective(list.get(i), list.get(i1))) {
                    objects.add(list.get(i));
                    objects.add(list.get(i));
                }
            }
        }
        return objects;
    }
}
