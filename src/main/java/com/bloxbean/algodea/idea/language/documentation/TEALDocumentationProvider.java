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

package com.bloxbean.algodea.idea.language.documentation;

import com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algodea.idea.language.opcode.TEALOpCodeFactory;
import com.bloxbean.algodea.idea.language.opcode.model.Field;
import com.bloxbean.algodea.idea.language.opcode.model.OpCode;
import com.bloxbean.algodea.idea.language.psi.TEALTypes;
import com.bloxbean.algodea.idea.util.PsiUtil;
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

import static com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords.GENERAL_OPERATIONS_ELEMENTS;

public class TEALDocumentationProvider extends AbstractDocumentationProvider  {

    private static final List<IElementType> SEARCH_TYPES = Lists.newArrayList(
            TEALTypes.LOADING_OP,
            TEALTypes.TXN_LOADING_OP,
            TEALTypes.FLOWCONTROL_OP,
            TEALTypes.STATEACCESS_OP,
            TEALTypes.PSEUDO_OP,
            TEALTypes.NAMED_INTEGER_CONSTANT,
            TEALTypes.TYPENUM_CONSTANT,
            TEALTypes.GLOBAL_FIELD,
            TEALTypes.TXN_FIELD_ARG,
            TEALTypes.ASSET_PARAMS_GET_FIELD,
            TEALTypes.ASSET_HOLDING_GET_FIELD
    );
    public static final String ONCOMPLETE = "oncomplete";
    public static final String TYPEENUM_CONSTANTS = "typeenum_constants";
    public static final String GLOBAL_FIELDS = "global_fields";
    public static final String TXN_FIELDS = "txn_fields";
    public static final String ASSET_PARAMS_GET_FIELDS = "asset_params_get_fields";
    public static final String ASSET_HOLDING_GET_FIELDS = "asset_holding_get_fields";

    static {
        SEARCH_TYPES.addAll(GENERAL_OPERATIONS_ELEMENTS);
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor,
                                                    @NotNull PsiFile file,
                                                    @Nullable PsiElement contextElement, int targetOffset) {

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
        Optional<String> documentation = loadingOpcodeDocumentation(element);
        if (documentation.isPresent()) {
            return documentation.get();
        }

        //Check if Named Integer Constant or TypeEnum Constant
        documentation = loadFieldDocumentation(element);
        if(documentation.isPresent()) {
            return documentation.get();
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

    private Optional<String> loadFieldDocumentation(PsiElement element) {
        if(element == null) return Optional.empty();

        if (TEALTypes.NAMED_INTEGER_CONSTANT.equals(element.getNode().getElementType())) {
            String value = element.getNode().getText();
            Field field = TEALOpCodeFactory.getInstance().getField(ONCOMPLETE, value);
            if(field != null)
                return field.formatHtml();
        } else if(TEALTypes.TYPENUM_CONSTANT.equals(element.getNode().getElementType())) {
            String value = element.getNode().getText();
            Field field = TEALOpCodeFactory.getInstance().getField(TYPEENUM_CONSTANTS, value);
            if(field != null)
                return field.formatHtml();
        } else if(TEALTypes.GLOBAL_FIELD.equals(element.getNode().getElementType())) {
            String value = element.getNode().getText();
            Field field = TEALOpCodeFactory.getInstance().getField(GLOBAL_FIELDS, value);
            if(field != null)
                return field.formatHtml();
        } else if(TEALTypes.TXN_FIELD_ARG.equals(element.getNode().getElementType())) {
            String value = element.getNode().getText();
            Field field = TEALOpCodeFactory.getInstance().getField(TXN_FIELDS, value);
            if(field != null)
                return field.formatHtml();
        } else if(TEALTypes.ASSET_PARAMS_GET_FIELD.equals(element.getNode().getElementType())) {
            String value = element.getNode().getText();
            Field field = TEALOpCodeFactory.getInstance().getField(ASSET_PARAMS_GET_FIELDS, value);
            if(field != null)
                return field.formatHtml();
        } else if(TEALTypes.ASSET_HOLDING_GET_FIELD.equals(element.getNode().getElementType())) {
            String value = element.getNode().getText();
            Field field = TEALOpCodeFactory.getInstance().getField(ASSET_HOLDING_GET_FIELDS, value);
            if(field != null)
                return field.formatHtml();
        }

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
