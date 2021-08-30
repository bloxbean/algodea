package com.bloxbean.algodea.idea.dryrun.util;

import com.algorand.algosdk.crypto.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static String[] sources(File dryDumpFile) throws IOException {
        JsonNode jsonNode = mapper.readTree(dryDumpFile);
        if(jsonNode == null)
            return null;

        List<String> sources = new ArrayList<>();
        ArrayNode sourcesNode = (ArrayNode)jsonNode.get("sources");
        for(JsonNode nd: sourcesNode) {
            JsonNode sourceNode = nd.get("source");
            if(sourceNode != null)
                sources.add(sourceNode.asText());
        }

        return sources.toArray(new String[0]);
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
