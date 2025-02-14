package kr.intube.apps.mockapi.common.model;

import aidt.gla.common.model.range.ProbableRange;
import aidt.gla.common.model.range.Range;

import java.math.BigDecimal;

public class MockRange extends Range {
    double [] ratios;

    ProbableRange prob;

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

    public BigDecimal gen() {
        int rvalue = prob.sample().intValue();
        int index  = prob.index(rvalue);

        return sample(index);
    }

    public void setRatio(double [] ratios) {
        this.ratios = ratios;

        this.prob = new ProbableRange(ratios);
    }
}
