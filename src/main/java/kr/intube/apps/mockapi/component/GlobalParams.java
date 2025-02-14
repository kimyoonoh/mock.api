package kr.intube.apps.mockapi.component;

import aidt.gla.common.model.map.GenericMap;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("global")
public class GlobalParams {
    private GenericMap params;

    public static GlobalParams _inst = null;

    private GlobalParams() {
        params = new GenericMap();
    }

    public static GlobalParams getParams() {
        if (_inst == null) _inst = new GlobalParams();

        return _inst;
    }

    @SuppressWarnings("unchecked")
    public static Set<String> keySet() {
        return getParams().params.keySet();
    }

    @SuppressWarnings("unchecked")
    public static <T> T put(String key, T value) {
        return (T) getParams().params.put(key, value);
    }

    public static <T> T get(String key) {
        return get(key, null);
    }

    public static <T> T get(String key, T defaultValue) {
        return (T) getParams().params.get(key, defaultValue);
    }

    public static void clear() {
        getParams().params.clear();
    }
}
