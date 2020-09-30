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

package com.bloxbean.algorand.idea.account.ui;

import com.bloxbean.algorand.idea.account.model.AlgoAccount;

import javax.swing.table.AbstractTableModel;
import java.math.BigInteger;
import java.util.List;

public class AccountListTableModel extends AbstractTableModel {

    private boolean showBalance;
    private List<AlgoAccount> accounts;
    protected String[] columnNames = new String[] {"Account", "Balance"};
    protected Class[] columnClasses = new Class[] {String.class, String.class};

    public AccountListTableModel(List<AlgoAccount> accounts, boolean showBalance) {
        this.accounts = accounts;
        this.showBalance = showBalance;

        if(!showBalance) { //Balance is not shown for local mode
            columnNames = new String[]{"Account"};
            columnClasses = new Class[] {String.class};
        }
    }

    @Override
    public int getRowCount() {
        return accounts.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(accounts == null || accounts.size() == 0 || accounts.size()-1 < rowIndex)
            return null;

        AlgoAccount account = accounts.get(rowIndex);
        if(columnIndex == 0)
            return account.getAddress();
        else if(columnIndex == 1) {
            BigInteger balance = account.getBalance();
            if(balance == null)
                return "..";
            else {
                if(balance == BigInteger.ZERO)
                    return balance;
                else {
                   //TODO float aionValue = AionConversionUtil.nAmpToAion(balance);
                   // return aionValue + " Aion (" + balance + " nAmp)";
                    return balance;
                }
            }
        } else
            return null;
    }

    public boolean isCellEditable(int row, int col) {
        if (col== 0) {
            return true;
        } else {
            return false;
        }
    }
}
