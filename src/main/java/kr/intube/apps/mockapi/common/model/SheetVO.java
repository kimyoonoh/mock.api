package kr.intube.apps.mockapi.common.model;

import aidt.gla.common.tools.biz.Bool;
import aidt.gla.common.tools.biz.Checker;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public abstract class SheetVO {
    protected String getColumn(LinkedHashMap<String, Object> row, String key) {
        String lowerKey = key.toLowerCase();
        String upperKey = lowerKey.toUpperCase();

        if (Checker.isNull(row)) return "";

        if (row.containsKey(key))      return Checker.isNull(row.get(key)) ? "" : row.get(key).toString();
        if (row.containsKey(lowerKey)) return Checker.isNull(row.get(lowerKey)) ? "" : row.get(lowerKey).toString();
        if (row.containsKey(upperKey)) return Checker.isNull(row.get(upperKey)) ? "" : row.get(upperKey).toString();

        return "";
    }

    protected Integer getColumnInt(LinkedHashMap<String, Object> row, String key) {
        String value = getColumn(row, key);

        if (Checker.isBlank(value)) return -1;

        return new BigDecimal(value).intValue();
    }

    protected BigDecimal getColumnDecimal(LinkedHashMap<String, Object> row, String key) {
        String value = getColumn(row, key);

        if (Checker.isBlank(value)) return new BigDecimal(-1);

        return new BigDecimal(value);
    }

    protected boolean getColumnBool(LinkedHashMap<String, Object> row, String key) {
        String value = getColumn(row, key);

        if (Checker.isBlank(value)) return false;

        return Bool.bool(value);
    }

    protected String [] getColumnArray(LinkedHashMap<String, Object> row, String key) {
        String value = getColumn(row, key);

        if (Checker.isBlank(value)) {
            return new String[0];
        } else {
            return value.split(",");
        }
    }

    abstract public SheetVO rowToVO(LinkedHashMap<String, Object> row);
}
