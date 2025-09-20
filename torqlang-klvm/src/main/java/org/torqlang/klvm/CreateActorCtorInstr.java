/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import org.torqlang.util.SourceSpan;

public final class CreateActorCtorInstr extends AbstractCreateProcInstr {

    public CreateActorCtorInstr(Ident x, ProcDef procDef, SourceSpan sourceSpan) {
        super(x, procDef, sourceSpan);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) {
        return visitor.visitCreateActorCtorInstr(this, state);
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        PartialClosure handlersCtor = computeClosure(env);
        ActorCtor actorCtor = new PartialActorCtor(handlersCtor);
        ValueOrVar identRes = x.resolveValueOrVar(env);
        identRes.bindToValue(actorCtor, null);
    }

}
