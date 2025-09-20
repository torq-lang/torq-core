/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import java.util.IdentityHashMap;

public interface PartialProc extends Partial, Proc {

    @Override
    default CompleteProc checkComplete() throws WaitVarException {
        throw new CannotCompleteError(this);
    }

    @Override
    default CompleteProc checkComplete(IdentityHashMap<Partial, Complete> memos) throws WaitVarException {
        throw new CannotCompleteError(this);
    }

}
