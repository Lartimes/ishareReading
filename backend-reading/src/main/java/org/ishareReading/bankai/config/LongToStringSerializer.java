package org.ishareReading.bankai.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

// 自定义 Long 类型的序列化器
public class LongToStringSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(value.toString());
        } else {
            gen.writeNull();
        }
    }

    @Override
    public Class<Long> handledType() {
        return Long.class;
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Long value) {
        return value == null;
    }

}