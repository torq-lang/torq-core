/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

import org.torqlang.util.BinarySearchTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CompleteObjProcTable<T extends CompleteObj> implements ObjProcTable<T> {

    private final Entry<T>[] entries;

    @SuppressWarnings("unchecked")
    private CompleteObjProcTable(List<Entry<T>> entries) {
        this.entries = (Entry<T>[]) entries.toArray(new Entry[0]);
        Arrays.sort(this.entries, (a, b) -> FeatureComparator.SINGLETON.compare(a.feature, b.feature));
    }

    public static <T extends CompleteObj> Builder<T> builder() {
        return new Builder<>();
    }

    @Override
    public final Feature featureAt(int index) {
        return entries[index].feature;
    }

    @Override
    public final CompleteField fieldAt(int index) {
        Entry<T> entry = entries[index];
        return new CompleteField(entry.feature, (CompleteProc) entry.objProc);
    }

    @Override
    public final int fieldCount() {
        return entries.length;
    }

    @Override
    public final CompleteObjProcBinding<T> selectAndBind(T target, Feature selector) {
        int index = BinarySearchTools.search(entries, e -> FeatureComparator.SINGLETON.compare(selector, e.feature));
        if (index < 0) {
            throw new FeatureNotFoundError(target, selector);
        }
        return new CompleteObjProcBinding<>(target, entries[index].objProc);
    }

    @Override
    public final CompleteProc valueAt(T target, int index) {
        Feature feature = entries[index].feature;
        return selectAndBind(target, feature);
    }

    public static class Builder<T extends CompleteObj> {
        private final List<Entry<T>> entries = new ArrayList<>();

        private Builder() {
        }

        public Builder<T> addEntry(Feature feature, CompleteObjProc<T> objProc) {
            entries.add(new Entry<>(feature, objProc));
            return this;
        }

        public CompleteObjProcTable<T> build() {
            return new CompleteObjProcTable<>(entries);
        }
    }

    private record Entry<T extends CompleteObj>(Feature feature, CompleteObjProc<T> objProc) {
    }

}
