/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

/*
 * The arity of a composite structure is its list of sorted features. An index into a composite structure is an index
 * into the list of sorted features. The featureAt method gets you the feature at that index, valueAt gets you the
 * value at that index, and fieldAt gets you the feature-value pair at that index.
 *
 * The sort order for features is first by type, then by value within the type. The sort order for types is Str, Int,
 * Bool, Eof, Null, Token. Strings values are sorted in lexicographic order, integers are in ascending order, booleans
 * are in false, true order, and tokens are in ID order.
 */
public interface Composite extends Value {

    /*
     * Throw IndexOutOfBoundsException if not found.
     */
    Feature featureAt(int index);

    /*
     * Throw IndexOutOfBoundsException if not found.
     */
    Field fieldAt(int index);

    int fieldCount();

    Literal label();

    /*
     * Throw FeatureNotFoundError if not found.
     */
    ValueOrVar select(Feature feature) throws WaitException;

    /*
     * Throw IndexOutOfBoundsException if not found.
     */
    ValueOrVar valueAt(int index);

}
