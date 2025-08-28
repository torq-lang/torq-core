/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.CompleteOrIdent;
import org.torqlang.lang.ActorStmt;
import org.torqlang.lang.Parser;

import java.util.List;

public interface ActorBuilderParsed extends ActorBuilder {

    static ActorBuilderParsed create(ActorBuilderReady ready) {
        return new ActorBuilderParsedImpl(ready, null);
    }

    static ActorBuilderParsed create(ActorBuilderReady ready, ActorStmt actorStmt) {
        return new ActorBuilderParsedImpl(ready, actorStmt);
    }

    ActorImage actorImage();

    ActorStmt actorStmt();

    Address address();

    ActorBuilderConstructed construct();

    ActorBuilderGenerated generate();

    String source();

    ActorBuilderSpawned spawn();

    ActorBuilderSpawned spawn(List<? extends CompleteOrIdent> args);

    ActorSystem system();
}

final class ActorBuilderParsedImpl implements ActorBuilderParsed {

    private final Address address;
    private final String source;
    private final ActorSystem system;

    private ActorStmt actorStmt;

    ActorBuilderParsedImpl(ActorBuilderReady ready, ActorStmt actorStmt) {
        address = ready.address();
        source = ready.source();
        system = ready.system();
        transitionFromReadyToParsed(actorStmt);
    }

    @Override
    public final ActorImage actorImage() {
        return ActorBuilderGenerated.create(this).actorImage();
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
    public final ActorBuilderConstructed construct() {
        return ActorBuilderGenerated.create(this).construct();
    }

    @Override
    public final ActorBuilderGenerated generate() {
        return ActorBuilderGenerated.create(this);
    }

    @Override
    public final String source() {
        return source;
    }

    @Override
    public final ActorBuilderSpawned spawn() {
        return ActorBuilderGenerated.create(this).spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn(List<? extends CompleteOrIdent> args) {
        return ActorBuilderGenerated.create(this).spawn(args);
    }

    @Override
    public final ActorSystem system() {
        return system;
    }

    private void transitionFromReadyToParsed(ActorStmt existingActorStmt) {
        if (existingActorStmt != null) {
            this.actorStmt = existingActorStmt;
            return;
        }
        Parser p = new Parser(source());
        actorStmt = (ActorStmt) p.parse();
    }

}