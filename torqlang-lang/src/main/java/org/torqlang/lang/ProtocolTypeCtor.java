/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.util.SourceSpan;

import java.util.List;

import static org.torqlang.util.ListTools.nullSafeCopyOf;

public final class ProtocolTypeCtor extends AbstractLang implements ProtocolType {

    public final IdentAsProtocolType name;
    public final List<Type> typeArgs;

    public ProtocolTypeCtor(IdentAsProtocolType name,
                            List<? extends Type> typeArgs,
                            SourceSpan sourceSpan)
    {
        super(sourceSpan);
        this.name = name;
        this.typeArgs = nullSafeCopyOf(typeArgs);
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state) {
        return visitor.visitProtocolTypeCtor(this, state);
    }

}
