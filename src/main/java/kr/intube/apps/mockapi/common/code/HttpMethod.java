package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

@Getter
public enum HttpMethod implements CodeEnum {
    Get   ("GET",  "Get",   "GET", "Y"),
    Post  ("POST", "Post",  "POST", "Y"),
    Put   ("PUT",  "Put",   "PUT", "Y"),
    Delete("DEL",  "Delete","DEL", "Y"),
    All   ("ALL",  "All",   "*,all", "Y"),
    Unknown("",  "",   "", "N"),
    ;

    public final String codeId;
    public final String codeName;
    public final String kind;
    public final String useYn;

    HttpMethod(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static HttpMethod getCode(String key) {
        HttpMethod code = CodeHelper.getCode(HttpMethod.values(), key);
        return Checker.isNull(code) ? HttpMethod.Unknown : code;
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

    public RequestMethod getMethod() {
        if (this == All) return RequestMethod.GET;

        return RequestMethod.valueOf(this.codeId);
    }

    public RequestMethod [] getMethods() {
        if (this == All) {
            return new RequestMethod [] { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE };
        }

        return new RequestMethod [] { RequestMethod.valueOf(this.codeId) };
    }
}
