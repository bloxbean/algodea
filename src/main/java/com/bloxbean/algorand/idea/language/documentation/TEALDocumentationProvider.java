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

package com.bloxbean.algorand.idea.language.documentation;

import com.bloxbean.algorand.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algorand.idea.language.documentation.database.TEALDocumentation;
import com.bloxbean.algorand.idea.language.opcode.model.OpCode;
import com.bloxbean.algorand.idea.language.opcode.TEALOpCodeFactory;
import com.bloxbean.algorand.idea.language.psi.TEALGeneralOperation;
import com.bloxbean.algorand.idea.language.psi.TEALTypes;
import com.bloxbean.algorand.idea.util.PsiUtil;
import com.google.common.collect.Lists;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.bloxbean.algorand.idea.language.completion.metadata.atoms.TEALKeywords.*;

public class TEALDocumentationProvider extends AbstractDocumentationProvider  {

    private static final List<IElementType> SEARCH_TYPES = Lists.newArrayList(
            TEALTypes.LOADING_OP,
            TEALTypes.TXN_LOADING_OP,
            TEALTypes.FLOWCONTROL_OP,
            TEALTypes.STATEACCESS_OP,
            TEALTypes.PSEUDO_OP
    );

    static {
        SEARCH_TYPES.addAll(GENERAL_OPERATIONS_ELEMENTS);
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor,
                                                    @NotNull PsiFile file,
                                                    @Nullable PsiElement contextElement) {

        return PsiUtil.findParent(contextElement, SEARCH_TYPES).orElse(null);
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        Optional<String> opcodeDocumentation = loadingOpcodeDocumentation(element);
        if (opcodeDocumentation.isPresent()) {
            return opcodeDocumentation.get();
        }

        return null;
    }

    private Optional<String> loadingOpcodeDocumentation(PsiElement element) {
        if (TEALTypes.LOADING_OP.equals(element.getNode().getElementType())
                || TEALTypes.TXN_LOADING_OP.equals(element.getNode().getElementType())
                || TEALTypes.FLOWCONTROL_OP.equals(element.getNode().getElementType())
                || TEALTypes.STATEACCESS_OP.equals(element.getNode().getElementType())
                || TEALKeywords.GENERAL_OPERATIONS_ELEMENTS.contains(element.getNode().getElementType()))

        {
            String value = element.getNode().getText();
            return getDocumentHtmlForKey(value);
        } else if(TEALTypes.PSEUDO_OP.equals(element.getNode().getElementType())) {
            if(element.getFirstChild() != null && element.getFirstChild().getFirstChild() != null) {
                String nodeText = element.getFirstChild().getFirstChild().getText();
                return getDocumentHtmlForKey(nodeText);
            }

        }
//        else if(element instanceof TEALGeneralOperation) {
//            String value = element.getNode().getText();
//            return TEALDocumentation.OPCODES.lookup(value);
//        }
        return Optional.empty();
    }

    private Optional<String> getDocumentHtmlForKey(String nodeText) {
        OpCode opCode = TEALOpCodeFactory.getInstance().getOpCode(nodeText);
        if (opCode == null)
            return Optional.empty();
        else
            return opCode.formatHtml();
    }
}
