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
            TEALTypes.STATEACCESS_OP
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
        System.out.println("Inside get Quick navigateInfo....");
        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        System.out.println("Inside generate doc...." );
        Optional<String> opcodeDocumentation = loadingOpcodeDocumentation(element);
        if (opcodeDocumentation.isPresent()) {
            return opcodeDocumentation.get();
        }

        return null;
    }

    private Optional<String> loadingOpcodeDocumentation(PsiElement element) {

        System.out.println(" Inside generate Doc.....");

        if (TEALTypes.LOADING_OP.equals(element.getNode().getElementType())
                || TEALTypes.TXN_LOADING_OP.equals(element.getNode().getElementType())
                || TEALTypes.FLOWCONTROL_OP.equals(element.getNode().getElementType())
                || TEALTypes.STATEACCESS_OP.equals(element.getNode().getElementType())
                || TEALKeywords.GENERAL_OPERATIONS_ELEMENTS.contains(element.getNode().getElementType()))
        {
            String value = element.getNode().getText();
            OpCode opCode = TEALOpCodeFactory.getInstance().getOpCode(value);
            if(opCode == null)
                return Optional.empty();
            else
                return opCode.formatHtml();
        } else if(element instanceof TEALGeneralOperation) {
            String value = element.getNode().getText();
            return TEALDocumentation.OPCODES.lookup(value);
        }
        return Optional.empty();
    }
}
