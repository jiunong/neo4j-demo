package com.cxm.neo4j.strategy;

@FunctionalInterface
public interface AtPredicate<T>  {

    boolean isEffective(T t1,T t2);

}
