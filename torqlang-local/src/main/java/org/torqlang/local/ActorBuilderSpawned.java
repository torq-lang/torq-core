/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.*;
import org.torqlang.lang.ActorStmt;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ActorBuilderSpawned extends ActorBuilder {

    static ActorBuilderSpawned create(ActorBuilderConfigured configured) {
        return new ActorBuilderSpawnedImpl(configured);
    }

    ActorCfg actorCfg();

    ActorImage actorImage();

    Rec actorRec();

    ActorRef actorRef();

    ActorStmt actorStmt();

    Address address();

    List<? extends CompleteOrIdent> args();

    LocalInstr enhancedCreateActorRecInstr();

    LocalInstr generatedCreateActorRecInstr();

    String source();

    ActorSystem system();
}

final class ActorBuilderSpawnedImpl implements ActorBuilderSpawned {

    private final Address address;
    private final String source;
    private final ActorSystem system;
    private final ActorStmt actorStmt;
    private final LocalInstr generatedCreateActorRecInstr;
    private final LocalInstr enhancedCreateActorRecInstr;
    private final List<? extends CompleteOrIdent> args;
    private final Rec actorRec;
    private final ActorCfg actorCfg;

    private ActorImage actorImage;
    private LocalActor localActor;

    ActorBuilderSpawnedImpl(ActorBuilderConfigured configured) {
        address = configured.address();
        source = configured.source();
        system = configured.system();
        actorStmt = configured.actorStmt();
        generatedCreateActorRecInstr = configured.generatedCreateActorRecInstr();
        enhancedCreateActorRecInstr = configured.enhancedCreateActorRecInstr();
        args = configured.args();
        actorRec = configured.actorRec();
        actorCfg = configured.actorCfg();
        transitionFromConfiguredToSpawned();
    }

    @Override
    public final ActorCfg actorCfg() {
        return actorCfg;
    }

    @Override
    public final ActorImage actorImage() {
        if (actorImage == null) {
            Object response;
            try {
                response = RequestClient.builder()
                    .setAddress(address())
                    .send(actorRef(), CaptureImage.SINGLETON)
                    .awaitResponse(10, TimeUnit.MILLISECONDS);
                if (response instanceof FailedValue failedValue) {
                    throw new IllegalStateException(failedValue.toString());
                }
            } catch (Exception exc) {
                throw new IllegalStateException(exc);
            }
            actorImage = (ActorImage) response;
        }
        return actorImage;
    }

    @Override
    public final Rec actorRec() {
        return actorRec;
    }

    @Override
    public final List<? extends CompleteOrIdent> args() {
        return args;
    }

    @Override
    public final ActorRef actorRef() {
        return localActor;
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
    public final ActorSystem system() {
        return system;
    }

    private void transitionFromConfiguredToSpawned() {
        localActor = new LocalActor(address(), system());
        localActor.configure(actorCfg());
    }

}