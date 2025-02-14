package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;
import lombok.Getter;

@Getter
public enum HttpProtocol implements CodeEnum {
    Http ("HTTP", "HTTP", "*", "Y"),
    Https("HTTPS", "HTTPS", "*", "Y"),
    Unknown("",  "",   "", "N"),
    ;

    public final String codeId;
    public final String codeName;
    public final String kind;
    public final String useYn;

    HttpProtocol(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static HttpProtocol getCode(String key) {
        HttpProtocol code = CodeHelper.getCode(HttpProtocol.values(), key);
        return Checker.isNull(code) ? HttpProtocol.Unknown : code;
    }

    @Override
    public HttpProtocol getCode() {
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
