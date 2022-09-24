package com.cxm.neo4j.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONObject;

import java.util.List;

public class Neo4jUtil {


    public static void main(String[] args) {

    }
    public static List<JSONObject> ObjectToJsonList(Object o) {
        List<JSONObject> list = ListUtil.list(false);
        if (o == null) {

        } else if (o instanceof JSONObject) {
            list.add((JSONObject) o);
        } else {
            list.addAll((List<JSONObject>) o);
        }
        return list;
    }

}
