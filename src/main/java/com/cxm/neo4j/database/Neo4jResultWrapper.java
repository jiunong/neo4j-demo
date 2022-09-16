package com.cxm.neo4j.database;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;

public class Neo4jResultWrapper {
    private Result result;

    Neo4jResultWrapper(Result result) {
        this.result = result;
    }

    public Result getResultSet() {
        return result;
    }


}
