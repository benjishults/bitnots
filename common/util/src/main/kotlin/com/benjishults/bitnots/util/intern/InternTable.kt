package com.benjishults.bitnots.util.intern

/**
 * Currently not thread safe.
 */
open class InternTable<C>(open val makeNew: (String) -> C) {

    protected val table = mutableMapOf<String, C>()

    fun clear() {
        table.clear()
    }

    fun exists(name: String): Boolean = table.containsKey(name)

    /**
     * If this name is on the intern table, return the C with that name.  Otherwise, create a new C with the given name.
     */
    fun intern(name: String): C {
        table.get(name)?.let {
            return it
        } ?: return makeNew(name).also {
            table.put(name, it)
        }
    }

    /**
     * If the given [baseName] is on the intern table, then a new, similar name is chosen that is not on the intern table.
     * A new C with that name is returned.  If [baseName] is not on the on the intern table, then a new C is created with that name and returned.
     */
    tailrec fun new(baseName: String): C {
        if (table.containsKey(baseName)) {
            val index = baseName.lastIndexOf('-')
            if (index >= 0) {
                val trailingInt = baseName.substring(index + 1).toIntOrNull()
                if (trailingInt === null) {
                    return new(baseName + "-0")
                } else {
                    return new("${baseName.substring(0, index)}-${trailingInt + 1}")
                }
            } else {
                return new(baseName + "-0")
            }
        } else {
            return intern(baseName)
        }
    }

}
