package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;
import lombok.Getter;

@Getter
public enum HttpResponseType implements CodeEnum {
    Data          ("DATA",  "조회 API 조회 데이터",   "*", "Y"),
    Result        ("Result",  "처리 API 실행 결과",   "*", "Y"),
    ErrorParameter("ErrorParameter",  "요청 파라미터 오류",  "*", "Y"),
    ErrorKey      ("ErrorKey",  "요청 키 오류",   "*", "Y"),
    JSON          ("JSON",  "Json 결과로 바로 리턴함",   "*", "Y"),
    Break         ("Break",  "데이터 갱신으로 임시 중지","*", "Y"),
    Unknown("",  "",   "", "N"),
    ;

    public final String codeId;
    public final String codeName;
    public final String kind;
    public final String useYn;

    HttpResponseType(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static HttpResponseType getCode(String key) {
        HttpResponseType code = CodeHelper.getCode(HttpResponseType.values(), key);
        return Checker.isNull(code) ? HttpResponseType.Unknown : code;
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
