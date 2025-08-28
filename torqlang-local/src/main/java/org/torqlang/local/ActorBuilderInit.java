/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.CompleteOrIdent;
import org.torqlang.lang.ActorStmt;

import java.util.List;

public interface ActorBuilderInit extends ActorBuilder {

    static ActorBuilderInit create() {
        return new ActorBuilderInitImpl();
    }

    ActorImage actorImage(String source);

    Address address();

    ActorBuilderParsed setActorStmt(ActorStmt actorStmt);

    ActorBuilderInit setAddress(Address address);

    ActorBuilderReady setSource(String source);

    ActorBuilderInit setSystem(ActorSystem system);

    String source();

    ActorBuilderSpawned spawn(String source);

    ActorBuilderSpawned spawn(String source, List<? extends CompleteOrIdent> args);

    ActorSystem system();
}

final class ActorBuilderInitImpl implements ActorBuilderInit {

    private ActorSystem system;
    private Address address;
    private String source;

    ActorBuilderInitImpl() {
    }

    @Override
    public final ActorImage actorImage(String source) {
        this.source = source;
        return ActorBuilderReady.create(this).actorImage();
    }

    @Override
    public final Address address() {
        return address == null ? Address.UNDEFINED : address;
    }

    @Override
    public final ActorBuilderParsed setActorStmt(ActorStmt actorStmt) {
        return ActorBuilderReady.create(this).setActorStmt(actorStmt);
    }

    @Override
    public final ActorBuilderInit setAddress(Address address) {
        this.address = address;
        return this;
    }

    @Override
    public final ActorBuilderReady setSource(String source) {
        this.source = source;
        return ActorBuilderReady.create(this);
    }

    @Override
    public final ActorBuilderInit setSystem(ActorSystem system) {
        this.system = system;
        return this;
    }

    @Override
    public final String source() {
        return source;
    }

    @Override
    public final ActorBuilderSpawned spawn(String source) {
        this.source = source;
        return ActorBuilderReady.create(this).spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn(String source, List<? extends CompleteOrIdent> args) {
        this.source = source;
        return ActorBuilderReady.create(this).spawn(args);
    }

    @Override
    public final ActorSystem system() {
        return system == null ? ActorSystem.defaultSystem() : system;
    }

}
