package kr.intube.apps.mockapi.manage.dataset.vo;

import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class ExtraRowDataVO extends SheetVO {
    private String dataSetId;
    private int    rowNo;
    private String value;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        return null;
    }
}