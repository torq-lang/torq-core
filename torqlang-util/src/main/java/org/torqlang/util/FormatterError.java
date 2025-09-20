/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

public class FormatterError extends RuntimeException {

    public FormatterError() {
    }

    public FormatterError(String message) {
        super(message);
    }

    public FormatterError(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatterError(Throwable cause) {
        super(cause);
    }

}
