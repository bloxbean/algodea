/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bloxbean.algodea.idea.language.opcode;

import com.bloxbean.algodea.idea.language.documentation.database.DocumentationStorage;
import com.bloxbean.algodea.idea.language.opcode.model.OpCode;
import com.bloxbean.algodea.idea.language.opcode.model.Field;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;

import java.net.URL;
import java.util.*;

public class TEALOpCodeFactory {
    private static final Logger LOG = Logger.getInstance(TEALOpCodeFactory.class);
    private static String OPCODE_FILE = "/opcodes/opcodes.json";
    private static String FIELDS_FILE ="/opcodes/fields.json";

    private static TEALOpCodeFactory instance;
    private Map<String, OpCode> opCodeMap;
    private Set<String> ops;

    private Map<String, Map<String, Field>> fields;

    private TEALOpCodeFactory() {
        loadOpCodes();
        loadFields();
    }

    public static TEALOpCodeFactory getInstance() {
        if(instance != null) {
            return instance;
        } else {
            synchronized (TEALOpCodeFactory.class) {
                if (instance == null) {
                    instance = new TEALOpCodeFactory();
                }
            }
        }

        return instance;
    }

    private void loadOpCodes() {
        try {
            URL opcodeJsonUrl = DocumentationStorage.class.getResource(OPCODE_FILE);
            ObjectMapper mapper = new ObjectMapper();
            List<OpCode>  opCodes = mapper.readValue(opcodeJsonUrl, mapper.getTypeFactory().constructCollectionType(List.class, OpCode.class));
            opCodeMap = new HashMap<>();

            opCodes.stream()
                    .forEach(opCode -> {
                        opCodeMap.put(opCode.getOp(), opCode);
                    });

            ops = opCodeMap.keySet();
        } catch (Exception e) {
            LOG.warn("Error parsing opcodes.json at " + OPCODE_FILE, e);
            opCodeMap = new HashMap<>();
        }
    }

    private void loadFields() {
        try {
            URL opcodeJsonUrl = DocumentationStorage.class.getResource(FIELDS_FILE);
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, List<Field>>> typeRef
                    = new TypeReference<HashMap<String, List<Field>>>() {};

            fields = new HashMap<>();

            Map<String, List<Field>> _fields = mapper.readValue(opcodeJsonUrl, typeRef);
            _fields.entrySet()
                    .stream()
                    .forEach(e -> {
                        List<Field> fls = e.getValue();
                        if(fls != null) {
                            Map<String, Field> typeFieldMap = new HashMap();
                            fls.forEach(fl -> typeFieldMap.put(fl.getName(), fl));

                            fields.put(e.getKey(), typeFieldMap);
                        }
                    });
        } catch (Exception e) {
            LOG.warn("Error parsing fields.json at " + FIELDS_FILE, e);
            fields = new HashMap<>();
        }
    }

    public Map<String, OpCode> getOpCodeMap() {
        return opCodeMap;
    }

    public Set<String> getOps() {
        return ops;
    }

    public Collection<OpCode> getOpCodes() {
        return opCodeMap.values();
    }

    public OpCode getOpCode(String op) {
        if(op == null || op.isEmpty())
            return null;

        return opCodeMap.get(op);
    }

    public Collection<Field> getFields(String type) {
        Map<String, Field> typeFields = fields.get(type);
        if(typeFields == null) return Collections.EMPTY_LIST;
        else
            return typeFields.values();
    }

    public Field getField(String type, String fieldText) {
        if(StringUtil.isEmpty(fieldText)) return null;

        Map<String, Field> typeFields = fields.get(type);
        if(typeFields == null) return null;

        return typeFields.get(fieldText);
    }

}
