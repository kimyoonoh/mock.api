package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;
import lombok.Getter;

@Getter
public enum HttpReqParamType implements CodeEnum {
    Query ("QUERY",  "Request Query String Parameter",   "*", "Y"),
    Path  ("PATH",  "Request Url Path Variable",  "*", "Y"),
    Body  ("BODY",  "Request Body",   "*", "Y"),
    Header("HEADER",  "Request Header","*", "Y"),
    Cookie("COOKIE",  "Request Cookie","*","Y"),
    Unknown("",  "",   "", "N"),
    ;

    public final String codeId;
    public final String codeName;
    public final String kind;
    public final String useYn;

    HttpReqParamType(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static HttpReqParamType getCode(String key) {
        HttpReqParamType code = CodeHelper.getCode(HttpReqParamType.values(), key);
        return Checker.isNull(code) ? HttpReqParamType.Unknown : code;
    }

    @Override
    public CodeEnum getCode() {
        return this;
    }

    public String getCodeId() {
        return this.codeId;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public String getKind() {
        return this.kind;
    }

    public String getUseYn() {
        return this.useYn;
    }
}
