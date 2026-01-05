/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.*;

import java.util.List;

import static org.torqlang.local.Envelope.createNotify;
import static org.torqlang.local.Envelope.createRequest;

// TODO: Bundle this into an ActorRefMod to be consistent with others, such as ArrayListMod?
public final class ActorRefObj implements CompleteObj {

    private final ActorRef referent;

    private final CompleteObjProcBinding<ActorRefObj> PROC_ASK = new CompleteObjProcBinding<>(this, ActorRefObj::objAsk);
    private final CompleteObjProcBinding<ActorRefObj> PROC_TELL = new CompleteObjProcBinding<>(this, ActorRefObj::objTell);

    public ActorRefObj(ActorRef referent) {
        this.referent = referent;
    }

    private static void objAsk(ActorRefObj obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 2;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "ActorRefObj.ask");
        }
        Value candidateMessage = ys.get(0).resolveValue(env);
        // This procedure will be suspended if 'checkComplete()' throws WaitException
        Complete message = candidateMessage.checkComplete();
        ValueOrVar responseTarget = ys.get(1).resolveValueOrVar(env);
        ActorRef owner = machine.owner();
        if (obj.referent == owner) {
            throw new SelfRefAskError(machine.current());
        }
        obj.referent.send(createRequest(message, owner, new ValueOrVarRef(responseTarget)));
    }

    private static void objTell(ActorRefObj obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 1;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "ActorRefObj.tell");
        }
        Value candidateMessage = ys.get(0).resolveValue(env);
        // This procedure will be suspended if 'checkComplete()' throws WaitException
        Complete message = candidateMessage.checkComplete();
        obj.referent.send(createNotify(message));
    }

    @Override
    public final Feature featureAt(int index) {
        if (index == 0) {
            return CommonFeatures.ASK;
        } else if (index == 1) {
            return CommonFeatures.TELL;
        } else {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
    }

    @Override
    public final CompleteField fieldAt(int index) {
        if (index == 0) {
            return new CompleteField(CommonFeatures.ASK, PROC_ASK);
        } else if (index == 1) {
            return new CompleteField(CommonFeatures.TELL, PROC_TELL);
        } else {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
    }

    @Override
    public final int fieldCount() {
        return 2;
    }

    @Override
    public final Literal label() {
        return Null.SINGLETON;
    }

    public final ActorRef referent() {
        return referent;
    }

    @Override
    public final Value select(Feature feature) {
        if (feature.equals(CommonFeatures.ASK)) {
            return PROC_ASK;
        } else if (feature.equals(CommonFeatures.TELL)) {
            return PROC_TELL;
        }
        throw new FeatureNotFoundError(this, feature);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

    @Override
    public final CompleteProc valueAt(int index) {
        if (index == 0) {
            return PROC_ASK;
        } else if (index == 1) {
            return PROC_TELL;
        } else {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
    }

}
