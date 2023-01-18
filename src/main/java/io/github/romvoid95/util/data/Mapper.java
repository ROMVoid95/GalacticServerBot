package io.github.romvoid95.util.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public final class Mapper
{
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) 
        .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    
    public static final ObjectMapper instance() {
        return MAPPER;
    }
    
    public static final ObjectWriter prettyPrintWriter()
    {
        return MAPPER.writerWithDefaultPrettyPrinter();
    }
    
    public static <T> String toJson(T object) throws JsonProcessingException {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(json, clazz);
    }

    public static <T> T fromJson(String json, TypeReference<T> type) throws JsonProcessingException {
        return MAPPER.readValue(json, type);
    }

    public static <T> T fromJson(String json, JavaType type) throws JsonProcessingException {
        return MAPPER.readValue(json, type);
    }

}
