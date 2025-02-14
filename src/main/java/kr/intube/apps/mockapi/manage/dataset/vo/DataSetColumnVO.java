package kr.intube.apps.mockapi.manage.dataset.vo;

import aidt.gla.common.model.range.Range;
import aidt.gla.common.model.valueset.ValueSet;
import kr.intube.apps.mockapi.common.code.DataColumnType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class DataSetColumnVO extends SheetVO {
    private String dataSetId;

    private int    columnNo;
    private String columnId;
    private String columnName;
    private int    columnOrder;

    private DataColumnType columnType;

    private String fixValue;

    private String valueSetId;
    private String rangeId;

    private ValueSet<String> values;
    private Range ranges;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        return null;
    }
}
