package com.bloxbean.algodea.idea.language.structureview;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.language.psi.TEALBranch;
import com.bloxbean.algodea.idea.language.psi.impl.TEALBranchImpl;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TEALStructureBranchElement implements StructureViewTreeElement, SortableTreeElement {

    private final NavigatablePsiElement element;
    private ItemPresentation presentation;

    public TEALStructureBranchElement(NavigatablePsiElement element) {
        this.element = element;
        if(element instanceof TEALBranch) {
            presentation = new BranchItemPresentation((TEALBranch) this.element);
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
        }
        return "ddf";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return presentation != null ? presentation : new PresentationData();
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        return new TreeElement[0];
    }

    class BranchItemPresentation implements ItemPresentation {
        TEALBranch myElement;
        public BranchItemPresentation(TEALBranch myElement) {
            this.myElement = myElement;
        }
        @Override
        public @Nullable String getPresentableText() {
            return myElement.getId().getText();
        }

        @Override
        public @Nullable String getLocationString() {
            return null;
        }

        @Override
        public @Nullable Icon getIcon(boolean unused) {
            return AlgoIcons.SUB_ROUTINE_ICON;
        }
    }
}
