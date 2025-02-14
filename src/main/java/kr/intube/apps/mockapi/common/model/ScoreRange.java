package kr.intube.apps.mockapi.common.model;

import aidt.gla.common.model.range.Range;

import java.math.BigDecimal;

public class ScoreRange extends Range {
    @Override
    public BigDecimal getDefaultMin() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getDefaultMax() {
        return new BigDecimal(100);
    }

    @Override
    public boolean isReal() {
        return false;
    }
}
