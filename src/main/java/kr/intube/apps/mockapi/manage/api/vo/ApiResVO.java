package kr.intube.apps.mockapi.manage.api.vo;

import kr.intube.apps.mockapi.common.code.HttpResponseType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ApiResVO extends SheetVO {
    private String apiId;
    private String responseId;
    private String responseName;
    private String profile;
    private HttpResponseType responseType;
    private Object responseValue;
    private int responseCode;
    private boolean collectionResult;
    private boolean dataSetResultType;

    private Map<String, ApiResHeaderVO> header;
    private Map<String, ApiResCookieVO> cookie;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        ApiResVO vo =new ApiResVO();

        vo.setApiId(getColumn(row, "API_ID"));
        vo.setResponseId(getColumn(row, "RES_ID"));
        vo.setResponseName(getColumn(row, "RES_NM"));
        vo.setProfile(getColumn(row, "PROFILE"));
        vo.setResponseType(HttpResponseType.getCode(getColumn(row, "RES_TYPE")));
        vo.setResponseValue(getColumn(row, "RES_VL"));
        vo.setResponseCode(getColumnInt(row, "RES_CD"));
        vo.setCollectionResult(getColumnBool(row, "COLS_YN"));
        vo.setDataSetResultType(false);

        vo.setHeader(new HashMap<>());
        vo.setCookie(new HashMap<>());

        return vo;
    }
}
