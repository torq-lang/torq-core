/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.util.ListTools.nullSafeCopyOf;

public abstract class ActorLang extends AbstractLang implements StmtOrExpr {

    public final List<Pat> params;
    public final ProtocolType protocolType;
    public final List<StmtOrExpr> body;

    private List<StmtOrExpr> initializer;
    private List<AskStmt> askHandlers;
    private List<TellStmt> tellHandlers;

    public ActorLang(List<Pat> params, ProtocolType protocolType, List<StmtOrExpr> body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.params = nullSafeCopyOf(params);
        this.protocolType = protocolType;
        this.body = nullSafeCopyOf(body);
        List<StmtOrExpr> initializerWork = new ArrayList<>(body.size());
        List<AskStmt> askHandlersWork = new ArrayList<>(body.size());
        List<TellStmt> tellHandlersWork = new ArrayList<>(body.size());
        for (StmtOrExpr sox : body) {
            if (sox instanceof AskStmt askHandler) {
                askHandlersWork.add(askHandler);
            } else if (sox instanceof TellStmt tellHandler) {
                tellHandlersWork.add(tellHandler);
            } else {
                initializerWork.add(sox);
            }
        }
        initializer = List.copyOf(initializerWork);
        askHandlers = List.copyOf(askHandlersWork);
        tellHandlers = List.copyOf(tellHandlersWork);
    }

    public final List<? extends AskStmt> askHandlers() {
        return askHandlers;
    }

    public final List<? extends StmtOrExpr> initializer() {
        return initializer;
    }

    public final List<TellStmt> tellHandlers() {
        return tellHandlers;
    }

}
