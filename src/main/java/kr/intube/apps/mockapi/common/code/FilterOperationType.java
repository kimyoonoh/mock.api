package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;

public enum FilterOperationType implements CodeEnum {
    Equal      ("EQ",   "같다",   "*", "Y"),
    NotEqual   ("NEQ",   "같지않다.",   "*", "Y"),
    LessThan   ("LT",   "작다",   "*", "Y"),
    LessEqual  ("LE",   "작거나같다",   "*", "Y"),
    GreateThan ("GT",   "크다",   "*", "Y"),
    GreateEqual("GE",   "크거나같다",   "*", "Y"),
    In         ("IN",   "포함된다.",   "*", "Y"),
    NotIn      ("NIN",  "포함되지않는다.",   "*", "Y"),
    Unknown    ("",  ".",   "*", "Y")
    ;

    public String codeId;
    public String codeName;
    public String kind;
    public String useYn;

    FilterOperationType(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static FilterOperationType getCode(String key) {
        FilterOperationType code = CodeHelper.getCode(FilterOperationType.values(), key);
        return Checker.isNull(code) ? FilterOperationType.Unknown : code;
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
