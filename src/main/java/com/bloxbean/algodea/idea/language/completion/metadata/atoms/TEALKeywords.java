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

package com.bloxbean.algodea.idea.language.completion.metadata.atoms;

import com.bloxbean.algodea.idea.language.completion.metadata.elements.TEALConstantElement;
import com.bloxbean.algodea.idea.language.completion.metadata.elements.TEALFieldElement;
import com.bloxbean.algodea.idea.language.completion.metadata.elements.TEALKeywordElement;
import com.bloxbean.algodea.idea.language.opcode.TEALOpCodeFactory;
import com.bloxbean.algodea.idea.language.opcode.model.Field;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.tree.IElementType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.bloxbean.algodea.idea.language.psi.TEALTypes.*;

public final class TEALKeywords {
    public final static LookupElement PRAGMA_LINE = new TEALKeywordElement("#pragma version 4").getLookupElement();

    public final static String TXN_FIELDS = "txn_fields";
    public final static String TYPE_ENUM_MAPPING = "typeenum_constants";
    public final static String GLOBAL_FIELDS = "global_fields";
    public final static String ASSET_HOLDING_GET_FIELDS = "asset_holding_get_fields";
    public final static String ASSET_PARAMS_GET_FIELDS = "asset_params_get_fields";
    public final static String ONCOMPLETE_CONSTANTS = "oncomplete";

    //https://developer.algorand.org/docs/reference/teal/specification/#arithmetic-logic-and-cryptographic-operations
    public static final Collection<IElementType> GENERAL_OPERATIONS_ELEMENTS = Sets.newHashSet(
            SHA256,
            KECCAK256,
            SHA512_256,
            ED25519VERIFY,
            ECDSA_OP,
            PLUS,
            MINUS,
            DIVIDE,
            TIMES,
            LESSTHAN,
            GREATERTHAN,
            LESSTHANEQUAL,
            GREATERTHANEQUAL,

            LOGICAL_AND,
            LOGICAL_OR,

            //v4
            SHL_OPCODE,
            SHR_OPCODE,
            SQRT_OPCODE,
            BITLEN_OPCODE,
            EXP_OPCODE,
            //v4

            LOGICAL_EQUAL,
            LOGICAL_NOTEQUAL,

            NOT,
            LEN,
            ITOB,
            BTOI,

            MODULO,
            BITWISE_OR,
            BITWISE_AND,
            BITWISE_XOR,
            BITWISE_INVERT,
            MULW,
            ADDW,

            DIVMODW, //v4
            EXPW_OPCODE, //v4

            SETBIT,
            GETBIT,
            SETBYTE,
            GETBYTE,
            CONCAT,
            SUBSTRING,
            SUBSTRING3,

            //v4
            B_PLUS_OPCODE,
            B_MINUS_OPCODE,
            B_DIV_OPCODE,
            B_TIMES_OPCODE,
            B_LESS_THAN_OPCODE,
            B_GREATER_THAN_OPCODE,
            B_LESS_THAN_EQ_OPCODE,
            B_GREATER_THAN_EQ_OPCODE,
            B_EQUAL_OPCODE,
            B_NOT_EQUAL_OPCODE,
            B_MODULO_OPCODE,

            B_BITWISE_OR_OPCODE,
            B_BITWISE_AND_OPCODE,
            B_BITWISE_XOR_OPCODE,
            B_INVERT_OPCODE
            //v4
    );

