package kr.intube.apps.mockapi.manage.filter.vo;

import kr.intube.apps.mockapi.common.code.FilterCombinationType;
import kr.intube.apps.mockapi.common.code.FilterOperationType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class FilterVO extends SheetVO {
    private String apiId;
    private String filerId;
    private String parameterId;
    private String dataSetId;
    private FilterOperationType operationType;
    private String compareValue;
    private FilterCombinationType combinationType;
    private int order;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        FilterVO vo = new FilterVO();

        vo.setApiId(getColumn(row, "API_ID"));
        vo.setFilerId(getColumn(row, "FILTER_ID"));

        vo.setParameterId(getColumn(row, "PRAM_ID"));
        vo.setDataSetId((getColumn(row, "DATASET_ID")));
        vo.setOperationType(FilterOperationType.getCode(getColumn(row, "OPER")));
        vo.setCombinationType(FilterCombinationType.getCode(getColumn(row, "COMB_METHOD")));
        vo.setOrder(getColumnInt(row, "ORDER"));

        return vo;
    }
}
