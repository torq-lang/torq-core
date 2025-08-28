/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.IdentityHashMap;

public final class PartialActorCtor extends ActorCtor implements PartialProc {

    private final PartialClosure handlersCtor;

    public PartialActorCtor(PartialClosure handlersCtor) {
        this.handlersCtor = handlersCtor;
    }

    @Override
    public CompleteActorCtor checkComplete() throws WaitVarException {
        return checkComplete(new IdentityHashMap<>());
    }

    @Override
    public final CompleteActorCtor checkComplete(IdentityHashMap<Partial, Complete> memos) throws WaitVarException {
        Complete previous = memos.get(this);
        if (previous != null) {
            return (CompleteActorCtor) previous;
        }
        CompleteActorCtor thisCompleteCtor = CompleteActorCtor.instanceForRestore();
        memos.put(this, thisCompleteCtor);
        thisCompleteCtor.restore(handlersCtor.checkComplete(memos));
        return thisCompleteCtor;
    }

    @Override
    public final PartialClosure handlersCtor() {
        return handlersCtor;
    }
}
