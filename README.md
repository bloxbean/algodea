![Java CI with Gradle](https://github.com/bloxbean/algorand-idea-plugin/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

![algoDEA](https://github.com/bloxbean/algodea/raw/master/src/main/resources/icons/algorand_intellij.png)

algoDEA - Algorand Plugin for Intellij

[algoDEA Documents](https://algodea-docs.bloxbean.com)

Latest Version : [v0.5.0-beta](https://github.com/bloxbean/algodea/releases/tag/v0.5.0-beta)

## Features
  - Algorand Smart Contract Project type
  - Code Completion, Syntax Highlighter and other editor level support for TEAL file
  - Create Stateless & Stateful Smart Contract
  - Atomic Transfer
  - PyTeal Support
  - Dry Run support for stateful & stateless contract (Experimental)

  - Algorand Node support
      - Custom node
      - Purestake.io
  - TEAL Compile using "goal" and algod REST api endpoint 
  - Account Management
      - Create account
      - Create Multi-Sig account
      - List Account
      - Account Details
      - Dump Account
  - Stateless Smart Contract
      - Generate Logic sig and multi-sig Logic Sig 
      - Send Logic Sig transactions
  - Stateful Smart Contract 
      - Create Application
      - Call, OptIn, CloseOut, Clear, ReadState
      - Delete Application
      - Update Application
  - Asset Management
      - Creat, Modify, OptIn, Freeze, UnFreeze, Revoke, Destroy
      
  - Transfer 
      - Algo, ASA
      
## Supported IntelliJ version (2020.2 and above)

The plugin has been tested with the following IDEs

- IntelliJ IDEA (Community/Ultimate/Educational)
- PyCharm
- GoLand
- WebStorm
- PhpStorm

**Known Issues:**
 - Message alert popup doesn't work properly on 2020.3 Ultimate edition with Big Sur OS (Mac). Due to this some functionalities don't work as expected.
   
   Similar issue (https://youtrack.jetbrains.com/issue/IDEA-257834)
   
   Fix: Upgrade your IDE to a newer version.
   
 - PyTeal compilation through pop-up menu only works in IntelliJ IDEA and PyCharm.

## Installation
- algoDEA plugin is available on IntelliJ Marketplace as "AlgoDEA Algorand Integration". You can directly install it from IntelliJ IDE.
(https://plugins.jetbrains.com/plugin/15300-algodea-algorand-integration)

- You can also manually download and install the plugin from the "releases" section of this project.
(https://github.com/bloxbean/algodea/releases)


## Documents

* algoDEA IntelliJ IDEA Plugin documents can be found at  (https://algodea-docs.bloxbean.com)

## Build From Source
* Clone the repository
* $> ./gradlew clean build
* $> Get the plugin zip file from build/distributions/algodea-{version}.zip 

## Getting Started Video

[![Getting Started with AlgoDEA](https://img.youtube.com/vi/sah1z0BinW0/0.jpg)](https://youtu.be/sah1z0BinW0)

