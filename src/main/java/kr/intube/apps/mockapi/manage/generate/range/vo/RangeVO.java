package kr.intube.apps.mockapi.manage.generate.range.vo;

import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class RangeVO extends SheetVO {
    private String rangeId;
    private String rangeName;

    private BigDecimal minValue;
    private BigDecimal maxValue;

    private int step;
    private BigDecimal gap;

    private List<BigDecimal> valueList;
    private double [] ratioList;

    private boolean real;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        RangeVO vo = new RangeVO();

        vo.setRangeId(getColumn(row, "RANGE_ID"));
        vo.setRangeName(getColumn(row, "RANGE_NM"));

        vo.setMinValue(getColumnDecimal(row, "MIN_VL"));
        vo.setMaxValue(getColumnDecimal(row, "MAX_VL"));

        vo.setStep(getColumnInt(row, "STEP"));
        vo.setGap(getColumnDecimal(row, "GAP"));

        String [] vl = getColumnArray(row, "VL_LST");
        vo.valueList = new ArrayList<>();
        for (String v : vl) {
            vo.valueList.add(new BigDecimal(v));
        }

        String [] rl = getColumnArray(row, "ORCN_RT_LST");
        vo.ratioList = new double[rl.length];
        for (int i = 0; i < rl.length; i++) {
            vo.ratioList[i] = Double.parseDouble(rl[i]);
        }

        vo.setReal(getColumnBool(row, "IS_REAL"));

        return vo;
    }
}