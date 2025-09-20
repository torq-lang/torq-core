/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.CompleteOrIdent;
import org.torqlang.lang.ActorStmt;

import java.util.List;

public interface ActorBuilderReady extends ActorBuilder {
    static ActorBuilderReady create(ActorBuilderInit init) {
        return new ActorBuilderReadyImpl(init);
    }

    ActorImage actorImage();

    Address address();

    ActorBuilderConstructed construct();

    ActorBuilderGenerated generate();

    ActorBuilderParsed parse();

    String source();

    ActorBuilderParsed setActorStmt(ActorStmt actorStmt);

    ActorBuilderSpawned spawn();

    ActorBuilderSpawned spawn(List<? extends CompleteOrIdent> args);

    ActorSystem system();
}

final class ActorBuilderReadyImpl implements ActorBuilderReady {

    private final Address address;
    private final String source;
    private final ActorSystem system;

    ActorBuilderReadyImpl(ActorBuilderInit init) {
        this.address = init.address();
        this.source = init.source();
        this.system = init.system();
    }

    @Override
    public final ActorImage actorImage() {
        return ActorBuilderParsed.create(this).actorImage();
    }

    @Override
    public final Address address() {
        return address;
    }

    @Override
    public final ActorBuilderConstructed construct() {
        return ActorBuilderParsed.create(this).construct();
    }

    @Override
    public final ActorBuilderGenerated generate() {
        return ActorBuilderParsed.create(this).generate();
    }

    @Override
    public final ActorBuilderParsed parse() {
        return ActorBuilderParsed.create(this);
    }

    @Override
    public final String source() {
        return source;
    }

    @Override
    public final ActorBuilderParsed setActorStmt(ActorStmt actorStmt) {
        return ActorBuilderParsed.create(this, actorStmt);
    }

    @Override
    public final ActorBuilderSpawned spawn() {
        return ActorBuilderParsed.create(this).spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn(List<? extends CompleteOrIdent> args) {
        return ActorBuilderParsed.create(this).spawn(args);
    }

    @Override
    public final ActorSystem system() {
        return system;
    }

}