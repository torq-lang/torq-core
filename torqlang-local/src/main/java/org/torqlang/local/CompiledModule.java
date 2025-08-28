/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.Instr;
import org.torqlang.lang.ModuleStmt;
import org.torqlang.util.FileName;

import java.util.List;
import java.util.Objects;

public final class CompiledModule {
    private final String qualifiedName;
    private final String qualifier;
    private final List<FileName> absolutePath;
    private final List<FileName> qualifiedTorqFile;
    private final List<FileName> torqPackage;
    private final FileName torqFile;
    private final ModuleStmt moduleStmt;

    private Instr moduleInstr;
    private Instr moduleInstrEnhanced;

    private CompiledModule(String qualifiedName,
                           List<FileName> absolutePath,
                           List<FileName> qualifiedTorqFile,
                           List<FileName> torqPackage,
                           FileName torqFile,
                           ModuleStmt moduleStmt,
                           Instr moduleInstr)
    {
        this.qualifiedName = qualifiedName;
        this.qualifier = TorqCompiler.qualifierFrom(qualifiedName);
        this.absolutePath = absolutePath;
        this.qualifiedTorqFile = qualifiedTorqFile;
        this.torqPackage = torqPackage;
        this.torqFile = torqFile;
        this.moduleStmt = moduleStmt;
        this.moduleInstr = moduleInstr;
    }

    static CompiledModule createAfterParse(String qualifiedName,
                                           List<FileName> absolutePath,
                                           List<FileName> qualifiedTorqFile,
                                           List<FileName> torqPackage,
                                           FileName torqFile,
                                           ModuleStmt moduleStmt)
    {
        return new CompiledModule(qualifiedName, absolutePath, qualifiedTorqFile, torqPackage, torqFile, moduleStmt, null);
    }

    public final List<FileName> absolutePath() {
        return absolutePath;
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        var that = (CompiledModule) other;
        return Objects.equals(this.qualifiedName, that.qualifiedName);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(qualifiedName);
    }

    public final Instr moduleInstr() {
        return moduleInstr;
    }

    public final Instr moduleInstrEnhanced() {
        return moduleInstrEnhanced;
    }

    public final ModuleStmt moduleStmt() {
        return moduleStmt;
    }

    public final String qualifiedName() {
        return qualifiedName;
    }

    public final List<FileName> qualifiedTorqFile() {
        return qualifiedTorqFile;
    }

    public final String qualifier() {
        return qualifier;
    }

    final void setModuleInstr(Instr moduleInstr) {
        this.moduleInstr = moduleInstr;
    }

    final void setModuleInstrEnhanced(Instr moduleInstrEnhanced) {
        this.moduleInstrEnhanced = moduleInstrEnhanced;
    }

    public final FileName torqFile() {
        return torqFile;
    }

    public final List<FileName> torqPackage() {
        return torqPackage;
    }

    @Override
    public final String toString() {
        return qualifiedName;
    }
}
