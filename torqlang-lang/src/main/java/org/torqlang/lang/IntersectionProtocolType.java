/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.util.SourceSpan;

public final class IntersectionProtocolType extends AbstractLang implements ProtocolType {

    public final ProtocolType arg1;
    public final ProtocolType arg2;

    public IntersectionProtocolType(ProtocolType arg1, ProtocolType arg2, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state) {
        return visitor.visitIntersectionProtocolType(this, state);
    }

}
