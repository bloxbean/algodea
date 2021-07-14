package com.bloxbean.algodea.idea.language.structureview;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.language.psi.TEALBranch;
import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.bloxbean.algodea.idea.language.psi.TEALProgram;
import com.bloxbean.algodea.idea.language.psi.impl.TEALBranchImpl;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TEALStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private final NavigatablePsiElement element;
    private ItemPresentation presentation;

    public TEALStructureViewElement(NavigatablePsiElement element) {
        this.element = element;
        if(element instanceof TEALFile) {
            presentation = new ItemPresentation() {
                @Override
                public @Nullable String getPresentableText() {
                    return element.getName();
                }

                @Override
                public @Nullable String getLocationString() {
                    return null;
                }

                @Override
                public @Nullable Icon getIcon(boolean unused) {
                    return AlgoIcons.TEAL_PROG_ICON;
                }
            };
        } else {
            presentation = this.element.getPresentation();
        }
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element.canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        if(element instanceof TEALBranch) {
            return ((TEALBranchImpl) element).getId().getText();
        } else {
            String name = element.getName();
            return name != null ? name : "";
        }
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return presentation != null ? presentation : new PresentationData();
    }


    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof TEALFile) {
            List<TEALProgram> properties = PsiTreeUtil.getChildrenOfTypeAsList(element, TEALProgram.class);

            Collection<TEALBranch> branches = PsiTreeUtil.findChildrenOfType(element, TEALBranch.class);
            List<TreeElement> treeElements = new ArrayList<>(properties.size());
            for (TEALBranch branch : branches) {
                treeElements.add(new TEALStructureBranchElement((TEALBranchImpl) branch));
            }
            return treeElements.toArray(new TreeElement[0]);
        }

        return EMPTY_ARRAY;
    }

}
