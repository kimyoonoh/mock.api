package kr.intube.apps.mockapi.manage.api.vo;

import aidt.gla.common.tools.biz.Bool;
import kr.intube.apps.mockapi.common.code.HttpMethod;
import kr.intube.apps.mockapi.common.code.HttpProtocol;
import kr.intube.apps.mockapi.common.code.HttpResponseType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class ApiResHeaderVO extends SheetVO {
    private String responseId;
    private String headerId;
    private String headerName;
    private String headerValue;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        ApiResHeaderVO vo = new ApiResHeaderVO();

        vo.setResponseId(getColumn(row, "RES_ID"));
        vo.setHeaderId(getColumn(row, "HEAD_ID"));
        vo.setHeaderName(getColumn(row, "HEAD_NM"));
        vo.setHeaderValue(getColumn(row, "HEAD_VL"));

        return vo;
    }
}
