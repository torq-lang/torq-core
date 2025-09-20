/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.klvm.Complete;

public interface ScalarAsType extends ValueType {
    @Override
    default <T, R> R accept(LangVisitor<T, R> visitor, T state) {
        return visitor.visitScalarAsType(this, state);
    }

    Complete value();
}
