/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.*;
import org.torqlang.lang.ActorStmt;
import org.torqlang.util.ListTools;
import org.torqlang.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.klvm.CommonFeatures.$NEW;

public interface ActorBuilderConfigured extends ActorBuilder {

    static ActorBuilderConfigured create(ActorBuilderConstructed constructed) {
        return new ActorBuilderConfiguredImpl(constructed);
    }

    ActorCfg actorCfg();

    ActorImage actorImage();

    Rec actorRec();

    ActorStmt actorStmt();

    Address address();

    List<? extends CompleteOrIdent> args();

    LocalInstr enhancedCreateActorRecInstr();

    LocalInstr generatedCreateActorRecInstr();

    String source();

    ActorBuilderSpawned spawn();

    ActorSystem system();
}

final class ActorBuilderConfiguredImpl implements ActorBuilderConfigured {

    private final Address address;
    private final String source;
    private final ActorSystem system;
    private final ActorStmt actorStmt;
    private final LocalInstr generatedCreateActorRecInstr;
    private final LocalInstr enhancedCreateActorRecInstr;
    private final List<? extends CompleteOrIdent> args;
    private final Rec actorRec;

    private ActorCfg actorCfg;

    ActorBuilderConfiguredImpl(ActorBuilderConstructed constructed) {
        address = constructed.address();
        source = constructed.source();
        system = constructed.system();
        actorStmt = constructed.actorStmt();
        generatedCreateActorRecInstr = constructed.generatedCreateActorRecInstr();
        enhancedCreateActorRecInstr = constructed.enhancedCreateActorRecInstr();
        actorRec = constructed.actorRec();
        args = constructed.args();
        transitionFromConstructedToConfigured();
    }

    @Override
    public final ActorCfg actorCfg() {
        return actorCfg;
    }

    @Override
    public final ActorImage actorImage() {
        return ActorBuilderSpawned.create(this).actorImage();
    }

    @Override
    public final Rec actorRec() {
        return actorRec;
    }

    @Override
    public final ActorStmt actorStmt() {
        return actorStmt;
    }

    @Override
    public final Address address() {
        return address;
    }

    @Override
    public final List<? extends CompleteOrIdent> args() {
        return args;
    }

    @Override
    public final LocalInstr enhancedCreateActorRecInstr() {
        return enhancedCreateActorRecInstr;
    }

    @Override
    public final LocalInstr generatedCreateActorRecInstr() {
        return generatedCreateActorRecInstr;
    }

    @Override
    public final String source() {
        return source;
    }

    @Override
    public final ActorBuilderSpawned spawn() {
        return ActorBuilderSpawned.create(this);
    }

    @Override
    public final ActorSystem system() {
        return system;
    }

    private void transitionFromConstructedToConfigured() {
        // The actor record will contain values (not vars). Therefore, we can access the ActorCtor directly.
        ActorCtor actorCtor = (ActorCtor) actorRec().findValue($NEW);
        Env env = Env.create(LocalActor.rootEnv(),
            List.of(
                new EnvEntry(Ident.$ACTOR_CTOR, new Var(actorCtor)),
                new EnvEntry(Ident.$R, new Var())
            )
        );
        List<CompleteOrIdent> argsWithTarget = ListTools.append(CompleteOrIdent.class, args(), Ident.$R);
        List<Instr> localInstrs = new ArrayList<>();
        localInstrs.add(new ApplyInstr(Ident.$ACTOR_CTOR, argsWithTarget, SourceSpan.emptySourceSpan()));
        SeqInstr seqInstr = new SeqInstr(localInstrs, SourceSpan.emptySourceSpan());
        ComputeTools.computeInstr(this, seqInstr, env);
        try {
            actorCfg = (ActorCfg) env.get(Ident.$R).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

}
