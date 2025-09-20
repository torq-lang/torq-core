/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

import org.torqlang.klvm.Ident;
import org.torqlang.util.SourceSpan;

/*
 * TYPE HIERARCHY
 *
 * - Value
 *     - Any -- turns off type checking
 *     - Composite -- defines label and fields (feature-value pairs) as a type with selection
 *         - Struct -- support unification and entailment
 *             - Array -- no label, Int32 features starting at 0, undetermined size, single value type
 *             - Rec -- label, mixed features, determined size, multiple value types
 *             - Tuple -- label, Int32 features starting at 0, determined size, multiple value types
 *         - Obj -- no unification, requires equals and hash_code, and can have hidden state
 *     - Literal
 *         - Bool -- true or false
 *         - Eof -- the type of the singleton "eof" used exclusively by iterators and streams
 *         - Null -- the type of the singleton "null"
 *         - Str -- equivalent to the Java String
 *         - Token -- an unforgeable value that can be created and passed around as proof of issuance
 *     - Num
 *         - Dec128 -- equivalent to the Java BigDecimal class
 *         - Flt64 -- equivalent to the Java Double class
 *             - Flt32 -- equivalent to the Java Float class
 *         - Int64 -- equivalent to the Java Long class
 *             - Int32 -- equivalent to the Java Integer class
 *                 - Char -- equivalent to the Java Character class
 *     - Method -- a function or a procedure
 *         - Func -- a method that takes zero or more parameters and returns a value
 *         - Proc -- a method that takes zero or more parameters and returns Void
 *     - Union -- a union of types
 *     - Void -- there are no instances of Void
 *
 * FEATURE TYPES
 *
 * - Value
 *     - Feature
 *         - Int32
 *         - Literal
 *
 * OBJECTS AND FUNCTIONS WITH PROTOCOL PARAMETERS
 *
 * - Value
 *     - Composite
 *         - Obj
 *             - ActorCfg[P <: Protocol]
 *             - ActorRef[P <: Protocol]
 *     - Method
 *         - Func
 *             - ActorCtor[P <: Protocol] -> ActorCfg[P]
 */
public interface Type extends Lang {

    String ACTOR_CFG = "ActorCfg";
    String ACTOR_CTOR = "ActorCtor";
    String ACTOR_REF = "ActorRef";

    static IdentAsType fromIdent(Ident ident) {
        return fromIdent(ident, SourceSpan.emptySourceSpan());
    }

    static IdentAsType fromIdent(Ident ident, SourceSpan sourceSpan) {
        return switch (ident.name) {
            case StrType.NAME -> StrType.create(sourceSpan);
            case BoolType.NAME -> BoolType.create(sourceSpan);
            case Int32Type.NAME -> Int32Type.create(sourceSpan);
            case Int64Type.NAME -> Int64Type.create(sourceSpan);
            case Flt32Type.NAME -> Flt32Type.create(sourceSpan);
            case Flt64Type.NAME -> Flt64Type.create(sourceSpan);
            case ArrayType.NAME -> ArrayType.create(sourceSpan);
            case RecType.NAME -> RecType.create(sourceSpan);
            case TupleType.NAME -> TupleType.create(sourceSpan);
            case ObjType.NAME -> ObjType.create(sourceSpan);
            case AnyType.NAME -> AnyType.create(sourceSpan);
            case NullType.NAME -> NullType.create(sourceSpan);
            case EofType.NAME -> EofType.create(sourceSpan);
            case TokenType.NAME -> TokenType.create(sourceSpan);
            case CharType.NAME -> CharType.create(sourceSpan);
            default -> IdentAsType.create(ident, sourceSpan);
        };
    }

}
