/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

/*
 * Ultimately, a protocol defines the messages exchanged between actors using an ActorRef object. As a general rule,
 * types do not support method overloading, but with protocols, we can get around that rule for ActorRef and overload
 * the ask, tell, and stream methods.
 *
 * Obtaining an ActorRef is a two-step process. First, we invoke an ActorCtor[P <: Protocol] to get an
 * ActorCfg[P <: Protocol]. Then, we spawn with the actor configuration to get an ActorRef[P <: Protocol]. To support
 * this process, protocols appear as type parameters on three built-in types:
 *
 *     1. Actor Constructor -- ActorCtor[P <: Protocol]
 *     2. Actor Config -- ActorCfg[P <: Protocol]
 *     3. Actor Reference -- ActorRef[P <: Protocol]
 */
public interface ProtocolType extends Type {
}
