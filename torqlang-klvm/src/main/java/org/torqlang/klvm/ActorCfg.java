/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public final class ActorCfg implements PartialObj {

    private final List<Complete> args;
    private final Closure handlersCtor;

    public ActorCfg(List<Complete> args, Closure handlersCtor) {
        this.args = args;
        this.handlersCtor = handlersCtor;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) {
        return visitor.visitActorCfg(this, state);
    }

    public final List<Complete> args() {
        return args;
    }

    @Override
    public final Feature featureAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }

    @Override
    public final PartialField fieldAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }

    @Override
    public final int fieldCount() {
        return 0;
    }

    public final Closure handlersCtor() {
        return handlersCtor;
    }

    @Override
    public final Literal label() {
        return Null.SINGLETON;
    }

    @Override
    public final ValueOrVar select(Feature feature) throws WaitException {
        throw new FeatureNotFoundError(this, feature);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

    @Override
    public final Proc valueAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }

}
