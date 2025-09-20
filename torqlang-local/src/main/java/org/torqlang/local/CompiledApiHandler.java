/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import java.util.ArrayList;
import java.util.List;

public final class CompiledApiHandler {
    private final String qualifiedName;
    private final List<CompiledApiRoute> routes = new ArrayList<>();

    private ActorImage actorImage;
    private CompiledMember member;

    CompiledApiHandler(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public final String qualifiedName() {
        return qualifiedName;
    }

    public final ActorImage actorImage() {
        return actorImage;
    }

    public final CompiledMember member() {
        return member;
    }

    public final List<CompiledApiRoute> routes() {
        return routes;
    }

    final void setActorImage(ActorImage actorImage) {
        this.actorImage = actorImage;
    }

    final void setMember(CompiledMember member) {
        this.member = member;
    }
}
