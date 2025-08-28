/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

/*
 * Object procedure tables are immutable and global to all instances of its class. A class will create just one table
 * for all of its instances. When a caller selects an object feature, the object uses the procedure table to create a
 * binding.
 *
 * Noteworthy:
 *   1. There is no procedure table overhead when you instantiate an object since the table is created once for the
 *      class. Therefore, fine-grained types like Str can be implemented as objects without a performance impact.
 *   2. Selecting a feature creates and returns a bound procedure -- an ObjProcBinding.
 *   3. A bound procedure can be held and passed around like any other procedure. In essence, the fact that a procedure
 *      is bound to an object is irrelevant.
 *   4. Repeatedly selecting the same feature on an object can be optimized by reusing the bound procedure. This is
 *      possible because procedure tables are immutable and bound procedures are immutable. In essence, repeatedly
 *      selecting a feature always returns equivalent results.
 */
public interface ObjProcTable<T extends Obj> {

    Feature featureAt(int index);

    Field fieldAt(int index);

    int fieldCount();

    ObjProcBinding<T> selectAndBind(T target, Feature selector);

    Proc valueAt(T target, int index);

}
