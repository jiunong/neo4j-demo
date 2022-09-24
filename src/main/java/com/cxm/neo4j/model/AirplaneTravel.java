package com.cxm.neo4j.model;

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

    private String compareKey;
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
