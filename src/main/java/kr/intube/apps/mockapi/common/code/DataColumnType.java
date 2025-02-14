package kr.intube.apps.mockapi.common.code;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Bool;
import aidt.gla.common.tools.biz.Checker;
import aidt.gla.common.tools.biz.SetCalc;
import aidt.gla.common.tools.biz.StrCalc;
import aidt.gla.common.utils.DateUtil;

public enum DataColumnType implements CodeEnum {
    String       ("STR",    "String",          "String",            "Y", (t) -> { return t; }),
    Number       ("NUM",    "Number",          "Number,Int",        "Y", (t) -> { return StrCalc.getDigit(t).intValue(); }),
    Integer      ("INT",    "Integer",         "Number,Int",        "Y", (t) -> { return StrCalc.getDigit(t).intValue(); }),
    Double       ("DBL",    "Double",          "Number,Real",       "Y", (t) -> { return StrCalc.getDigit(t).doubleValue(); }),
    Date         ("DT",     "Date",            "Date",              "Y", (t) -> { return DateUtil.toDate(t); }),
    Datetime     ("DTM",    "Datetime",        "Date",              "Y", (t) -> { return t; }),
    Boolean      ("BOOL",   "Boolean",         "Boolean",           "Y", (t) -> { return Bool.bool(t); }),
    JSON         ("JSON",   "JSON",            "JSON",              "Y", (t) -> { return t; }),
    DataSet      ("DS",     "DataSet",         "*",                 "Y", (t) -> { return t; }),
    StringArray  ("ASTR",   "StringArray",     "String,ARRAY",      "Y", (t) -> { return getStringArray(t); }),
    NumberArray  ("ANUM",   "NumberArray",     "Number,Int,ARRAY",  "Y", (t) -> { return getNumberArray(t); }),
    IntegerArray ("AINT",   "IntegerArray",    "Number,Int,ARRAY",  "Y", (t) -> { return getIntegerArray(t); }),
    DoubleArray  ("ADBL",   "DoubleArray",     "Number,Real,ARRAY", "Y", (t) -> { return getDoubleArray(t); }),
    DateArray    ("ADT",    "DateArray",       "Date,ARRAY",        "Y", (t) -> { return getDateArray(t); }),
    DatetimeArray("ADTM",   "DatetimeArray",   "Date,ARRAY",        "Y", (t) -> { return getStringArray(t); }),
    BooleanArray ("ABOL",   "BooleanArray",    "Bool,ARRAY",        "Y", (t) -> { return getBoolArray(t); }),
    List         ("list",   "DataSetArray",    "ARRAY",             "Y", (t) -> { return t; }),
    Unknown      ("",       "",                 "",                 "N", (t) -> { return t; }),
    ;

    public String codeId;
    public String codeName;
    public String kind;
    public String useYn;
    public Function<String, Object> funcTransform;

    private Set<String> kindSet;

    DataColumnType(String codeId, String codeName, String kind, String useYn, Function<String, Object> funcTransform) {
        this.codeId   = codeId;
        this.codeName = codeName;
        this.kind     = kind;
        this.useYn    = useYn;

        this.funcTransform = funcTransform;
        this.kindSet  = SetCalc.toSet(kind.split(","));
    }

    public static DataColumnType getCode(String key) {
        DataColumnType code = CodeHelper.getCode(DataColumnType.values(), key);
        return Checker.isNull(code) ? DataColumnType.Unknown : code;
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

    public Set<String> getKindSet() {
        return this.kindSet;
    }

    public boolean correctKind(Set<String> kinds) {
        return SetCalc.bigset(this.kindSet, kinds);
    }

    public Object getValue(Object data) {
        return this.funcTransform.apply(java.lang.String.valueOf(data));
    }

    public static java.util.List<DataColumnType> search(String kinds) {
        java.util.List<DataColumnType> codes = new ArrayList<>();

        Set<String> kindSet = SetCalc.toSet(kinds.split(","));

        for (DataColumnType type : DataColumnType.values()) {
            if (type.correctKind(kindSet)) codes.add(type);
        }

        return codes;
    }

    public static DataColumnType [] filter(String kinds) {
        return (DataColumnType []) search(kinds).toArray();
    }

    public static java.util.List<String> getStringArray(String value) {
        java.util.List<String> result = new ArrayList<>();

        if (Checker.isBlank(value)) return result;

        for (String v : value.split(",")) {
            result.add(v);
        }

        return result;
    }

    public static java.util.List<Integer> getNumberArray(String value) {
        java.util.List<Integer> result = new ArrayList<>();

        if (Checker.isBlank(value)) return result;

        for (String v : value.split(",")) {
            result.add(new BigInteger(v).intValue());
        }

        return result;
    }

    public static java.util.List<Object> getIntegerArray(String value) {
        java.util.List<Object> result = new ArrayList<>();

        if (Checker.isBlank(value)) return result;

        for (String v : value.split(",")) {
            result.add(new BigInteger(v).intValue());
        }

        return result;
    }

    public static java.util.List<Double> getDoubleArray(String value) {
        java.util.List<Double> result = new ArrayList<>();

        if (Checker.isBlank(value)) return result;

        for (String v : value.split(",")) {
            result.add(new BigDecimal(v).doubleValue());
        }

        return result;
    }

    public static java.util.List<Boolean> getBoolArray(String value) {
        java.util.List<Boolean> result = new ArrayList<>();

        if (Checker.isBlank(value)) return result;

        for (String v : value.split(",")) {
            result.add(Bool.bool(v));
        }

        return result;
    }

    public static java.util.List<java.util.Date> getDateArray(String value) {
        java.util.List<java.util.Date> result = new ArrayList<>();

        if (Checker.isBlank(value)) return result;

        for (String v : value.split(",")) {
            result.add(DateUtil.toDate(v));
        }

        return result;
    }
}
