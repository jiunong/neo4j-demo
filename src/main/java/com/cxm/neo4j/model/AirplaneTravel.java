package com.cxm.neo4j.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * TODO
 *
 * @author xuhong.ding
 * @since 2022/9/24 16:11
 */
@Data
@Builder
public class AirplaneTravel {

    @ExcelProperty("index")
    private int index ;
    @ExcelProperty("相同")
    private String compareKey;
    @ExcelProperty("不同")
    private String compareValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AirplaneTravel)) return false;
        AirplaneTravel that = (AirplaneTravel) o;
        return Objects.equals(compareKey, that.compareKey) && Objects.equals(compareValue, that.compareValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compareKey, compareValue);
    }
}
