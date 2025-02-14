package kr.intube.apps.mockapi.common.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IntegerRange extends MockRange {
    @Override
    public BigDecimal getDefaultMax() {
        return new BigDecimal(Integer.MAX_VALUE);
    }
}