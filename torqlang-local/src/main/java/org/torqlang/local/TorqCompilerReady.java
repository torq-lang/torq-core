/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.util.MessageLevel;
import org.torqlang.util.SourceFileBroker;

import java.util.List;

public interface TorqCompilerReady {
    TorqCompilerReady setWorkspace(List<SourceFileBroker> fileBrokers);

    TorqCompilerParsed parse() throws Exception;

    TorqCompilerReady setLoggingLevel(MessageLevel loggingLevel);
}
