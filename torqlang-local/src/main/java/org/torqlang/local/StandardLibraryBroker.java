/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.util.FileName;
import org.torqlang.util.FileType;
import org.torqlang.util.ResourceFileBroker;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.torqlang.util.ResourceFileBroker.Entry;

public final class StandardLibraryBroker {

    private static final List<Entry> CONTENT = List.of(
        new Entry(new FileName(FileType.FOLDER, "torq"), List.of(
            new Entry(new FileName(FileType.FOLDER, "lang"), List.of(
                new Entry(new FileName(FileType.SOURCE, "Cell.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "FieldIter.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Int32.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Int64.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "RangeIter.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Rec.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Str.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Token.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "ValueIter.torq"), null)
            )),
            new Entry(new FileName(FileType.FOLDER, "util"), List.of(
                new Entry(new FileName(FileType.SOURCE, "ArrayList.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Criteria.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "HashMap.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "LocalDate.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Message.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "StringBuilder.torq"), null),
                new Entry(new FileName(FileType.SOURCE, "Timer.torq"), null)
            ))
        ))
    );

    private final ResourceFileBroker broker;

    private StandardLibraryBroker() {
        broker = new ResourceFileBroker(MethodHandles.lookup().lookupClass(), List.of(List.of()), CONTENT);
    }

    public static ResourceFileBroker get() {
        return LazySingleton.SINGLETON.broker;
    }

    private static final class LazySingleton {
        private static final StandardLibraryBroker SINGLETON = new StandardLibraryBroker();
    }

}
