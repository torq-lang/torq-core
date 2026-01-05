/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.List;

public final class TokenMod implements KernelModule {

    public static final Str TOKEN_STR = Str.of("Token");
    public static final Ident TOKEN_IDENT = Ident.create(TOKEN_STR.value);

    private final CompleteRec namesake;
    private final CompleteRec exports;

    private TokenMod() {
        namesake = Rec.completeRecBuilder()
            .addField(CommonFeatures.$NEW, (CompleteProc) TokenMod::clsNew)
            .build();
        exports = Rec.completeRecBuilder()
            .addField(TOKEN_STR, namesake)
            .build();
    }

    public static TokenMod singleton() {
        return LazySingleton.SINGLETON;
    }

    // Signatures:
    //     new Token -> Token
    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 1;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Token.new");
        }
        Token token = new Token();
        ValueOrVar target = ys.get(0).resolveValueOrVar(env);
        target.bindToValue(token, null);
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
        return TOKEN_IDENT;
    }

    private static final class LazySingleton {
        private static final TokenMod SINGLETON = new TokenMod();
    }
}
