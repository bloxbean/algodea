package com.bloxbean.algorand.idea.action.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompileVarTmplInputDialog extends DialogWrapper {

    private String name;
    private List<JTextField> paramTfs = new ArrayList<>();

    private List<VarParam> params = new ArrayList<>();
    private JLabel valueLabel;

    public CompileVarTmplInputDialog(String method, List<VarParam> params) {
        super(false);

        this.params = params;

        init();
        setTitle("Provide template variable values");

    }

    private void initComponents() {
        for(VarParam param: params) {
            JTextField paramTf = new JTextField();
            paramTfs.add(paramTf);

            if(!StringUtil.isEmptyOrSpaces(param.getDefaultValue())) {
                paramTf.setText(param.getDefaultValue());
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        initComponents();
        FormLayout layout = new FormLayout(
                "right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, "
                        + "right:max(40dlu;pref), 3dlu, 70dlu, 3dlu, left:max(40dlu;pref)",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");

                JPanel panel = new JPanel(layout);
        panel.setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();

        if(params.size() != 0)
            panel.add(new JSeparator(),  cc.xyw(1,  1, 9));

        int row = 3;
        Font font = panel.getFont();

        for(int i=0; i < params.size(); i++) {
            row += 2;
            JLabel paramLabel = new JLabel(params.get(i).getName());
            paramLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

            panel.add(paramLabel, cc.xy (1,  row  ));
            panel.add(paramTfs.get(i),                 cc.xyw(3,  row, 5));
        }

        return panel;

    }

    public List<VarParam> getParamsWithValues() {
        for(int i=0; i < params.size(); i++) {
            params.get(i).setValue(paramTfs.get(i).getText().trim());
        }

        return params;
    }

}
