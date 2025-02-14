package kr.intube.apps.mockapi.common.model;

import aidt.gla.common.model.range.Range;

import java.math.BigDecimal;

public class DoubleRange extends MockRange {
    @Override
    public BigDecimal getDefaultMin() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getDefaultMax() {
        return new BigDecimal(Double.MAX_VALUE);
    }

    @Override
    public boolean isReal() {
        return true;
    }
}
