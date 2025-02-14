package kr.intube.apps.mockapi.manage.api.vo;

import kr.intube.apps.mockapi.common.code.HttpMethod;
import kr.intube.apps.mockapi.common.code.HttpProtocol;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

@Getter
@Setter
public class ApiVO extends SheetVO {
    private String       apiId;
    private String       apiName;
    private String       systemId;
    private HttpProtocol protocol;
    private HttpMethod   method;
    private String       endPoint;
    private String       contextPath;
    private boolean      useYn;

    private Map<String, ApiReqVO> request;
    private Map<String, ApiResVO> response;

    private ApiResVO resData;
    private ApiResVO resErrorParameter;
    private ApiResVO resErrorKey;

    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        ApiVO vo = new ApiVO();

        vo.setApiId   (getColumn(row, "API_ID"));
        vo.setApiName (getColumn(row, "API_NM"));
        vo.setSystemId(getColumn(row, "SYS_ID"));
        vo.setProtocol(HttpProtocol.getCode(getColumn(row, "PROTOCOL")));
        vo.setMethod(HttpMethod.getCode(getColumn(row, "METHOD")));
        vo.setEndPoint(getColumn(row, "END_PNT"));
        vo.setContextPath(getColumn(row, "CONTEXT_PATH"));
        vo.setUseYn(getColumnBool(row, "USE_YN"));

        vo.setRequest(new HashMap<>());
        vo.setResponse(new HashMap<>());

        return vo;
    }
}
