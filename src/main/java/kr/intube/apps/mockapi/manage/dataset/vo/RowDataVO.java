package kr.intube.apps.mockapi.manage.dataset.vo;

import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class RowDataVO extends SheetVO {
    private String dataSetId;
    private int rowNo;

    private String col01;
    private String col02;
    private String col03;
    private String col04;
    private String col05;
    private String col06;
    private String col07;
    private String col08;
    private String col09;
    private String col10;

    private List<ExtraRowDataVO> extraColumns;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        return null;
    }
}
