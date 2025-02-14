package kr.intube.apps.mockapi.manage.dataset.vo;

import aidt.gla.common.tools.biz.Bool;
import kr.intube.apps.mockapi.common.code.CookieSamesiteType;
import kr.intube.apps.mockapi.common.code.DataColumnType;
import kr.intube.apps.mockapi.common.code.DataGenType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataSetVO extends SheetVO {
    private String  dataSetId;
    private String  columnId;
    private String  mappingId;
    private String  columnName;
    private DataColumnType dataType;
    private DataGenType genType;
    private Object  defaultValue;

    private List<Map<String, SheetVO>> rows;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        DataSetVO vo = new DataSetVO();

        vo.setDataSetId(getColumn(row, "DATASET_ID"));

        vo.setColumnId(getColumn(row, "COLUMN_ID"));
        vo.setMappingId(getColumn(row, "MAPP_ID"));
        vo.setColumnName(getColumn(row, "COLUMN_NM"));

        vo.setDataType(DataColumnType.getCode(getColumn(row, "DATA_TYPE")));
        vo.setGenType(DataGenType.getCode(getColumn(row, "GEN_TYPE")));

        vo.setDefaultValue(getColumn(row, "DEFAULT"));

        vo.setRows(new ArrayList<>());

        return vo;
    }
}
