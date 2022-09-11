package com.cxm.neo4j.util;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.excel.util.ListUtils;
import com.cxm.neo4j.model.Cities;
import com.cxm.neo4j.model.Person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GeneratePersonInfoCsv {

    private final static List<String> xing = ListUtils.newArrayList("赵", "钱", "孙", "李");
    private final static List<String> ming = ListUtils.newArrayList("一", "二", "三", "四");


    public static List<Person> getPersons(int num) {
        List<Person> personList = ListUtil.list(false);
        RandInfo randInfo = new RandInfo();
        for (int i = 0; i < num; i++) {
            String[] nameAndSex = randInfo.getNameAndSex(i % 2 == 1 ? "男" : "女");
            personList.add(Person.builder().idCard(getIdNo(i % 2 == 1))
                    .name(randInfo.getFamilyName().concat(nameAndSex[0]))
                    .sex(nameAndSex[1]).build());
        }
        return personList;
    }

    public static String getIdNo(boolean male) {
        //随机生成生日 1~99岁
        long begin = System.currentTimeMillis() - 3153600000000L;//100年内
        long end = System.currentTimeMillis() - 31536000000L; //1年内
        long rtn = begin + (long) (Math.random() * (end - begin));
        Date date = new Date(rtn);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String birth = simpleDateFormat.format(date);
        return getIdNo(birth, male);
    }

    public static String getIdNo(String birth, boolean male) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int value = random.nextInt(Cities.cities.length);
        sb.append(Cities.cities[value]);
        sb.append(birth);
        value = random.nextInt(999) + 1;
        if (male && value % 2 == 0) {
            value++;
        }
        if (!male && value % 2 == 1) {
            value++;
        }
        if (value >= 100) {
            sb.append(value);
        } else if (value >= 10) {
            sb.append('0').append(value);
        } else {
            sb.append("00").append(value);
        }
        sb.append(calcTrailingNumber(sb));
        return sb.toString();
    }

    private static final int[] calcC = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] calcR = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private static char calcTrailingNumber(StringBuilder sb) {
        int[] n = new int[17];
        int result = 0;
        for (int i = 0; i < n.length; i++) {
            n[i] = Integer.parseInt(String.valueOf(sb.charAt(i)));
        }
        for (int i = 0; i < n.length; i++) {
            result += calcC[i] * n[i];
        }
        return calcR[result % 11];
    }


}
