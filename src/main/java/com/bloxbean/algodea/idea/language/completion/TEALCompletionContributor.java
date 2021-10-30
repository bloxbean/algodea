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

package com.bloxbean.algodea.idea.language.completion;

import com.bloxbean.algodea.idea.language.completion.providers.*;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

public class TEALCompletionContributor extends CompletionContributor {

    public TEALCompletionContributor() {
        //byte
        extend(CompletionType.BASIC,
                ByteCompletionProvider.PATTERN,
                new ByteCompletionProvider());

        //opcode
        extend(CompletionType.BASIC,
                KeywordCompletionProvider.PATTERN,
                new KeywordCompletionProvider());
        extend(CompletionType.BASIC,
                KeywordCompletionProvider.FIRST_ELEMENT_PATTERN,
                new KeywordCompletionProvider());
        extend(CompletionType.BASIC,
                KeywordCompletionProvider.FIRST_ELEMENT_AFTER_PRAGMA_PATTERN,
                new KeywordCompletionProvider());


        extend(CompletionType.BASIC,
                TxnArgCompletionProvider.PATTERN,
                new TxnArgCompletionProvider());

        extend(CompletionType.BASIC,
                GlobalFieldsCompletionProvider.PATTERN,
                new GlobalFieldsCompletionProvider());

        //Gtxn, Gtxna
        extend(CompletionType.BASIC,
                GTxnArgCompletionProvider.FIRSTARG_PATTERN,
                new GTxnArgCompletionProvider());
        extend(CompletionType.BASIC,
                GTxnArgCompletionProvider.SECONDARG_PATTERN,
                new GTxnArgCompletionProvider());

        //asset_holding_get
        extend(CompletionType.BASIC,
                AssetHoldingGetCompletionProvider.PATTERN,
                new AssetHoldingGetCompletionProvider());

        //asset_params_get
        extend(CompletionType.BASIC,
                AssetParamsGetCompletionProvider.PATTERN,
                new AssetParamsGetCompletionProvider());

        //app_params_get
        extend(CompletionType.BASIC,
                AppParamsGetCompletionProvider.PATTERN,
                new AppParamsGetCompletionProvider());

        //Named integer constants
        extend(CompletionType.BASIC,
                NamedIntegerConstantsCompletionProvider.PATTERN,
                new NamedIntegerConstantsCompletionProvider());

        //Itxn fields
        extend(CompletionType.BASIC,
                ItxnTxnArgsCompletionProvider.PATTERN,
                new ItxnTxnArgsCompletionProvider());

    }

}
