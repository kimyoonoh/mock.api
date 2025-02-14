package kr.intube.apps.mockapi.manage.api.vo;

import aidt.gla.common.tools.biz.Bool;
import kr.intube.apps.mockapi.common.code.HttpMethod;
import kr.intube.apps.mockapi.common.code.HttpProtocol;
import kr.intube.apps.mockapi.common.code.HttpReqParamType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class ApiReqVO extends SheetVO {
    private String apiId;
    private String parameterId;
    private HttpReqParamType paramType;
    private boolean requireYn;
    private String defaultValue;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        ApiReqVO vo = new ApiReqVO();

        vo.setApiId(getColumn(row, "API_ID"));
        vo.setParameterId(getColumn(row, "PRAM_ID"));
        vo.setParamType(HttpReqParamType.getCode(getColumn(row, "PRAM_TYPE")));
        vo.setRequireYn(getColumnBool(row, "REQUIRE_YN"));
        vo.setDefaultValue(getColumn(row, "DEFAULT_YN"));

        return vo;
    }
}
