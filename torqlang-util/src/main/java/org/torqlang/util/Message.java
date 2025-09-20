/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.util.Objects;

/*
 * A general message structure compatible with OpenAPI best practices. If possible, users should use the types defined
 * in MessageLevel. However, the type field is not constrained to a particular domain of values so that messages can be
 * adapted to existing systems.
 *
 * The standard message fields:
 *     name         - a name that usually correlates with the qualified name of the exception class
 *     type         - an ID that usually maps to one of the standard types: ERROR, WARN, INFO, DEBUG, TRACE
 *     message      - the message text
 *     details      - an optional detailed explanation of the message
 *     traceId      - an optional ID that can map back to trace details
 *     traceDetails - trace details not communicated to the end user but stored on the server
 */
public interface Message {

    static Message create(String name, String type, String message, String details, String traceId, String traceDetails) {
        return new MessageImpl(name, type, message, details, traceId, traceDetails);
    }

    static Message create(String name, MessageLevel type, String message, String details, String traceId, String traceDetails) {
        return new MessageImpl(name, type.name(), message, details, traceId, traceDetails);
    }

    static Message create(String name, MessageLevel type, String message) {
        return new MessageImpl(name, type.name(), message, null, null, null);
    }

    String details();

    String traceDetails();

    String traceId();

    void log(Logger logger);

    String message();

    String name();

    String type();
}

record MessageImpl(String name, String type, String message, String details, String traceId, String traceDetails)
    implements Message
{

    MessageImpl {
        Objects.requireNonNull(name);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        MessageImpl that = (MessageImpl) other;
        return Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, message);
    }

    public final void log(Logger logger) {
        String text = LoggerTools.formatter().apply(type, null, message);
        logger.log(text);
    }

}