package com.bloxbean.algodea.idea.nodeint.service;

public class SourceConstatant {
    // user declared approval program (initial)
    public static String approvalProgramSourceInitial ="#pragma version 2\n" +
            "  \n" +
            "// read global state\n" +
            "byte \"counter\"\n" +
            "dup\n" +
            "app_global_get\n" +
            "\n" +
            "// increment the value\n" +
            "int 1\n" +
            "+\n" +
            "\n" +
            "// store to scratch space\n" +
            "dup\n" +
            "store 0\n" +
            "\n" +
            "// update global state\n" +
            "app_global_put\n" +
            "\n" +
            "// load return value as approval\n" +
            "load 0\n" +
            "return";

    // user declared approval program (refactored)
    public static String approvalProgramSourceRefactored = "#pragma version 2\n" +
            "//// Handle each possible OnCompletion type. We don't have to worry about\n" +
            "//// handling ClearState, because the ClearStateProgram will execute in that\n" +
            "//// case, not the ApprovalProgram.\n" +

            "txn OnCompletion\n" +
            "int NoOp\n" +
            "==\n" +
            "bnz handle_noop\n" +

            "txn OnCompletion\n" +
            "int OptIn\n" +
            "==\n" +
            "bnz handle_optin\n" +

            "txn OnCompletion\n" +
            "int CloseOut\n" +
            "==\n" +
            "bnz handle_closeout\n" +

            "txn OnCompletion\n" +
            "int UpdateApplication\n" +
            "==\n" +
            "bnz handle_updateapp\n" +

            "txn OnCompletion\n" +
            "int DeleteApplication\n" +
            "==\n" +
            "bnz handle_deleteapp\n" +

            "//// Unexpected OnCompletion value. Should be unreachable.\n" +
            "err\n" +

            "handle_noop:\n" +
            "//// Handle NoOp\n" +
            "//// Check for creator\n" +
            "addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4\n" +
            "txn Sender\n" +
            "==\n" +
            "bnz handle_optin\n" +

            "//// read global state\n" +
            "byte \"counter\"\n" +
            "dup\n" +
            "app_global_get\n" +

            "//// increment the value\n" +
            "int 1\n" +
            "+\n" +

            "//// store to scratch space\n" +
            "dup\n" +
            "store 0\n" +

            "//// update global state\n" +
            "app_global_put\n" +

            "//// read local state for sender\n" +
            "int 0\n" +
            "byte \"counter\"\n" +
            "app_local_get\n" +

            "//// increment the value\n" +
            "int 1\n" +
            "+\n" +
            "store 1\n" +

            "//// update local state for sender\n" +
            "//// update \"counter\"\n" +
            "int 0\n" +
            "byte \"counter\"\n" +
            "load 1\n" +
            "app_local_put\n" +

            "//// update \"timestamp\"\n" +
            "int 0\n" +
            "byte \"timestamp\"\n" +
            "txn ApplicationArgs 0\n" +
            "app_local_put\n" +

            "//// load return value as approval\n" +
            "load 0\n" +
            "return\n" +

            "handle_optin:\n" +
            "//// Handle OptIn\n" +
            "//// approval\n" +
            "int 1\n" +
            "return\n" +

            "handle_closeout:\n" +
            "//// Handle CloseOut\n" +
            "////approval\n" +
            "int 1\n" +
            "return\n" +

            "handle_deleteapp:\n" +
            "//// Check for creator\n" +
            "addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4\n" +
            "txn Sender\n" +
            "==\n" +
            "return\n" +

            "handle_updateapp:\n" +
            "//// Check for creator\n" +
            "addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4\n" +
            "txn Sender\n" +
            "==\n" +
            "return\n";

    // declare clear state program source
    public static String clearProgramSource = "#pragma version 2\n" +
            "int 1\n";
}
