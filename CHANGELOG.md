### 0.9.0
  - AVM 1.1 Opcodes support (TEAL 6)
  - Client code generation (JavaScript) for 
      - Stateful contract operations
      - Stateless contract operations
      - ASA operations
      - Transfer Algo/ASA
  - Algoexplorer api endpoints updated
### 0.8.0
  - AVM 1.0 Opcodes support (TEAL v5)
  - On-Completion option during stateful contract creation
### 0.7.0
  - TEAL debug support
### 0.6.0
  - TEAL v4 support (New Opcodes, Txn Fields)
  - New Structure view
### 0.5.0
  - TEAL v3 support (New Opcodes, Global Fields, Txn Fields)
  - Auto-complete, Syntax Highlights for TEAL v3
  - Algorand java sdk version updated to support v3 deployment
  - Error highlighter if v3 opcodes are used in TEAL v2 files
### 0.4.2
  - Rekey support [#19]
  - Sign and submit exported transaction file [#20]
  - Payment and Opt-In transaction using Stateless TEAL file [#17, #18]
  - Support for Algoexplorer api endpoint
  - UX enhancements [#20]  
  - Fixes : [#10, #11, #12, #13, #14]
### 0.3.3
  - Fixes : Enable "Export txn" for read-only accounts [#7, #8, #9]
### 0.3.2
  - Support ASA in Logic Sig Send Transaction UI
  - Opt-In Asset transaction using a Logic Sig 
  - Fixes [#5]
### 0.3.1
  - Support for other IntelliJ IDEs (PyCharm, GoLand, WebStorm)
  - Fixes
### 0.3.0
  - Support for other IntelliJ IDEs (PyCharm, GoLand, WebStorm)
### 0.2.0
  - Atomic Transfer
  - PyTeal Support
  - Dry Run support for stateful & stateless contract (Experimental)
  
### 0.1.0
  - Algorand Smart Contract Project type
  - Code Completion, Syntax Highlighter and other editor level support for TEAL file
  - Create Stateless & Stateful Smart Contract
  - Algorand Node support (Custom node, Purestake.io)
  - TEAL Compile using "goal" and algod REST api endpoint 
  - Account Management. (Create account, Create Multi-Sig account, List Account, Account Details, Dump Account)
  - Stateless Smart Contract (Generate and send Logic sig and multi-sig Logic Sig transactions)
  - Stateful Smart Contract 
      - Create Application
      - Call, OptIn, CloseOut, Clear, ReadState
      - Delete Application
      - Update Application
  - Asset Management
      - Creat, Modify, OptIn, Freeze, UnFreeze, Revoke, Destroy
      
  - Transfer 
      - Algo, ASA
