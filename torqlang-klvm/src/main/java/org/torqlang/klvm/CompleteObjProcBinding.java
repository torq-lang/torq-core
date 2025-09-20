/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

public final class CompleteObjProcBinding<T extends CompleteObj> implements CompleteProc, ObjProcBinding<T> {

    private final T obj;
    private final ObjProc<T> proc;

    public CompleteObjProcBinding(T obj, CompleteObjProc<T> proc) {
        this.obj = obj;
        this.proc = proc;
    }

    @Override
    public final void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        proc.apply(obj, ys, env, machine);
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final T obj() {
        return obj;
    }

    @Override
    public final ObjProc<T> proc() {
        return proc;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
