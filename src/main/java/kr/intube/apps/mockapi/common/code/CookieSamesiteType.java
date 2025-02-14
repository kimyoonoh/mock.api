package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;

public enum CookieSamesiteType implements CodeEnum {
    Strict("STRICT",  "스트릭모드",   "*", "Y"),
    Lax   ("LAX",  "",   "*", "Y"),
    None  ("NONE",  "작용안함",  "*", "Y"),
    Unknown("",  "",   "", "N"),
    ;

    public String codeId;
    public String codeName;
    public String kind;
    public String useYn;

    CookieSamesiteType(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static CookieSamesiteType getCode(String key) {
        CookieSamesiteType code = CodeHelper.getCode(CookieSamesiteType.values(), key);
        return Checker.isNull(code) ? CookieSamesiteType.Unknown : code;
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