package kr.intube.apps.mockapi.common.model;

import java.math.BigDecimal;

public class DoubleRange extends MockRange {
    @Override
    public BigDecimal getDefaultMax() {
        return new BigDecimal(Double.MAX_VALUE);
    }

    @Override
    public boolean isReal() {
        return true;
    }
}
