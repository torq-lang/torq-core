/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.lang.RecType;
import org.torqlang.lang.TupleType;
import org.torqlang.lang.Type;

public final class CompiledApiRoute {

    private final String pathExpr;

    private Type inputType;
    private Type outputType;
    private TupleType pathType;
    private RecType queryType;

    CompiledApiRoute(String pathExpr) {
        this.pathExpr = pathExpr;
    }

    public final Type inputType() {
        return inputType;
    }

    public final Type outputType() {
        return outputType;
    }

    public final String pathExpr() {
        return pathExpr;
    }

    public final TupleType pathType() {
        return pathType;
    }

    public final RecType queryType() {
        return queryType;
    }

    final void setInputType(Type inputType) {
        this.inputType = inputType;
    }

    final void setOutputType(Type outputType) {
        this.outputType = outputType;
    }

    final void setPathType(TupleType pathType) {
        this.pathType = pathType;
    }

    final void setQueryType(RecType queryType) {
        this.queryType = queryType;
    }
}
