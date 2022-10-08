package com.cxm.neo4j;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.cxm.neo4j.database.Neo4jQuery;
import com.cxm.neo4j.model.AirplaneTravel;
import com.cxm.neo4j.util.MmListUtil;
import com.cxm.neo4j.util.Neo4jUtil;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author
 * @since
 */
@SpringBootTest
public class Neo4AirTest {

    private static final long MINUTE = 60 * 1000;
    private static final long HOUR = 60 * 60 * 1000;
    private static final long DAY = 24 * 60 * 60 * 1000;

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
        //test4(list, "时间");
        //test4(list, "时间", "地点", "起始站");
        test5(list, "时间", 30 * MINUTE);
    }


    /**
     * TODO
     *
     * @param list          : 数据集合map
     * @param excludeLabels :  允许不一样的属性
     * @return void
     * @author xuhong.ding
     * @since 2022/9/24 16:52
     **/
    void test4(List<LinkedHashMap<String, Object>> list, String... excludeLabels) {
        AtomicInteger index = new AtomicInteger();
        //存放待打印的符合条件的数据
        LinkedHashSet<String> res = new LinkedHashSet();
        LinkedHashSet<AirplaneTravel> aps = new LinkedHashSet();
        // 不一样的属性集合
        List<String> labelList = ListUtil.of(excludeLabels);
        //存放数据结果
        List<AirplaneTravel> compareList = ListUtil.list(false);
        /*
            循环集合
            重新处理每个数据对象
            属性在labelList里的拼接一个字符串compareValue
                不在labelList里的拼接字符串compareKey
            当compareKey重复则将重复的两条数据放入res
         */
        list.forEach(u -> {
            StringBuilder compareKey = new StringBuilder();
            StringBuilder compareValue = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = u.entrySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().getKey();
                if (!labelList.contains(key)) {
                    compareKey.append(key + "=" + u.get(key));
                } else {
                    compareValue.append(key + "=" + u.get(key));
                }
            }
            AirplaneTravel build = AirplaneTravel.builder().compareKey(compareKey.toString()).compareValue(compareValue.toString()).build();
            List<String> compareKeyList = compareList.stream().map(AirplaneTravel::getCompareKey).collect(Collectors.toList());
            //当compareKey重复则将重复的两条数据放入res 否则放入compareList
            if (!compareKeyList.contains(compareKey.toString())) {
                build.setIndex(index.getAndIncrement());
                compareList.add(build);
            } else {
                AirplaneTravel airplaneTravel = compareList.stream().filter(m -> m.getCompareKey().equals(compareKey.toString())).findFirst().get();
                build.setIndex(airplaneTravel.getIndex());
                //res.add(airplaneTravel.toString());
                //res.add(build.toString());
                aps.add(airplaneTravel);
                aps.add(build);
            }
        });
        //res.stream().forEach(System.out::println);
        EasyExcel.write("/Users/mac/Downloads/result" + DateUtil.now() + ".csv", AirplaneTravel.class).sheet("模板").doWrite(aps.stream().sorted(Comparator.comparing(AirplaneTravel::getIndex)).collect(Collectors.toList()));

    }

    void test5(List<LinkedHashMap<String, Object>> list, String excludeLabel, long timeRange) {
        AtomicInteger index = new AtomicInteger();
        //存放待打印的符合条件的数据
        LinkedHashSet<AirplaneTravel> res = new LinkedHashSet();
        LinkedHashSet<AirplaneTravel> aps = new LinkedHashSet();
        // 不一样的属性集合
        List<String> labelList = ListUtil.of(excludeLabel);
        //存放数据结果
        List<AirplaneTravel> compareList = ListUtil.list(false);
        /*
            循环集合
            重新处理每个数据对象
            属性在labelList里的拼接一个字符串compareValue
                不在labelList里的拼接字符串compareKey
            当compareKey重复则将重复的两条数据放入res
         */
        list.forEach(u -> {
            StringBuilder compareKey = new StringBuilder();
            StringBuilder compareValue = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = u.entrySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().getKey();
                if (!labelList.contains(key)) {
                    compareKey.append(key + "=" + u.get(key));
                } else {
                    compareValue.append(u.get(key));
                }
            }
            AirplaneTravel build = AirplaneTravel.builder().compareKey(compareKey.toString()).compareValue(compareValue.toString()).build();
            List<String> compareKeyList = compareList.stream().map(AirplaneTravel::getCompareKey).collect(Collectors.toList());
            //当compareKey重复则将重复的两条数据放入res 否则放入compareList
            if (!compareKeyList.contains(compareKey.toString())) {
                build.setIndex(index.getAndIncrement());
                compareList.add(build);
            } else {
                AirplaneTravel airplaneTravel = compareList.stream().filter(m -> m.getCompareKey().equals(compareKey.toString())).findFirst().get();
                build.setIndex(airplaneTravel.getIndex());
                //res.add(airplaneTravel.toString());
                //res.add(build.toString());
                aps.add(airplaneTravel);
                aps.add(build);
            }
        });
        Map<Integer, List<AirplaneTravel>> apsMap = aps.stream().collect(Collectors.groupingBy(AirplaneTravel::getIndex));
        apsMap.keySet().forEach(i -> {
            List<AirplaneTravel> compareData = apsMap.get(i);
            List<AirplaneTravel> effectedList = MmListUtil.getEffectedList(compareData, (AirplaneTravel u1, AirplaneTravel u2) -> {
                long d1 = DateUtil.parse(u1.getCompareValue(), DatePattern.NORM_DATETIME_FORMAT).getTime();
                long d2 = DateUtil.parse(u2.getCompareValue(), DatePattern.NORM_DATETIME_FORMAT).getTime();
                return Math.abs(d1 - d2) <= timeRange;
            });
            res.addAll(effectedList);
           /* for (int i1 = compareData.size() - 1; i1 >= 0; i1--) {
                for (int i2 = i1 - 1; i2 >= 0; i2--) {
                    long d1 = DateUtil.parse(compareData.get(i1).getCompareValue(), DatePattern.NORM_DATETIME_FORMAT).getTime();
                    long d2 = DateUtil.parse(compareData.get(i2).getCompareValue(), DatePattern.NORM_DATETIME_FORMAT).getTime();
                    if (Math.abs(d1 - d2) <= timeRange) {
                        res.add(compareData.get(i1));
                        res.add(compareData.get(i2));
                    }
                }
            }*/
        });
        EasyExcel.write("/Users/mac/Downloads/result" + DateUtil.now() + ".csv", AirplaneTravel.class).sheet(excludeLabel).doWrite(res.stream().sorted(Comparator.comparing(AirplaneTravel::getIndex)).collect(Collectors.toList()));
    }


    @Test
    void loadJson() {
        String records = FileUtil.readUtf8String("/Users/mac/Downloads/records.json");
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
