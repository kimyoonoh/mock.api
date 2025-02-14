package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;
import lombok.Getter;

@Getter
public enum SheetName implements CodeEnum {
    API("API",  "ApiVO",   "*", "Y"),
    REQ("REQ",  "ApiReqVO",   "*", "Y"),
    RES("RES",  "ApiResVO",  "*", "Y"),
    RES_COOKIE("RES_COOKIE",  "ApiResCookieVO",   "*", "Y"),
    RES_HEAD("RES_HEAD",  "ApiResHeaderVO",   "*", "Y"),
    FILTER("FILTER",  "FilterVO","*", "Y"),
    DATASET("DATASET",  "DataSetVO","*", "Y"),
    RANGE ("RANGE",  "RangeVO","*", "Y"),
    VALUESET("VALUESET",  "ValueSetVO","*", "Y"),
    VAR("VAR",  "VariableVO","*", "Y"),
    Unknown("",  "SheetVO",   "", "N"),
    ;

    public final String codeId;
    public final String codeName;
    public final String kind;
    public final String useYn;

    SheetName(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static SheetName getCode(String key) {
        SheetName code = CodeHelper.getCode(SheetName.values(), key);
        return Checker.isNull(code) ? SheetName.Unknown : code;
    }

    public Class<?> getVOClass() {
        try {
            return Class.forName(this.getCodeName()).getClass();
        } catch (Exception e) {
            return null;
        }
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
