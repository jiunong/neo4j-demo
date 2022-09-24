package com.cxm.neo4j;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cxm.neo4j.database.Neo4jQuery;
import com.cxm.neo4j.model.AirplaneTravel;
import com.cxm.neo4j.util.Neo4jUtil;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author
 * @since
 */
@SpringBootTest
public class Neo4AirTest {

    @Test
    void test1() {
        Neo4jQuery.query("MATCH (n:飞机出行)" +
                        "RETURN n.承运航空公司代码 as num," +
                        "n.姓名 as name," +
                        "n.航班号 as number," +
                        "n.时间 as time," +
                        "n.登机机场 as airplane," +
                        "n.地点 as place," +
                        "n.到达机场 as arrival," +
                        "n.起始站 as begin"
                , u -> {
                    while (u.getResultSet().hasNext()) {
                        String num = u.getResultSet().next().get("num").asString();
                        String name = u.getResultSet().next().get("name").asString();
                        String number = u.getResultSet().next().get("number").asString();
                        String time = u.getResultSet().next().get("time").asString();
                        String airplane = u.getResultSet().next().get("airplane").asString();
                        String place = u.getResultSet().next().get("place").asString();
                        String arrival = u.getResultSet().next().get("arrival").asString();
                        String begin = u.getResultSet().next().get("begin").asString();
                        //创建字符串类型list
                        List<String> list = Arrays.asList("承运航空公司代码：" + num + " 姓名:" + name +
                                " 航班号：" + number + " 时间：" + time +
                                " 登机机场：" + airplane + " 地点：" + place +
                                " 到达机场：" + arrival + " 起始站：" + begin);
                        for (int i = 0; i < list.size(); i++) {
                            System.out.println(list.get(i));
                        }
                    }
                });
    }


    @Test
    void test2() {
        List<LinkedHashMap<String, Object>> list = ListUtil.list(false);
        Neo4jQuery.query("MATCH (n:飞机出行)  return n order by n.姓名 desc,n.时间 asc", u -> {
            while (u.getResultSet().hasNext()) {
                Record next = u.getResultSet().next();
                List<Value> values = next.values();
                LinkedHashMap map = new LinkedHashMap();
                values.forEach(value -> {
                    Node node = value.asNode();
                    Iterator<String> labels = node.labels().iterator();
                    String nodeType = labels.next();
                    Iterator<String> keys = node.keys().iterator();
                    while (keys.hasNext()) {
                        String attrKey = keys.next();
                        String attr = node.get(attrKey).asString();
                        map.put(attrKey, attr);
                    }
                    list.add(map);
                });
            }
        });
        test4(list, "时间");
        //test4(list, "时间", "地点", "起始站");
    }


    /**
     * TODO
     *
     * @param *             @param list :
     * @param excludeLabels :  允许不一样的标签
     * @return void
     * @author xuhong.ding
     * @since 2022/9/24 16:52
     **/

    void test4(List<LinkedHashMap<String, Object>> list, String... excludeLabels) {
        LinkedHashSet res = new LinkedHashSet();
        List<String> labelList = ListUtil.of(excludeLabels);
        List<AirplaneTravel> compareList = ListUtil.list(false);
        list.forEach(u -> {
            StringBuilder compareKey = new StringBuilder();
            StringBuilder compareValue = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = u.entrySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().getKey();
                if (!labelList.contains(key)) {
                    compareKey.append(key+"="+u.get(key));
                } else {
                    compareValue.append(key+"="+u.get(key));
                }
            }
            AirplaneTravel build = AirplaneTravel.builder().compareKey(compareKey.toString()).compareValue(compareValue.toString()).build();
            List<String> compareKeyList = compareList.stream().map(AirplaneTravel::getCompareKey).collect(Collectors.toList());
            if (!compareKeyList.contains(compareKey.toString())) {
                compareList.add(build);
            } else {
                AirplaneTravel airplaneTravel = compareList.stream().filter(m -> m.getCompareKey().equals(compareKey.toString())).findFirst().get();
                res.add(airplaneTravel.toString());
                res.add(build.toString());
            }
        });

        res.stream().forEach(System.out::println);
    }

    @Test
    void loadJson() {
        String records = FileUtil.readUtf8String("C:\\Users\\Administrator\\Desktop\\records.json");
        JSONArray values = JSONUtil.parseArray(records);
        values.stream().map(JSONObject::new).forEach(u -> {
            JSONObject n = Neo4jUtil.ObjectToJsonList(u.get("n")).get(0);
            List<JSONObject> labels = Neo4jUtil.ObjectToJsonList(n.get("labels"));
            List<JSONObject> properties = Neo4jUtil.ObjectToJsonList(n.get("properties"));
            JSONObject entries = properties.get(0);
            Iterator<Map.Entry<String, Object>> iterator = entries.entrySet().iterator();
            String sql = "CREATE (n:飞机出行{承运航空公司代码:'" + entries.get("承运航空公司代码") + "'" +
                    ",姓名:'" + entries.get("姓名") + "'" +
                    ",时间:'" + entries.get("时间") + "'" +
                    ",登机机场:'" + entries.get("登机机场") + "'" +
                    ",地点:'" + entries.get("地点") + "'" +
                    ",航班号:'" + entries.get("航班号") + "'" +
                    ",name:'" + entries.get("name") + "'" +
                    ",到达机场:'" + entries.get("到达机场") + "'" +
                    ",起始站:'" + entries.get("起始站") + "'" +
                    ",身份证号:'" + entries.get("身份证号") + "'" +
                    ",Name:'" + entries.get("Name") + "'})";
            Neo4jQuery.write(sql, u2 -> {
            });
        });
    }

}
