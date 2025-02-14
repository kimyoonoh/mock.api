package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;
import lombok.Getter;

@Getter
public enum DataGenType implements CodeEnum {
    Literal  ("LITERAL",   "즉치",   "*", "Y"),
    Range    ("RANGE",   "범위",   "*", "Y"),
    ValueSet ("VALUESET",   "값집합",   "*", "Y"),
    Calculate("CALCULATE",   "계산",   "*", "Y"),
    Variable ("VARIABLE",   "변수",   "*", "Y"),
    DataSet  ("DATASET",   "변수",   "*", "Y"),
    Parameter("Parameter",   "변수",   "*", "Y"),
    Unknown  ("",  "",   "", "N"),
            ;

    public final String codeId;
    public final String codeName;
    public final String kind;
    public final String useYn;

    DataGenType(String codeId, String codeName, String kind, String useYn) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;
    }

    public static DataGenType getCode(String key) {
        DataGenType code = CodeHelper.getCode(DataGenType.values(), key);
        return Checker.isNull(code) ? DataGenType.Unknown : code;
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
