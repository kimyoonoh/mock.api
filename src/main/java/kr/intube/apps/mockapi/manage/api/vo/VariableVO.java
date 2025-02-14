package kr.intube.apps.mockapi.manage.api.vo;

import kr.intube.apps.mockapi.common.code.HttpMethod;
import kr.intube.apps.mockapi.common.code.HttpProtocol;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class VariableVO extends SheetVO {
    private String       id;
    private String       value;

    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        VariableVO vo = new VariableVO();

        vo.setId   (getColumn(row, "VAR_ID"));
        vo.setValue(getColumn(row, "VAR_VL"));

        return vo;
    }
}
