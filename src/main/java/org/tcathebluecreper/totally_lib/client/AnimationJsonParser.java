package org.tcathebluecreper.totally_lib.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AnimationJsonParser {
    public static Animation parseFromJson(String json) {
        return parseFromJson((JsonObject) JsonParser.parseString(json));
    }
    public static Animation parseFromJson(JsonObject json) {
        return null;
    }
}
