/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.*;
import org.torqlang.lang.ActorStmt;
import org.torqlang.lang.Generator;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.klvm.Ident.$MAIN;

public interface ActorBuilderGenerated extends ActorBuilder {

    static ActorBuilderGenerated create(ActorBuilderParsed parsed) {
        return new ActorBuilderGeneratedImpl(parsed);
    }

    ActorImage actorImage();

    ActorStmt actorStmt();

    Address address();

    ActorBuilderConstructed construct();

    LocalInstr enhancedCreateActorRecInstr();

    LocalInstr generatedCreateActorRecInstr();

    String source();

    ActorBuilderSpawned spawn();

    ActorBuilderSpawned spawn(List<? extends CompleteOrIdent> args);

    ActorSystem system();
}

final class ActorBuilderGeneratedImpl implements ActorBuilderGenerated {

    private final Address address;
    private final String source;
    private final ActorSystem system;
    private final ActorStmt actorStmt;

    private LocalInstr generatedCreateActorRecInstr;
    private LocalInstr enhancedCreateActorRecInstr;

    ActorBuilderGeneratedImpl(ActorBuilderParsed parsed) {
        this.address = parsed.address();
        this.source = parsed.source();
        this.system = parsed.system();
        this.actorStmt = parsed.actorStmt();
        transitionFromParsedToGenerated();
    }

    @Override
    public final ActorImage actorImage() {
        return ActorBuilderConstructed.create(this).actorImage();
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
        return ActorBuilderConstructed.create(this);
    }

    private LocalInstr enhanceCreateActorRecInstr() {
        Instr bodyInstr = generatedCreateActorRecInstr.body;
        List<Instr> enhancedBodyInstrs = new ArrayList<>();
        // Inject a bind instruction so that we can retrieve the actor constructor
        enhancedBodyInstrs.add(new BindIdentToIdentInstr($MAIN, actorStmt().name.ident, bodyInstr.toSourceBegin()));
        if (bodyInstr instanceof SeqInstr seqInstr) {
            enhancedBodyInstrs.addAll(seqInstr.list);
        } else {
            enhancedBodyInstrs.add(bodyInstr);
        }
        return new LocalInstr(generatedCreateActorRecInstr.xs,
            new SeqInstr(enhancedBodyInstrs, bodyInstr),
            generatedCreateActorRecInstr.sourceSpan);
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
        return ActorBuilderConstructed.create(this).spawn();
    }

    @Override
    public final ActorBuilderSpawned spawn(List<? extends CompleteOrIdent> args) {
        return ActorBuilderConstructed.create(this, args).spawn();
    }

    @Override
    public final ActorSystem system() {
        return system;
    }

    private void transitionFromParsedToGenerated() {
        Generator g = new Generator();
        generatedCreateActorRecInstr = (LocalInstr) g.acceptStmt(actorStmt());
        enhancedCreateActorRecInstr = enhanceCreateActorRecInstr();
    }

}