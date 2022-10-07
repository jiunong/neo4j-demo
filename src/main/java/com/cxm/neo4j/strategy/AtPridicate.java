package com.cxm.neo4j.strategy;

public interface AtPridicate<T> {

    boolean isEffective(T t1,T t2);

}
