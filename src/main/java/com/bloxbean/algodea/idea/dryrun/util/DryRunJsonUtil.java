package com.bloxbean.algodea.idea.dryrun.util;

import com.algorand.algosdk.crypto.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class DryRunJsonUtil {
    protected final static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Address.class, new AddressSerializer());
        mapper.registerModule(module);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static class AddressSerializer extends JsonSerializer<Address> {

        @Override
        public void serialize(Address address, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if(address != null) {
                jsonGenerator.writeString(address.toString());
            } else {
                jsonGenerator.writeObject(null);
            }
        }
    }
}
