package kr.intube.apps.mockapi.common.model;

import aidt.gla.common.model.range.ProbableRange;
import aidt.gla.common.model.range.Range;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IntegerRange extends MockRange {


    @Override
    public BigDecimal getDefaultMin() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getDefaultMax() {
        return new BigDecimal(Integer.MAX_VALUE);
    }

    @Override
    public boolean isReal() {
        return false;
    }
}