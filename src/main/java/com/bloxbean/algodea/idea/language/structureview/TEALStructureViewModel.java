package com.bloxbean.algodea.idea.language.structureview;

import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class TEALStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    public TEALStructureViewModel(PsiFile psiFile) {
        super(psiFile, new TEALStructureViewElement(psiFile));
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof TEALFile;
    }

}
