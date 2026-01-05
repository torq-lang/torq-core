/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

public final class ValueIterMod implements KernelModule {

    public static final Str VALUE_ITER_STR = Str.of("ValueIter");
    public static final Ident VALUE_ITER_IDENT = Ident.create(VALUE_ITER_STR.value);

    private final CompleteRec namesake;
    private final CompleteRec exports;

    private ValueIterMod() {
        namesake = Rec.completeRecBuilder()
            .addField(CommonFeatures.$NEW, (CompleteProc) ValueIterMod::clsNew)
            .build();
        exports = Rec.completeRecBuilder()
            .addField(VALUE_ITER_STR, namesake)
            .build();
    }

    public static ValueIterMod singleton() {
        return LazySingleton.SINGLETON;
    }

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "ValueIter.new");
        }
        Value source = ys.get(0).resolveValue(env);
        if (!(source instanceof ValueIterSource iterable)) {
            throw new IllegalArgumentException(ys.get(0) + " must be a type of " +
                ValueIterSource.class.getSimpleName());
        }
        ValueOrVar iter = iterable.valueIter();
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
        return VALUE_ITER_IDENT;
    }

    private static final class LazySingleton {
        private static final ValueIterMod SINGLETON = new ValueIterMod();
    }
}
