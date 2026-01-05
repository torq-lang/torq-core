/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.Set;

/*
 * A Token is a final and unforgeable value with no fields.
 */
public final class Token implements Literal {

    private static final Object LOCK = new Object();

    private static volatile long nextId = 1;

    /*
     * Practically, an id can never overflow. If we create a Token every nanosecond, we create 31,536,000,000,000,000
     * tokens in 1 year. The total number of positive long values is 9,223,372,036,854,775,807. Therefore, we have
     * roughly 9,223,372/31,536 or approximately 293 years of unique tokens if we create a token every nanosecond.
     */
    public final long id;

    public Token() {
        synchronized (LOCK) {
            id = nextId++;
        }
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) {
        return visitor.visitScalar(this, state);
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) {
        return this.equals(operand);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Token that = (Token) other;
        return id == that.id;
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
        return "$token(" + id + ")";
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public Literal label() {
        return Null.SINGLETON;
    }

    @Override
    public final String toNativeValue() {
        return getClass().getName() + "." + id;
    }

    @Override
    public final String toString() {
        // A token doesn't have a close Java representation so we use its kernel representation
        return formatAsKernelString();
    }

    @Override
    public final CompleteProc valueAt(int index) {
        throw new IndexOutOfBoundsException(Integer.toString(index));
    }
}