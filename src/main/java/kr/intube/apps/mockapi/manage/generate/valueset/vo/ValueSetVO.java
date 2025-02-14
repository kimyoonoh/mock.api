package kr.intube.apps.mockapi.manage.generate.valueset.vo;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Bool;
import aidt.gla.common.tools.biz.Checker;
import kr.intube.apps.mockapi.common.code.DataColumnType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Getter
@Setter
public class ValueSetVO extends SheetVO {
    private String valueSetId;
    private String valueSetName;

    private DataColumnType valueSetType;

    private CodeEnum code;

    private String [] valueList;
    private double [] ratioList;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        ValueSetVO vo = new ValueSetVO();

        vo.setValueSetId(getColumn(row, "VLSET_ID"));
        vo.setValueSetName(getColumn(row, "VLSET_NM"));

        vo.setValueSetType(DataColumnType.getCode(getColumn(row, "VLSET_TYPE")));

        vo.setValueList(getColumnArray(row, "VL_LST"));

        String [] rl = getColumnArray(row, "ORCN_RT_LST");
        vo.ratioList = new double[rl.length];
        for (int i = 0; i < rl.length; i++) {
            vo.ratioList[i] = Double.parseDouble(rl[i]);
        }

        return vo;
    }
}
