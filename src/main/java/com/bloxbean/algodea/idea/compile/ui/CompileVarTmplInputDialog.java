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
package com.bloxbean.algodea.idea.compile.ui;

import com.bloxbean.algodea.idea.compile.model.VarParam;
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

    public CompileVarTmplInputDialog(String name, List<VarParam> params) {
        super(false);

        this.params = params;

        init();
        setTitle("Variables : " + name);
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
