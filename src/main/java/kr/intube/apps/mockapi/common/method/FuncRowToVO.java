package kr.intube.apps.mockapi.common.method;

import kr.intube.apps.mockapi.common.model.SheetVO;

import java.util.LinkedHashMap;

@FunctionalInterface
public interface FuncRowToVO {
    SheetVO make(LinkedHashMap<String, Object> row);
}
