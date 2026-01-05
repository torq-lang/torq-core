/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.CompleteRec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CompiledPackage {
    private final String qualifier;
    private final List<CompiledMember> members = new ArrayList<>();

    private CompleteRec packageRec;

    CompiledPackage(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        var that = (CompiledPackage) other;
        return Objects.equals(this.qualifier, that.qualifier);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(qualifier);
    }

    public final List<CompiledMember> members() {
        return members;
    }

    public final CompleteRec packageRec() {
        return packageRec;
    }

    public final String qualifier() {
        return qualifier;
    }

    final void setPackageRec(CompleteRec packageRec) {
        this.packageRec = packageRec;
    }

    @Override
    public final String toString() {
        return qualifier;
    }
}
