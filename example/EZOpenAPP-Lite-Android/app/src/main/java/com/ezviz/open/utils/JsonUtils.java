package com.ezviz.open.utils;

/**
 * Created by tanyongfeng on 2016/11/4.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Json转换工具
 */
public class JsonUtils {

    private static Gson gson;

    private static Gson getGson() {
        synchronized (com.ezviz.stream.JsonUtils.class) {
            if (gson == null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.serializeSpecialFloatingPointValues();
                gson = gsonBuilder.create();
            }
        }
        return gson;
    }

    /**
     * 对象转Json字符串
     * @return Json字符串
     */
    public static String toJson(Object object) {
        return getGson().toJson(object);
    }

    /**
     * Json转为对象
     * @param json JSON
     * @param clazz 类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return getGson().fromJson(json, clazz);
    }
}

