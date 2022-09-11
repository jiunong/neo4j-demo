package com.cxm.neo4j.model;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("身份证号")
    @NumberFormat()
    private  String idCard;

    @ExcelProperty("性别")
    private String sex;


}
