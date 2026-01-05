/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

public final class CellMod implements KernelModule {

    public static final Str CELL_STR = Str.of("Cell");
    public static final Ident CELL_IDENT = Ident.create(CELL_STR.value);

    private final CompleteRec namesake;
    private final CompleteRec exports;

    private CellMod() {
        namesake = Rec.completeRecBuilder()
            .addField(CommonFeatures.$NEW, (CompleteProc) CellMod::clsNew)
            .build();
        exports = Rec.completeRecBuilder()
            .addField(CELL_STR, namesake)
            .build();
    }

    public static CellMod singleton() {
        return LazySingleton.SINGLETON;
    }

    // Signatures:
    //     new Cell(initial::Value) -> Cell
    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Cell.new");
        }
        ValueOrVar initial = ys.get(0).resolveValueOrVar(env);
        CellObj cellObj = new CellObj(initial);
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValue(cellObj, null);
    }

    @Override
    public final CompleteRec exports() {
        return exports;
    }

    @Override
    public final CompleteRec namesake() {
        return namesake;
    }

    @Override
    public final Ident namesakeIdent() {
        return CELL_IDENT;
    }

    static final class CellObj implements PartialObj {

        private static final PartialObjProcTable<CellObj> objProcTable = PartialObjProcTable.<CellObj>builder()
            .build();

        private ValueOrVar valueOrVar;

        CellObj(ValueOrVar valueOrVar) {
            this.valueOrVar = valueOrVar;
        }

        @Override
        public final Feature featureAt(int index) {
            return objProcTable.featureAt(index);
        }

        @Override
        public final PartialField fieldAt(int index) {
            return objProcTable.fieldAt(index);
        }

        @Override
        public final int fieldCount() {
            return objProcTable.fieldCount();
        }

        final ValueOrVar get() {
            return valueOrVar;
        }

        @Override
        public final boolean isValidKey() {
            return true;
        }

        @Override
        public final Literal label() {
            return Null.SINGLETON;
        }

        @Override
        public final ValueOrVar select(Feature feature) {
            return objProcTable.selectAndBind(this, feature);
        }

        final ValueOrVar set(ValueOrVar valueOrVar) {
            ValueOrVar previous = this.valueOrVar;
            this.valueOrVar = valueOrVar;
            return previous;
        }

        @Override
        public final String toString() {
            return toKernelString();
        }

        @Override
        public final Proc valueAt(int index) {
            return objProcTable.valueAt(this, index);
        }
    }

    private static final class LazySingleton {
        private static final CellMod SINGLETON = new CellMod();
    }

}
