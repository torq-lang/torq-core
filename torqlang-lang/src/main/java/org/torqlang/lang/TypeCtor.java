/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.util.SourceSpan;

import java.util.List;

import static org.torqlang.util.ListTools.nullSafeCopyOf;

public final class TypeCtor extends AbstractLang implements ValueType {

    public final IdentAsType name;
    public final List<Type> typeArgs;

    public TypeCtor(IdentAsType name,
                    List<? extends Type> typeArgs,
                    SourceSpan sourceSpan)
    {
        super(sourceSpan);
        this.name = name;
        this.typeArgs = nullSafeCopyOf(typeArgs);
    }

    public static TypeCtor arrayOf(Type elementType) {
        return TypeCtor.create(Type.fromIdent(ArrayType.IDENT), List.of(elementType));
    }

    public static TypeCtor create(IdentAsType name, List<? extends Type> typeArgs) {
        return new TypeCtor(name, typeArgs, SourceSpan.emptySourceSpan());
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state) {
        return visitor.visitTypeCtor(this, state);
    }

}
