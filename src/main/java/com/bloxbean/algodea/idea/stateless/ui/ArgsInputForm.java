package com.bloxbean.algodea.idea.stateless.ui;

import com.bloxbean.algodea.idea.nodeint.model.ApplArg;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ArgsInputForm {
    private JTextField argTf;
    private JComboBox argTypeCB;
    private JList argList;
    private JLabel argLabel;
    private JButton argAddBtn;
    private JButton argDelBtn;
    private JPanel mainPanel;

    DefaultComboBoxModel<ArgType> argTypeComboBoxModel;
    DefaultListModel<ApplArg> argListModel;

    Project project;

    public ArgsInputForm() {

    }

    public void initializeData(Project project) {
        this.project = project;
        argAddBtn.addActionListener(e -> {
            ArgType argType = (ArgType)argTypeCB.getSelectedItem();

            if(argType != null) {
                ApplArg applArg = new ApplArg(argType, argTf.getText());
                argListModel.addElement(applArg);
                argTf.setText("");
            }
        });

        argDelBtn.addActionListener(e -> {
            Object selectedValue = argList.getSelectedValue();
            if(selectedValue != null) {
                argListModel.removeElement(selectedValue);
            }
        });
    }

    public ValidationInfo doValidate() {
        return null;
    }

    public List<ApplArg> getArgs() {
        Enumeration<ApplArg> elems = argListModel.elements();
        if(elems == null) return Collections.EMPTY_LIST;

        return Collections.list(elems);
    }

    public List<byte[]> getArgsAsBytes() throws Exception {
        Enumeration<ApplArg> elems = argListModel.elements();
        if(elems == null) return Collections.EMPTY_LIST;

        List<byte[]> argsBytes = new ArrayList<>();
        while(elems.hasMoreElements()) {
            ApplArg applArg = elems.nextElement();
            byte[] bytes = ArgTypeToByteConverter.convert(applArg.getType(), applArg.getValue());
            argsBytes.add(bytes);
        }

        return argsBytes;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        argTypeComboBoxModel
                = new DefaultComboBoxModel(new ArgType[] {ArgType.String, ArgType.Integer, ArgType.Address, ArgType.Base64});
        argTypeCB = new ComboBox(argTypeComboBoxModel);

        argListModel = new DefaultListModel<>();
        argList = new JBList(argListModel);
    }

}
