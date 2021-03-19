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

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.language.opcode.model.Field;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;

public class TEALConstantElement implements TEALElement {
    private final static Logger LOG = Logger.getInstance(TEALConstantElement.class);

    private final Field field;

    public TEALConstantElement(Field field) {
        this.field = field;
    }

    @Override
    public LookupElement getLookupElement() {
        if(field == null)
            return null;

        if(LOG.isDebugEnabled()) {
            if (field.getName() == null) {
                LOG.error("Field.getName() cannot be null" + field);
            }
        }
        return LookupElementBuilder
                .create(field.getName())
                .withIcon(AlgoIcons.NAMED_INT_CONSTANT_ICON)
                .withTypeText(field.getType());

    }

    public LookupElement getCompositeLookupElement(String prefix, String suffix) {

        if(field == null)
            return null;

        if(LOG.isDebugEnabled()) {
            if (field.getName() == null) {
                LOG.error("Field.getName() cannot be null" + field);
            }
        }

        StringBuilder sb = new StringBuilder();

        if(prefix != null) {
            sb.append(prefix)
                    .append(" ");
        }

        sb.append(field.getName());

        if(suffix != null)
            sb.append(" ")
                .append(suffix);

        return LookupElementBuilder
                .create(sb.toString())
                .withIcon(AlgoIcons.NAMED_INT_CONSTANT_ICON);

    }
}
