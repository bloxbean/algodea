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

package com.bloxbean.algorand.idea.util;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PsiUtil {

    public static Optional<PsiElement> findParent(PsiElement node, List<IElementType> searchTypes) {
        if (node == null) {
            return Optional.empty();
        }
        if (node.getNode() != null && searchTypes.contains(node.getNode().getElementType())) {
            return Optional.of(node);
        }

        return findParent(node.getParent(), searchTypes);
    }

    public static ASTNode findFirstDeepChildByType(ASTNode root, IElementType type) {
        if (root.getElementType().equals(type)) {
            return root;
        }

        for (ASTNode element = root.getFirstChildNode(); element != null; element = element.getTreeNext()) {
            ASTNode child = findFirstDeepChildByType(element, type);
            if (child != null) {
                return child;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends PsiElement> List<T> collectPsiElementsByType(PsiElement psiRoot,
                                                                          IElementType type) {
        ASTNode rootNode = psiRoot.getNode();
        List<ASTNode> nodes = new ArrayList<>();
        collectAstNodesByType(nodes, rootNode, type);

        return nodes.stream()
                .map(node -> (T) node.getPsi())
                .collect(Collectors.toList());
    }

    public static void collectAstNodesByType(List<ASTNode> nodes, ASTNode rootNode, IElementType type) {
        if (rootNode.getElementType().equals(type)) {
            nodes.add(rootNode);
        }
        for (ASTNode element = rootNode.getFirstChildNode(); element != null; element = element.getTreeNext()) {
            collectAstNodesByType(nodes, element, type);
        }
    }
}
