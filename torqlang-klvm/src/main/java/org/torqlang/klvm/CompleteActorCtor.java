/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

public final class CompleteActorCtor extends ActorCtor implements CompleteProc {

    private CompleteClosure handlersCtor;

    private CompleteActorCtor() {
    }

    public CompleteActorCtor(CompleteClosure handlersCtor) {
        this.handlersCtor = handlersCtor;
    }

    static CompleteActorCtor instanceForRestore() {
        return new CompleteActorCtor();
    }

    @Override
    public final CompleteActorCtor checkComplete() {
        return this;
    }

    @Override
    public final CompleteClosure handlersCtor() {
        return handlersCtor;
    }

    final void restore(CompleteClosure handlersCtor) {
        this.handlersCtor = handlersCtor;
    }

}
