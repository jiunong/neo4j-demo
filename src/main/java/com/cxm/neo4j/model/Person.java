package com.cxm.neo4j.model;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@Builder
@Node(primaryLabel = "person")
public class Person {
    @ExcelProperty("姓名")
    @Property("name")
    private String name;


    @Id
    @ExcelProperty("身份证号")
    private  String idCard;

    @Property("sex")
    @ExcelProperty("性别")
    private String sex;

    @Property("familyName")
    @ExcelProperty("姓")
    private String familyName;


}
