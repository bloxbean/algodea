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
import com.google.common.collect.Sets;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.tree.IElementType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.bloxbean.algodea.idea.language.psi.TEALTypes.*;

public final class TEALKeywords {
    public final static LookupElement PRAGMA_LINE = new TEALKeywordElement("#pragma version 3").getLookupElement();

    public final static String TXN_FIELDS = "txn_fields";
    public final static String TYPE_ENUM_MAPPING = "typeenum_constants";
    public final static String GLOBAL_FIELDS = "global_fields";
    public final static String ASSET_HOLDING_GET_FIELDS = "asset_holding_get_fields";
    public final static String ASSET_PARAMS_GET_FIELDS = "asset_params_get_fields";
    public final static String ONCOMPLETE_CONSTANTS = "oncomplete";

//    public static final Collection<String> LOADING_OPERATIONS = Sets.newHashSet(
//            "intcblock", "intc", "intc_0", "intc_1", "intc_2", "intc_3", "bytecblock", "bytec", "bytec_0"
//            , "bytec_1", "bytec_2", "bytec_3", "arg", "arg_0", "arg_1", "arg_2", "arg_3", "global", "load", "store"
//            , "txn", "gtxn", "txna", "gtxna", "addr"
//            , "err", "return", "pop", "dup", "dup2", "bnz", "bz", "b"
//            , "balance", "app_opted_in", "app_local_get", "app_local_get_ex", "app_global_get"
//            , "app_global_get_ex", "app_local_put", "app_global_put", "app_local_del"
//            , "app_global_del", "asset_holding_get", "asset_params_get"
//    );

    public static final Collection<IElementType> GENERAL_OPERATIONS_ELEMENTS = Sets.newHashSet(
            SHA256,
            KECCAK256,
            SHA512_256,
            ED25519VERIFY,
            PLUS,
            MINUS ,
            DIVIDE ,
            TIMES,
            LESSTHAN ,
            GREATERTHAN ,
            LESSTHANEQUAL  ,
            GREATERTHANEQUAL,

            LOGICAL_AND,
            LOGICAL_OR  ,
            LOGICAL_EQUAL,
            LOGICAL_NOTEQUAL,

            NOT ,
            LEN ,
            ITOB,
            BTOI ,

            MODULO ,
            BITWISE_OR  ,
            BITWISE_AND ,
            BITWISE_XOR  ,
            BITWISE_INVERT,

            MULW,
            ADDW ,
            SETBIT,
            GETBIT,
            SETBYTE,
            GETBYTE,
            CONCAT ,
            SUBSTRING  ,
            SUBSTRING3
    );

//    public static final Collection<String> TXN_ARGS = Sets.newHashSet(
//            "Sender", "Fee", "FirstValid", "FirstValidTime", "LastValid", "Note", "Lease", "Receiver", "Amount"
//            , "CloseRemainderTo", "VotePK", "SelectionPK", "VoteFirst", "VoteLast", "VoteKeyDilution", "Type"
//            , "TypeEnum", "XferAsset", "AssetAmount", "AssetSender", "AssetReceiver", "AssetCloseTo", "GroupIndex"
//            , "TxID", "ApplicationID", "OnCompletion", "ApplicationArgs", "NumAppArgs", "Accounts", "NumAccounts"
//            , "ApprovalProgram", "ClearStateProgram", "RekeyTo", "ConfigAsset", "ConfigAssetTotal", "ConfigAssetDecimals"
//            , "ConfigAssetDefaultFrozen", "ConfigAssetUnitName", "ConfigAssetName", "ConfigAssetURL", "ConfigAssetMetadataHash"
//            , "ConfigAssetManager", "ConfigAssetReserve", "ConfigAssetFreeze", "ConfigAssetClawback", "FreezeAsset"
//            , "FreezeAssetAccount", "FreezeAssetFrozen"
//    );


//    public static final List<LookupElement> KEYWORD_LOOKUP_ELEMENTS = LOADING_OPERATIONS.stream()
//            .map(TEALKeywordElement::new)
//            .map(TEALKeywordElement::getLookupElement)
//            .collect(Collectors.toList());
    public static final List<LookupElement> KEYWORD_LOOKUP_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getOps().stream()
//            .getOpCodes().parallelStream()
            .sorted()
            .map(TEALKeywordElement::new)
            .map(TEALKeywordElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> TXNARGS_LOOKUP_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(TXN_FIELDS)
            .stream()
            .map(f -> new TEALFieldElement(f))
            .map(TEALFieldElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<TEALFieldElement> TXNARGS_LOOKUP_ELEMENTS_STREAM = TEALOpCodeFactory.getInstance()
            .getFields(TXN_FIELDS)
            .stream()
            .map(f -> new TEALFieldElement(f))
            .collect(Collectors.toList());

    public static final List<LookupElement> TYPEENUM_CONSTANT_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(TYPE_ENUM_MAPPING)
            .stream()
            .map(f -> new TEALConstantElement(f))
            .map(TEALConstantElement::getLookupElement)
            .collect(Collectors.toList());

    public static final List<LookupElement> GLOBAL_FIELDS_ELEMENTS = TEALOpCodeFactory.getInstance()
            .getFields(GLOBAL_FIELDS)
            .stream()
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
