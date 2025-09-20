/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

public final class FieldIterMod implements KernelModule {

    public static final Str FIELD_ITER_STR = Str.of("FieldIter");
    public static final Ident FIELD_ITER_IDENT = Ident.create(FIELD_ITER_STR.value);

    private final CompleteRec namesake;
    private final CompleteRec exports;

    private FieldIterMod() {
        namesake = Rec.completeRecBuilder()
            .addField(CommonFeatures.$NEW, (CompleteProc) FieldIterMod::clsNew)
            .build();
        exports = Rec.completeRecBuilder()
            .addField(FIELD_ITER_STR, namesake)
            .build();
    }

    public static FieldIterMod singleton() {
        return LazySingleton.SINGLETON;
    }

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "FieldIter.new");
        }
        Value source = ys.get(0).resolveValue(env);
        if (!(source instanceof FieldIterSource iterable)) {
            throw new IllegalArgumentException(ys.get(0) + " must be a type of " +
                FieldIterSource.class.getSimpleName());
        }
        ValueOrVar iter = iterable.fieldIter();
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValueOrVar(iter, null);
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
        return FIELD_ITER_IDENT;
    }

    private static final class LazySingleton {
        private static final FieldIterMod SINGLETON = new FieldIterMod();
    }
}