    //Lists needed for auto-completion
    public static final List<LookupElement> KEYWORD_LOOKUP_ELEMENTS = TEALOpCodeFactory.getInstance()
            //.getOps().stream()
            .getOpCodes().stream()
            .filter(opc -> opc.getSince() <= 2)
            .map(opc -> opc.getOp())
            .sorted()
            .map(TEALKeywordElement::new)
            .map(TEALKeywordElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> KEYWORD_LOOKUP_ELEMENTS_V3 = TEALOpCodeFactory.getInstance()
            //.getOps().stream()
            .getOpCodes().stream()
            .filter(opc -> opc.getSince() == 3)
            .map(opc -> opc.getOp())
            .sorted()
            .map(TEALKeywordElement::new)
            .map(TEALKeywordElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> KEYWORD_LOOKUP_ELEMENTS_V4 = TEALOpCodeFactory.getInstance()
            //.getOps().stream()
            .getOpCodes().stream()
            .filter(opc -> opc.getSince() == 4)
            .map(opc -> opc.getOp())
            .sorted()
            .map(TEALKeywordElement::new)
            .map(TEALKeywordElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> KEYWORD_LOOKUP_ELEMENTS_V5 = TEALOpCodeFactory.getInstance()
            //.getOps().stream()
            .getOpCodes().stream()
            .filter(opc -> opc.getSince() == 5)
            .map(opc -> opc.getOp())
            .sorted()
            .map(TEALKeywordElement::new)
            .map(TEALKeywordElement::getLookupElement)
            .collect(Collectors.toList());

    private static final Collection<Field> txnFieldsList = TEALOpCodeFactory.getInstance().getFields(TXN_FIELDS);
    //V2
    public static final List<LookupElement> TXNARGS_LOOKUP_ELEMENTS = txnFieldsList
            .stream()
            .filter(f -> f.getSince() <= 2)
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    //V3
    public static final List<LookupElement> TXNARGS_LOOKUP_ELEMENTS_V3 = txnFieldsList
            .stream()
            .filter(f -> f.getSince() == 3)
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    //V4
    public static final List<LookupElement> TXNARGS_LOOKUP_ELEMENTS_V4 = txnFieldsList
            .stream()
            .filter(f -> f.getSince() == 4)
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    //V5
    public static final List<LookupElement> TXNARGS_LOOKUP_ELEMENTS_V5 = txnFieldsList
            .stream()
            .filter(f -> f.getSince() == 5)
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    //V2
    public static final List<TEALFieldElement> TXNARGS_LOOKUP_ELEMENTS_STREAM = txnFieldsList
            .stream()
            .filter(f -> f.getSince() <= 2)
            .map(f -> new TEALFieldElement(f))
            .collect(Collectors.toList());

    //V3
    public static final List<TEALFieldElement> TXNARGS_LOOKUP_ELEMENTS_STREAM_V3 = txnFieldsList
            .stream()
            .filter(f -> f.getSince() == 3)
            .map(f -> new TEALFieldElement(f))
            .collect(Collectors.toList());

    //V4
    public static final List<TEALFieldElement> TXNARGS_LOOKUP_ELEMENTS_STREAM_V4 = txnFieldsList
            .stream()
            .filter(f -> f.getSince() == 4)
            .map(f -> new TEALFieldElement(f))
            .collect(Collectors.toList());

    //V5
    public static final List<TEALFieldElement> TXNARGS_LOOKUP_ELEMENTS_STREAM_V5 = txnFieldsList
            .stream()
            .filter(f -> f.getSince() == 5)
            .map(f -> new TEALFieldElement(f))
            .collect(Collectors.toList());

    public static final List<LookupElement> TYPEENUM_CONSTANT_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(TYPE_ENUM_MAPPING)
            .stream()
            .map(f -> new TEALConstantElement(f))
            .map(TEALConstantElement::getLookupElement)
            .collect(Collectors.toList());

    private static final Collection<Field> gloablFields = TEALOpCodeFactory.getInstance().getFields(GLOBAL_FIELDS);
    //V2
    public static final List<LookupElement> GLOBAL_FIELDS_ELEMENTS = gloablFields
            .stream()
            .filter(f -> f.getSince() <= 2)
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    //since V3
    public static final List<LookupElement> GLOBAL_FIELDS_ELEMENTS_V3 = gloablFields
            .stream()
            .filter(f -> f.getSince() == 3)
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> ASSET_HOLDING_GET_FIELDS_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(ASSET_HOLDING_GET_FIELDS)
            .stream()
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> ASSET_PARAMS_GET_FIELDS_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(ASSET_PARAMS_GET_FIELDS)
            .stream()
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> ONCOMPLETE_CONSTANT_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(ONCOMPLETE_CONSTANTS)
            .stream()
            .map(f -> new TEALConstantElement(f))
            .map(TEALConstantElement::getLookupElement)
            .collect(Collectors.toList());

}
