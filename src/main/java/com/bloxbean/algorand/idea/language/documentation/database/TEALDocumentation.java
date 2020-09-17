package com.bloxbean.algorand.idea.language.documentation.database;

import com.bloxbean.algorand.idea.language.opcode.TEALOpCodeFactory;

public abstract class TEALDocumentation {

    public static final DocumentationStorage OPCODES =
            new DocumentationStorage("/docs/opcodes", TEALOpCodeFactory.getInstance().getOps());
}
