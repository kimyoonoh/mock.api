package kr.intube.apps.mockapi.manage.api.vo;

import aidt.gla.common.tools.biz.Bool;
import kr.intube.apps.mockapi.common.code.CookieSamesiteType;
import kr.intube.apps.mockapi.common.code.HttpReqParamType;
import kr.intube.apps.mockapi.common.model.SheetVO;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class ApiResCookieVO extends SheetVO {
    private String responseId;
    private String domainName;
    private String cookieId;
    private boolean httpOnly;
    private CookieSamesiteType samesite;
    private int maxAge;
    private boolean secureYn;

    @Override
    public SheetVO rowToVO(LinkedHashMap<String, Object> row) {
        ApiResCookieVO vo = new ApiResCookieVO();

        vo.setResponseId(getColumn(row, "RES_ID"));

        vo.setDomainName(getColumn(row, "DOWN_NM"));
        vo.setCookieId((getColumn(row, "COOK_ID")));
        vo.setHttpOnly(getColumnBool(row, "HTTP_ONLY_YN"));
        vo.setSamesite(CookieSamesiteType.getCode(getColumn(row, "SAME_SITE")));
        vo.setSecureYn(getColumnBool(row, "SECURE_YN"));
        vo.setMaxAge(getColumnInt(row, "MAX_AGE"));

        return vo;
    }
}
