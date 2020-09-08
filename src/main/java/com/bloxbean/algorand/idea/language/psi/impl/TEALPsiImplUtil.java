package com.bloxbean.algorand.idea.language.psi.impl;

import com.bloxbean.algorand.idea.language.psi.TEALProperty;
import com.bloxbean.algorand.idea.language.psi.TEALTypes;
import com.intellij.lang.ASTNode;

public class TEALPsiImplUtil {
    public static String getKey(TEALProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(TEALTypes.KEY);
        if (keyNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return keyNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static String getValue(TEALProperty element) {
        ASTNode valueNode = element.getNode().findChildByType(TEALTypes.VALUE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }
}
