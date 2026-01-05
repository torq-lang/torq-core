/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

public final class RecMod implements KernelModule {

    public static final Str REC_STR = Str.of("Rec");
    public static final Ident REC_IDENT = Ident.create(REC_STR.value);

    private final CompleteRec namesake;
    private final CompleteRec exports;

    private RecMod() {
        namesake = Rec.completeRecBuilder()
            .addField(CommonFeatures.ASSIGN, (CompleteProc) RecMod::clsAssign)
            .addField(CommonFeatures.SIZE, (CompleteProc) RecMod::clsSize)
            .build();
        exports = Rec.completeRecBuilder()
            .addField(REC_STR, namesake)
            .build();
    }

    public static RecMod singleton() {
        return LazySingleton.SINGLETON;
    }

    /*
     * Rec.assign(from::Rec, to::Rec) -> Rec
     *
     * Assign fields from the left argument to the right argument, which effectively adds or replaces fields on the
     * right side.
     *
     * var from = { b: 4, c: 5 }
     * var to = { a: 1, b: 2 }
     *
     * Rec.assign(from, to, result) // result = { a: 1, b: 4, c: 5 }
     */
    static void clsAssign(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 3;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Rec.assign");
        }
        PartialRecBuilder builder = Rec.partialRecBuilder();
        Rec rec0 = (Rec) ys.get(0).resolveValue(env);
        rec0.checkDetermined();
        for (int i = 0; i < rec0.fieldCount(); i++) {
            builder.addField(rec0.featureAt(i), rec0.valueAt(i));
        }
        Rec rec1 = (Rec) ys.get(1).resolveValue(env);
        rec1.checkDetermined();
        for (int i = 0; i < rec1.fieldCount(); i++) {
            Feature rec1Feat = rec1.featureAt(i);
            if (rec0.findValue(rec1Feat) == null) {
                builder.addField(rec1Feat, rec1.valueAt(i));
            }
        }
        Rec assigned = builder.build();
        ValueOrVar target = ys.get(2).resolveValueOrVar(env);
        target.bindToValue(assigned, null);
    }

    // Signatures:
    //    Rec.size(rec::Rec) -> Int32
    static void clsSize(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Rec.size");
        }
        Rec rec0 = (Rec) ys.get(0).resolveValue(env);
        rec0.checkDetermined();
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValue(Int32.of(rec0.fieldCount()), null);
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
        return REC_IDENT;
    }

    private static final class LazySingleton {
        private static final RecMod SINGLETON = new RecMod();
    }
}
