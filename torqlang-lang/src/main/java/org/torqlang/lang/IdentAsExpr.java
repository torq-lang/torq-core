/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.klvm.Ident;
import org.torqlang.klvm.Str;
import org.torqlang.util.SourceSpan;

public final class IdentAsExpr extends AbstractLang implements LabelExpr {

    public final Ident ident;

    public IdentAsExpr(Ident ident, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.ident = ident;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state) {
        return visitor.visitIdentAsExpr(this, state);
    }

    public final Str asStr() {
        return ident.asStr();
    }
}
