/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.util.SourceSpan;

import java.util.List;

public final class ActorStmt extends ActorLang implements Stmt {

    public final IdentAsExpr name;

    public ActorStmt(IdentAsExpr name, List<Pat> params, ProtocolType protocolType, List<StmtOrExpr> body, SourceSpan sourceSpan) {
        super(params, protocolType, body, sourceSpan);
        this.name = name;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state) {
        return visitor.visitActorStmt(this, state);
    }

}
