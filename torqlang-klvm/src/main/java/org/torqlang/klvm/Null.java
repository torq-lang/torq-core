/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

public final class Null implements Literal {

    public static final String NULL_NAME = "null";
    public static final Null SINGLETON = new Null();

    private Null() {
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) {
        return visitor.visitScalar(this, state);
    }

    @Override
    public final String appendToString(String string) {
        return string + NULL_NAME;
    }

    @Override
    public final Feature featureAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }

    @Override
    public final CompleteField fieldAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }

    @Override
    public final int fieldCount() {
        return 0;
    }

    @Override
    public final String formatAsKernelString() {
        return NULL_NAME;
    }

    @Override
    public final Literal label() {
        return Null.SINGLETON;
    }

    @Override
    public final Object toNativeValue() {
        return null;
    }

    @Override
    public final String toString() {
        return NULL_NAME;
    }

    @Override
    public final CompleteProc valueAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }
}
