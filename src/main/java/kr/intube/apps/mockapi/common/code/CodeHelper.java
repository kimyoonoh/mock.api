package kr.intube.apps.mockapi.common.code;

import aidt.gla.common.code.CodeEnum;
import aidt.gla.common.tools.biz.Checker;

public class CodeHelper {
    public static <T extends CodeEnum> T getCode(CodeEnum [] codes, String key) {
        for (CodeEnum code : codes) {
            if (Checker.eq(code.getCodeId().toLowerCase(), key.toLowerCase()))   return (T) code;
            if (Checker.eq(code.getCodeName().toLowerCase(), key.toLowerCase())) return (T) code;
        }

        return null;
    }
}
