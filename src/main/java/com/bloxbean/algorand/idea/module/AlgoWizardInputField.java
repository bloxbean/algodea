package com.bloxbean.algorand.idea.module;

import com.bloxbean.algorand.idea.module.sdk.AlgoSdkType;
import com.intellij.ide.util.projectWizard.WizardInputField;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Condition;

import javax.swing.*;

public class AlgoWizardInputField extends WizardInputField{

    private final AlgoSdkComboBox myCombo;

    protected AlgoWizardInputField() {
        super("Algorand", findMostRecentSdkPath());
        myCombo = new AlgoSdkComboBox();
    }

    private static String findMostRecentSdkPath() {
        Sdk sdk = ProjectJdkTable.getInstance() //.findMostRecentSdkOfType(new AlgoLocalSdkType());
                .findMostRecentSdk(new Condition<Sdk>() {
            @Override
            public boolean value(Sdk sdk) {
                return sdk.getSdkType() instanceof AlgoSdkType;
            }
        });
        return sdk != null ? sdk.getName() : null;
    }

    @Override
    public String getLabel() {
        return "Algorand SDK";
    }

    @Override
    public JComponent getComponent() {
        return myCombo;
    }

    @Override
    public String getValue() {
        Sdk sdk = myCombo.getSelectedSdk();
        return sdk == null ? "" : sdk.getHomePath();
    }
}
