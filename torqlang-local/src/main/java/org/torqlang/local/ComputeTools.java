/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.*;

final class ComputeTools {

    private static final int TIME_SLICE_10_000 = 10_000;

    static void computeInstr(Object owner, Instr instr, Env env) {
        Stack stack;
        if (DebuggerSetting.get() != null) {
            DebugInstr debugInstr = new DebugInstr(DebuggerSetting.get(), instr, env, instr);
            stack = new Stack(debugInstr, Env.emptyEnv(), null);
        } else {
            stack = new Stack(instr, env, null);
        }
        Machine.compute(owner, stack, TIME_SLICE_10_000);
    }
}
