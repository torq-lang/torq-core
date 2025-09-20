/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.*;
import org.torqlang.lang.ActorStmt;

import java.util.List;

import static org.torqlang.klvm.Ident.$MAIN;

public interface ActorBuilderConstructed extends ActorBuilder {

    static ActorBuilderConstructed create(ActorBuilderGenerated generated) {
        return new ActorBuilderConstructedImpl(generated, null, null);
    }

    static ActorBuilderConstructed create(ActorBuilderGenerated generated, Rec actorRec) {
        return new ActorBuilderConstructedImpl(generated, actorRec, null);
    }

    static ActorBuilderConstructed create(ActorBuilderGenerated generated, List<? extends CompleteOrIdent> args) {
        return new ActorBuilderConstructedImpl(generated, null, args);
    }

    ActorImage actorImage();

    Rec actorRec();

    ActorStmt actorStmt();

    Address address();

    List<? extends CompleteOrIdent> args();

    ActorBuilderConfigured configure() throws Exception;

    ActorBuilderConfigured configure(List<? extends CompleteOrIdent> args) throws Exception;

    LocalInstr enhancedCreateActorRecInstr();

    LocalInstr generatedCreateActorRecInstr();

    String source();

    ActorBuilderSpawned spawn();

    ActorSystem system();
}

final class ActorBuilderConstructedImpl implements ActorBuilderConstructed {

    private final Address address;
    private final String source;
    private final ActorSystem system;
    private final ActorStmt actorStmt;
    private final LocalInstr generatedCreateActorRecInstr;
    private final LocalInstr enhancedCreateActorRecInstr;

    private List<? extends CompleteOrIdent> args;
    private Rec actorRec;

    ActorBuilderConstructedImpl(ActorBuilderGenerated generated, Rec actorRec, List<? extends CompleteOrIdent> args) {
        this.address = generated.address();
        this.source = generated.source();
        this.system = generated.system();
        this.actorStmt = generated.actorStmt();
        this.generatedCreateActorRecInstr = generated.generatedCreateActorRecInstr();
        this.enhancedCreateActorRecInstr = generated.enhancedCreateActorRecInstr();
        transitionFromGeneratedToConstructed(actorRec, args);
    }

    @Override
    public final ActorImage actorImage() {
        return ActorBuilderConfigured.create(this).actorImage();
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
        return args == null ? List.of() : args;
    }

    @Override
    public final ActorBuilderConfigured configure() {
        return ActorBuilderConfigured.create(this);
    }

    @Override
    public final ActorBuilderConfigured configure(List<? extends CompleteOrIdent> args) {
        this.args = args == null ? List.of() : args;
        return ActorBuilderConfigured.create(this);
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
        return ActorBuilderConfigured.create(this).spawn();
    }

    @Override
    public final ActorSystem system() {
        return system;
    }

    private void transitionFromGeneratedToConstructed(Rec existingActorRec, List<? extends CompleteOrIdent> existingArgs) {
        if (existingActorRec != null) {
            this.actorRec = existingActorRec;
            return;
        }
        if (existingArgs != null) {
            this.args = existingArgs;
        }
        Env env = Env.create(LocalActor.rootEnv(), new EnvEntry($MAIN, new Var()));
        ComputeTools.computeInstr(this, enhancedCreateActorRecInstr(), env);
        try {
            actorRec = (Rec) env.get($MAIN).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

}