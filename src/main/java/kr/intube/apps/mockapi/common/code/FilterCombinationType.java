package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;

public enum FilterCombinationType implements CodeEnum {
    And("AND",   "AND",   "*", "Y"),
    Or ("OR",   "OR",   "*", "Y"),
    Unknown("",  "",   "", "N"),
    ;

    public String codeId;
    public String codeName;
    public String kind;
    public String useYn;

    FilterCombinationType(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static FilterCombinationType getCode(String key) {
        FilterCombinationType code = CodeHelper.getCode(FilterCombinationType.values(), key);
        return Checker.isNull(code) ? FilterCombinationType.Unknown : code;
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