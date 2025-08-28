/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.lang.ProtocolStmt;

public record CompiledProtocolExport(ProtocolStmt protocolStmt, CompiledModule module) implements CompiledExport {
    @Override
    public final String simpleName() {
        return protocolStmt.name.ident.name;
    }
}
