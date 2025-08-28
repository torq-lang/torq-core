/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CompiledMember {
    private final String qualifiedName;
    private final String name;
    private final String qualifier;
    private final List<CompiledModule> whereUsed;

    private CompiledModule module;
    private CompiledExport export;
    private boolean apiHandler;

    CompiledMember(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.name = TorqCompiler.nameFrom(qualifiedName);
        this.qualifier = TorqCompiler.qualifierFrom(qualifiedName);
        this.whereUsed = new ArrayList<>();
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        var that = (CompiledMember) other;
        return Objects.equals(this.qualifiedName, that.qualifiedName);
    }

    public final CompiledExport export() {
        return export;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(qualifiedName);
    }

    public final boolean isApiHandler() {
        return apiHandler;
    }

    public final CompiledModule module() {
        return module;
    }

    public final String name() {
        return name;
    }

    public final String qualifiedName() {
        return qualifiedName;
    }

    public final String qualifier() {
        return qualifier;
    }

    final void setApiHandler(boolean apiHandler) {
        this.apiHandler = apiHandler;
    }

    final void setExport(CompiledExport export) {
        this.export = export;
    }

    final void setModule(CompiledModule module) {
        this.module = module;
    }

    @Override
    public final String toString() {
        return qualifiedName;
    }

    public final List<CompiledModule> whereUsed() {
        return whereUsed;
    }
}
