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

package com.bloxbean.algodea.idea.language.completion.metadata.elements;

import com.bloxbean.algodea.idea.language.opcode.TEALOpCodeFactory;
import com.bloxbean.algodea.idea.language.opcode.model.OpCode;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;

public class TEALKeywordElement implements TEALElement {
    private final static Logger LOG = Logger.getInstance(TEALKeywordElement.class);

    private final String keyword;

    public TEALKeywordElement(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public LookupElement getLookupElement() {
        if(keyword == null)
            return null;

        OpCode opCode = TEALOpCodeFactory.getInstance().getOpCode(keyword);

        if(opCode == null) {
            return LookupElementBuilder
                    .create(keyword)
                    .withIcon(AlgoIcons.OPCODE_ICON);
        } else  {
            if(LOG.isDebugEnabled() && keyword == null)
                LOG.error("keyword cannot be null");

            return LookupElementBuilder
                    .create(keyword)
                    .withIcon(AlgoIcons.OPCODE_ICON)
                    . withTypeText(opCode.getOpcode());
        }
    }
}
